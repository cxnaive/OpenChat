package com.example.messageservice.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import com.example.messageservice.MessageServicePlugin;

import java.sql.*;
import java.util.logging.Level;

/**
 * 数据库管理器
 * 管理数据库连接池和表结构
 * 仅支持 MySQL 数据库（跨服同步需要共享数据库）
 * 
 * 参照 FoliaShop 实现：
 * - 事务支持
 * - 行锁（FOR UPDATE）
 * - DriverShim 包装类
 * - 驱动重定位
 * - 资源管理
 */
public class DatabaseManager {

    private final MessageServicePlugin plugin;
    private HikariDataSource dataSource;
    private DatabaseQueue databaseQueue;

    public DatabaseManager(MessageServicePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 初始化数据库
     */
    public boolean initialize() {
        // 先关闭可能存在的旧连接（处理 PlugMan 重载情况）
        shutdown();

        // 保存原始 ClassLoader
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            // 使用插件的 ClassLoader 作为上下文 ClassLoader
            // 这样 HikariCP 就能找到打包的 MySQL 驱动
            Thread.currentThread().setContextClassLoader(plugin.getClass().getClassLoader());

            // 初始化连接池
            if (!initDataSource()) {
                plugin.getLogger().severe("MySQL 数据库连接失败！跨服同步功能需要 MySQL 数据库支持。");
                plugin.getLogger().severe("请检查 config.yml 中的数据库配置是否正确。");
                return false;
            }

            // 初始化数据库队列
            databaseQueue = new DatabaseQueue(plugin);
            databaseQueue.start();

            // 创建表结构
            createTables();

            plugin.getLogger().info("数据库初始化成功");
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "数据库初始化失败", e);
            return false;
        } finally {
            // 恢复原始 ClassLoader
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    /**
     * 关闭数据库
     * 参照 FoliaShop 的资源管理实现
     */
    public void shutdown() {
        if (databaseQueue != null) {
            databaseQueue.stop();
        }
        if (dataSource != null && !dataSource.isClosed()) {
            // 关闭连接池
            dataSource.close();

            // MySQL 数据库在关闭后需要一点时间完全释放资源
            // PlugMan 重载时需要确保资源被释放
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {}

            // 强制触发 GC 以清理未关闭的资源引用
            System.gc();

            // 再等待一段时间确保资源被释放
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}

            // 尝试注销驱动（解决 PlugMan 重载时的驱动冲突）
            try {
                // 使用插件 ClassLoader 查找驱动类并注销
                ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
                java.util.Enumeration<Driver> drivers = DriverManager.getDrivers();
                while (drivers.hasMoreElements()) {
                    Driver driver = drivers.nextElement();
                    // 检查驱动是否来自此插件的 ClassLoader
                    if (driver.getClass().getClassLoader() == pluginClassLoader) {
                        DriverManager.deregisterDriver(driver);
                    }
                }
            } catch (Exception ignored) {
                // 驱动可能不存在或未注册，忽略错误
            }
        }
        plugin.getLogger().info("数据库已关闭");
    }

    /**
     * 初始化数据源（仅支持 MySQL）
     */
    private boolean initDataSource() {
        // 读取 OpenChat 的 storage.yml 配置（PlayerChat 标准）
        org.bukkit.configuration.file.FileConfiguration storageConfig =
                cn.handyplus.lib.constants.BaseConstants.STORAGE_CONFIG;
        if (storageConfig == null) {
            plugin.getLogger().severe("storage.yml 未加载！");
            return false;
        }

        String storageMethod = storageConfig.getString("storage-method", "SQLite");
        if (!"MySQL".equalsIgnoreCase(storageMethod)) {
            plugin.getLogger().warning("storage-method 不是 MySQL，公告数据库功能需要 MySQL");
            return false;
        }

        return initMySQLDataSource(storageConfig);
    }

    /**
     * 初始化 MySQL 数据源
     * 参照 FoliaShop 的驱动注册实现
     */
    private boolean initMySQLDataSource(org.bukkit.configuration.file.FileConfiguration storageConfig) {
        try {
            // 使用标准 MySQL 驱动（OpenChat 的 shade 不重定位 MySQL）
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("cn.handyplus.chat.libs.com.mysql.cj.jdbc.Driver");

            String host = storageConfig.getString("MySQL.Host", "localhost");
            int port = storageConfig.getInt("MySQL.Port", 3306);
            String database = storageConfig.getString("MySQL.Database", "mc");
            String username = storageConfig.getString("MySQL.User", "root");
            String password = storageConfig.getString("MySQL.Password", "");

            config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true",
                    host, port, database));
            config.setUsername(username);
            config.setPassword(password);

            config.setMaximumPoolSize(storageConfig.getInt("MySQL.maximumPoolSize", 10));
            config.setMinimumIdle(2);
            config.setConnectionTimeout(5000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            plugin.getLogger().info("MySQL 数据库连接池已创建");
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "MySQL 数据库连接失败", e);
            plugin.getLogger().severe("请检查 storage.yml 中 MySQL 配置");
            return false;
        }
    }

    /**
     * JDBC 驱动包装类，用于在 Shade 打包后正确注册驱动
     * 参照 FoliaShop 的 DriverShim 实现
     */
    private static class DriverShim implements Driver {
        private final Driver driver;

        DriverShim(Driver driver) {
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, java.util.Properties info) throws SQLException {
            return driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) throws SQLException {
            return driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public java.util.logging.Logger getParentLogger() {
            return java.util.logging.Logger.getLogger(DriverShim.class.getName());
        }
    }

    /**
     * 创建表结构
     */
    private void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 创建公告表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS announcements (
                    id VARCHAR(64) PRIMARY KEY,
                    enabled BOOLEAN DEFAULT TRUE,
                    priority INT DEFAULT 1,
                    content TEXT NOT NULL,
                    display_type VARCHAR(32) DEFAULT 'CHAT',
                    fade_in INT DEFAULT 10,
                    stay INT DEFAULT 70,
                    fade_out INT DEFAULT 20,
                    color VARCHAR(32),
                    style VARCHAR(32),
                    toast_icon VARCHAR(256),
                    bossbar_progress BOOLEAN DEFAULT TRUE,
                    servers TEXT,
                    target_type VARCHAR(32) DEFAULT 'ALL',
                    target_value VARCHAR(256) DEFAULT '*',
                    trigger_type VARCHAR(32) DEFAULT 'MANUAL',
                    trigger_value VARCHAR(256),
                    global_cooldown INT DEFAULT 0,
                    player_cooldown INT DEFAULT 0,
                    version INT DEFAULT 1,
                    updated_at BIGINT DEFAULT 0,
                    updated_by VARCHAR(64) DEFAULT '',
                    created_at BIGINT DEFAULT 0,
                    server_id VARCHAR(64) DEFAULT ''
                )
            """);

            // 添加缺失的字段（用于现有表的升级）
            addColumnIfNotExists(stmt, "announcements", "version", "INT DEFAULT 1");
            addColumnIfNotExists(stmt, "announcements", "updated_by", "VARCHAR(64) DEFAULT ''");

            // 创建索引（捕获异常避免重复创建错误）
            createIndexIfNotExists(stmt, "idx_announcements_enabled", "announcements", "enabled");
            createIndexIfNotExists(stmt, "idx_announcements_updated", "announcements", "updated_at");
            createIndexIfNotExists(stmt, "idx_announcements_server", "announcements", "server_id");

            plugin.getLogger().info("数据库表结构检查完成");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "创建数据库表失败", e);
        }
    }

    /**
     * 安全地创建索引（如果索引已存在则忽略错误）
     */
    private void createIndexIfNotExists(Statement stmt, String indexName, String tableName, String columnName) {
        try {
            stmt.execute("CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")");
        } catch (SQLException e) {
            // 索引已存在或其他错误，忽略
            if (e.getMessage().contains("Duplicate key") || e.getMessage().contains("already exists")) {
                plugin.getLogger().fine("索引已存在: " + indexName);
            } else {
                plugin.getLogger().log(Level.WARNING, "创建索引失败: " + indexName, e);
            }
        }
    }

    /**
     * 安全地添加字段（如果字段已存在则忽略错误）
     */
    private void addColumnIfNotExists(Statement stmt, String tableName, String columnName, String columnDefinition) {
        try {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
            plugin.getLogger().info("已添加字段: " + tableName + "." + columnName);
        } catch (SQLException e) {
            // 字段已存在或其他错误，忽略
            if (e.getMessage().contains("Duplicate column") || e.getMessage().contains("already exists")) {
                plugin.getLogger().fine("字段已存在: " + tableName + "." + columnName);
            } else {
                plugin.getLogger().log(Level.WARNING, "添加字段失败: " + tableName + "." + columnName, e);
            }
        }
    }

    /**
     * 获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("数据源未初始化或已关闭");
        }
        return dataSource.getConnection();
    }

    /**
     * 获取数据库队列
     */
    public DatabaseQueue getDatabaseQueue() {
        return databaseQueue;
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    // ==================== 事务支持 ====================

    /**
     * 执行事务操作
     * 参照 FoliaShop 的事务实现
     * 
     * @param operation 事务操作，返回 true 表示提交，false 表示回滚
     * @return 事务是否成功
     */
    public boolean executeTransaction(TransactionOperation operation) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean commit = operation.execute(conn);
                if (commit) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                plugin.getLogger().log(Level.WARNING, "事务执行失败，已回滚", e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "获取数据库连接失败", e);
            return false;
        }
    }

    /**
     * 事务操作接口
     */
    @FunctionalInterface
    public interface TransactionOperation {
        /**
         * 执行事务操作
         * @param conn 数据库连接
         * @return true 提交事务，false 回滚事务
         * @throws SQLException SQL 异常
         */
        boolean execute(Connection conn) throws SQLException;
    }

    // ==================== 行锁支持 ====================

    /**
     * 获取带行锁的公告数据
     * 使用 FOR UPDATE 锁定行，防止并发修改
     * 参照 FoliaShop 的原子性操作实现
     * 
     * @param conn 数据库连接（必须在事务中）
     * @param id 公告 ID
     * @return 是否锁定成功
     * @throws SQLException SQL 异常
     */
    public boolean lockAnnouncement(Connection conn, String id) throws SQLException {
        String sql = "SELECT id FROM announcements WHERE id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // 如果存在则锁定成功
            }
        }
    }

    /**
     * 获取带行锁的公告版本信息
     * 
     * @param conn 数据库连接（必须在事务中）
     * @param id 公告 ID
     * @return 公告版本信息，不存在返回 null
     * @throws SQLException SQL 异常
     */
    public AnnouncementVersion lockAndGetVersion(Connection conn, String id) throws SQLException {
        String sql = "SELECT id, version, updated_at, updated_by FROM announcements WHERE id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AnnouncementVersion(
                        rs.getString("id"),
                        rs.getInt("version"),
                        rs.getLong("updated_at"),
                        rs.getString("updated_by")
                    );
                }
                return null;
            }
        }
    }

    /**
     * 公告版本信息记录
     */
    public record AnnouncementVersion(String id, int version, long updatedAt, String updatedBy) {}
}
