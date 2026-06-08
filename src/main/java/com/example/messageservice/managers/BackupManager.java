package com.example.messageservice.managers;

import cn.handyplus.chat.PlayerChat;
import com.example.messageservice.database.DatabaseManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.example.messageservice.MessageServicePlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据备份管理器
 * 参照 FoliaShop 的数据库迁移和资源管理实现
 * 
 * 功能：
 * - 自动定期备份数据库
 * - 备份配置文件
 * - 保留最近 N 天的备份
 * - 支持手动备份和恢复
 */
public class BackupManager {

    private final MessageServicePlugin plugin;
    private final DatabaseManager databaseManager;
    
    // 备份目录
    private final File backupDir;
    // 保留备份天数
    private final int keepDays;
    // 备份间隔（小时）
    private final int backupInterval;
    // 是否启用自动备份
    private final boolean autoBackupEnabled;
    
    // 备份文件命名格式
    private static final String BACKUP_FILE_FORMAT = "backup_%s_%s.yml";
    private static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public BackupManager(MessageServicePlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        
        // 加载配置
        this.autoBackupEnabled = plugin.getConfig().getBoolean("backup.auto-enabled", true);
        this.keepDays = plugin.getConfig().getInt("backup.keep-days", 7);
        this.backupInterval = plugin.getConfig().getInt("backup.interval-hours", 24);
        
        // 初始化备份目录（使用 PlayerChat 的数据目录，因为 MessageServicePlugin 是桥接类）
        this.backupDir = new File(cn.handyplus.chat.PlayerChat.INSTANCE.getDataFolder(), "backups");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
    }

    /**
     * 启动自动备份任务
     */
    public void startAutoBackup() {
        if (!autoBackupEnabled) {
            plugin.getLogger().info("自动备份已禁用");
            return;
        }
        
        // 计算 tick 数（1小时 = 72000 tick）
        long intervalTicks = backupInterval * 72000L;
        
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
            PlayerChat.INSTANCE,
            task -> performBackup("auto"),
            intervalTicks,
            intervalTicks
        );
        
        plugin.getLogger().info("自动备份已启动，间隔: " + backupInterval + " 小时");
    }

    /**
     * 执行备份
     * @param type 备份类型（auto 或 manual）
     * @return 备份文件路径，失败返回 null
     */
    public String performBackup(String type) {
        try {
            String timestamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
            String backupFileName = String.format(BACKUP_FILE_FORMAT, type, timestamp);
            File backupFile = new File(backupDir, backupFileName);
            
            plugin.getLogger().info("开始执行 " + type + " 备份...");
            
            // 备份数据库
            if (databaseManager != null && databaseManager.isConnected()) {
                backupDatabase(backupFile);
            } else {
                plugin.getLogger().warning("数据库未连接，跳过数据库备份");
            }
            
            // 备份配置文件
            backupConfigs(backupFile);
            
            plugin.getLogger().info("备份完成: " + backupFile.getAbsolutePath());
            
            // 清理旧备份
            cleanupOldBackups();
            
            return backupFile.getAbsolutePath();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "备份失败", e);
            return null;
        }
    }

    /**
     * 备份数据库到 YAML 文件
     * 参照 FoliaShop 的数据库迁移实现
     */
    private void backupDatabase(File backupFile) throws Exception {
        FileConfiguration backupConfig = YamlConfiguration.loadConfiguration(backupFile);
        
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 获取所有表名
            ResultSet tables = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                backupTable(stmt, backupConfig, tableName);
            }
            
            backupConfig.save(backupFile);
        }
    }

    /**
     * 备份单个表的数据
     */
    private void backupTable(Statement stmt, FileConfiguration backupConfig, String tableName) throws Exception {
        String path = "database." + tableName;
        List<Map<String, Object>> rows = new ArrayList<>();
        
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                rows.add(row);
            }
        }
        
        backupConfig.set(path, rows);
        plugin.getLogger().fine("已备份表: " + tableName + " (" + rows.size() + " 行)");
    }

    /**
     * 备份配置文件
     */
    private void backupConfigs(File backupFile) {
        FileConfiguration backupConfig = YamlConfiguration.loadConfiguration(backupFile);
        
        // 备份 config.yml（使用 PlayerChat 的数据目录）
        backupConfigFile(backupConfig, "config", new File(cn.handyplus.chat.PlayerChat.INSTANCE.getDataFolder(), "config.yml"));

        // 备份 announcements.yml
        backupConfigFile(backupConfig, "announcements", new File(cn.handyplus.chat.PlayerChat.INSTANCE.getDataFolder(), "announcements.yml"));
        
        try {
            backupConfig.save(backupFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "保存备份文件失败", e);
        }
    }

    /**
     * 备份单个配置文件
     */
    private void backupConfigFile(FileConfiguration backupConfig, String key, File configFile) {
        if (configFile.exists()) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                backupConfig.set("configs." + key, config.getValues(true));
                plugin.getLogger().fine("已备份配置文件: " + configFile.getName());
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "备份配置文件失败: " + configFile.getName(), e);
            }
        }
    }

    /**
     * 清理旧备份
     * 参照 FoliaShop 的资源管理实现
     */
    public void cleanupOldBackups() {
        try {
            long cutoffTime = System.currentTimeMillis() - (keepDays * 24L * 60L * 60L * 1000L);
            int deletedCount = 0;
            
            File[] backupFiles = backupDir.listFiles((dir, name) -> 
                name.startsWith("backup_") && name.endsWith(".yml")
            );
            
            if (backupFiles != null) {
                for (File file : backupFiles) {
                    if (file.lastModified() < cutoffTime) {
                        if (file.delete()) {
                            deletedCount++;
                            plugin.getLogger().fine("已删除旧备份: " + file.getName());
                        }
                    }
                }
            }
            
            if (deletedCount > 0) {
                plugin.getLogger().info("已清理 " + deletedCount + " 个旧备份文件");
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "清理旧备份失败", e);
        }
    }

    /**
     * 获取所有备份文件列表
     */
    public List<BackupInfo> getBackupList() {
        List<BackupInfo> backups = new ArrayList<>();
        
        File[] backupFiles = backupDir.listFiles((dir, name) -> 
            name.startsWith("backup_") && name.endsWith(".yml")
        );
        
        if (backupFiles != null) {
            for (File file : backupFiles) {
                backups.add(new BackupInfo(
                    file.getName(),
                    new Date(file.lastModified()),
                    file.length()
                ));
            }
            
            // 按时间倒序排列
            backups.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        }
        
        return backups;
    }

    /**
     * 删除指定备份
     */
    public boolean deleteBackup(String backupName) {
        File backupFile = new File(backupDir, backupName);
        if (backupFile.exists() && backupFile.delete()) {
            plugin.getLogger().info("已删除备份: " + backupName);
            return true;
        }
        return false;
    }

    /**
     * 获取备份目录
     */
    public File getBackupDir() {
        return backupDir;
    }

    /**
     * 备份信息类
     */
    public static class BackupInfo {
        private final String name;
        private final Date date;
        private final long size;

        public BackupInfo(String name, Date date, long size) {
            this.name = name;
            this.date = date;
            this.size = size;
        }

        public String getName() { return name; }
        public Date getDate() { return date; }
        public long getSize() { return size; }
        
        public String getFormattedSize() {
            if (size < 1024) return size + " B";
            if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
}
