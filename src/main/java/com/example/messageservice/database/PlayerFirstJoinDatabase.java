package com.example.messageservice.database;

import com.example.messageservice.MessageServicePlugin;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

/**
 * 玩家首次登录数据库管理
 * 用于记录玩家是否首次登录服务器
 */
public class PlayerFirstJoinDatabase {

    private final MessageServicePlugin plugin;
    private final DatabaseManager databaseManager;

    public PlayerFirstJoinDatabase(MessageServicePlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    /**
     * 创建玩家首次登录记录表
     */
    public void createTable() {
        if (!databaseManager.isConnected()) {
            return;
        }

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS player_first_join (
                    uuid VARCHAR(36) PRIMARY KEY,
                    player_name VARCHAR(32) NOT NULL,
                    first_join_time BIGINT NOT NULL,
                    server_name VARCHAR(64) NOT NULL,
                    INDEX idx_server_name (server_name)
                )
            """);

            plugin.getLogger().fine("玩家首次登录表检查完成");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "创建 player_first_join 表失败", e);
        }
    }

    /**
     * 检查玩家是否首次登录
     *
     * @param uuid 玩家UUID
     * @return 如果是首次登录返回 true，否则返回 false
     */
    public boolean isFirstJoin(UUID uuid) {
        if (!databaseManager.isConnected()) {
            // 数据库未连接时，使用内存缓存或视为非首次登录
            return false;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT 1 FROM player_first_join WHERE uuid = ? LIMIT 1")) {

            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            return !rs.next(); // 如果没有记录，则是首次登录

        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "检查玩家首次登录状态失败: " + uuid, e);
            return false;
        }
    }

    /**
     * 记录玩家首次登录
     *
     * @param uuid       玩家UUID
     * @param playerName 玩家名称
     * @param serverName 服务器名称
     */
    public void recordFirstJoin(UUID uuid, String playerName, String serverName) {
        if (!databaseManager.isConnected()) {
            return;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO player_first_join (uuid, player_name, first_join_time, server_name) VALUES (?, ?, ?, ?) " +
                 "ON DUPLICATE KEY UPDATE player_name = ?")) {

            long currentTime = System.currentTimeMillis();
            stmt.setString(1, uuid.toString());
            stmt.setString(2, playerName);
            stmt.setLong(3, currentTime);
            stmt.setString(4, serverName);
            stmt.setString(5, playerName); // 更新玩家名称

            stmt.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "记录玩家首次登录失败: " + uuid, e);
        }
    }

    /**
     * 异步检查并记录玩家首次登录
     * 使用数据库队列执行
     *
     * @param uuid       玩家UUID
     * @param playerName 玩家名称
     * @param serverName 服务器名称
     * @param callback   回调函数，参数为是否是首次登录
     */
    public void checkAndRecordFirstJoinAsync(UUID uuid, String playerName, String serverName, FirstJoinCallback callback) {
        if (!databaseManager.isConnected()) {
            // 数据库未连接时，直接回调 false
            callback.onResult(false);
            return;
        }

        DatabaseQueue queue = databaseManager.getDatabaseQueue();
        if (queue == null) {
            callback.onResult(false);
            return;
        }

        queue.submit("checkFirstJoin_" + uuid, conn -> {
            boolean isFirstJoin = isFirstJoin(uuid);
            if (isFirstJoin) {
                recordFirstJoin(uuid, playerName, serverName);
            }
            return isFirstJoin;
        }, result -> {
            if (result instanceof Boolean) {
                callback.onResult((Boolean) result);
            } else {
                callback.onResult(false);
            }
        }, error -> {
            plugin.getLogger().log(Level.WARNING, "检查首次登录失败: " + uuid, error);
            callback.onResult(false);
        });
    }

    /**
     * 首次登录检查回调接口
     */
    @FunctionalInterface
    public interface FirstJoinCallback {
        void onResult(boolean isFirstJoin);
    }
}
