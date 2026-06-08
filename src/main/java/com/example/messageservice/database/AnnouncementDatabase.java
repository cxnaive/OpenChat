package com.example.messageservice.database;

import com.example.messageservice.models.Announcement;
import com.example.messageservice.MessageServicePlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * 公告数据库操作类
 * 处理公告的增删改查操作
 */
public class AnnouncementDatabase {

    private final MessageServicePlugin plugin;
    private final DatabaseManager databaseManager;

    public AnnouncementDatabase(MessageServicePlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    /**
     * 保存公告到数据库
     */
    public void saveAnnouncement(Announcement announcement, String serverId) {
        String sql = """
            INSERT INTO announcements (
                id, enabled, priority, content, display_type, fade_in, stay, fade_out,
                color, style, toast_icon, bossbar_progress, servers, target_type, target_value,
                trigger_type, trigger_value, global_cooldown, player_cooldown, version, updated_at, updated_by, created_at, server_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                enabled = VALUES(enabled),
                priority = VALUES(priority),
                content = VALUES(content),
                display_type = VALUES(display_type),
                fade_in = VALUES(fade_in),
                stay = VALUES(stay),
                fade_out = VALUES(fade_out),
                color = VALUES(color),
                style = VALUES(style),
                toast_icon = VALUES(toast_icon),
                bossbar_progress = VALUES(bossbar_progress),
                servers = VALUES(servers),
                target_type = VALUES(target_type),
                target_value = VALUES(target_value),
                trigger_type = VALUES(trigger_type),
                trigger_value = VALUES(trigger_value),
                global_cooldown = VALUES(global_cooldown),
                player_cooldown = VALUES(player_cooldown),
                version = VALUES(version),
                updated_at = VALUES(updated_at),
                updated_by = VALUES(updated_by),
                server_id = VALUES(server_id)
        """;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int index = 1;
            ps.setString(index++, announcement.getId());
            ps.setBoolean(index++, announcement.isEnabled());
            ps.setInt(index++, announcement.getPriority());
            ps.setString(index++, String.join("\n", announcement.getContent()));

            Announcement.DisplaySettings display = announcement.getDisplay();
            if (display != null) {
                ps.setString(index++, display.getType().name());
                ps.setInt(index++, display.getFadeIn());
                ps.setInt(index++, display.getStay());
                ps.setInt(index++, display.getFadeOut());
                ps.setString(index++, display.getColor());
                ps.setString(index++, display.getStyle());
                ps.setString(index++, display.getToastIcon());
                ps.setBoolean(index++, display.isBossbarProgress());
            } else {
                ps.setString(index++, "CHAT");
                ps.setInt(index++, 10);
                ps.setInt(index++, 70);
                ps.setInt(index++, 20);
                ps.setNull(index++, Types.VARCHAR);
                ps.setNull(index++, Types.VARCHAR);
                ps.setNull(index++, Types.VARCHAR);
                ps.setBoolean(index++, true);
            }

            List<String> servers = announcement.getServers();
            ps.setString(index++, servers != null ? String.join(",", servers) : "*");

            Announcement.TargetSettings target = announcement.getTarget();
            if (target != null) {
                ps.setString(index++, target.getType().name());
                ps.setString(index++, target.getValue());
            } else {
                ps.setString(index++, "ALL");
                ps.setString(index++, "*");
            }

            Announcement.TriggerSettings trigger = announcement.getTrigger();
            if (trigger != null) {
                ps.setString(index++, trigger.getType().name());
                ps.setString(index++, trigger.getSchedule());
            } else {
                ps.setString(index++, "MANUAL");
                ps.setNull(index++, Types.VARCHAR);
            }

            Announcement.CooldownSettings cooldown = announcement.getCooldown();
            if (cooldown != null) {
                ps.setInt(index++, cooldown.getGlobal());
                ps.setInt(index++, cooldown.getPerPlayer());
            } else {
                ps.setInt(index++, 0);
                ps.setInt(index++, 0);
            }

            ps.setInt(index++, announcement.getVersion() != null ? announcement.getVersion() : 1);
            ps.setLong(index++, System.currentTimeMillis());
            ps.setString(index++, serverId);
            ps.setLong(index++, System.currentTimeMillis());
            ps.setString(index++, serverId);

            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "保存公告到数据库失败: " + announcement.getId(), e);
        }
    }

    /**
     * 从数据库删除公告
     */
    public void deleteAnnouncement(String id) {
        String sql = "DELETE FROM announcements WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "从数据库删除公告失败: " + id, e);
        }
    }

    /**
     * 从数据库加载所有公告
     */
    public List<Announcement> loadAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcements";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Announcement announcement = parseAnnouncementFromResultSet(rs);
                if (announcement != null) {
                    announcements.add(announcement);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "从数据库加载公告失败", e);
        }

        return announcements;
    }

    /**
     * 获取上次更新时间之后的新公告或更新
     */
    public List<Announcement> loadAnnouncementsSince(long timestamp) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcements WHERE updated_at > ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, timestamp);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Announcement announcement = parseAnnouncementFromResultSet(rs);
                    if (announcement != null) {
                        announcements.add(announcement);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "从数据库加载更新公告失败", e);
        }

        return announcements;
    }

    /**
     * 更新公告的更新时间（用于触发跨服同步）
     */
    public void updateAnnouncementTimestamp(String id) {
        String sql = "UPDATE announcements SET updated_at = ? WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, System.currentTimeMillis());
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "更新公告时间戳失败: " + id, e);
        }
    }

    /**
     * 获取所有公告的版本信息（轻量级查询）
     */
    public List<AnnouncementVersion> loadAnnouncementVersions() {
        List<AnnouncementVersion> versions = new ArrayList<>();
        String sql = "SELECT id, version, updated_at, updated_by FROM announcements";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                versions.add(new AnnouncementVersion(
                    rs.getString("id"),
                    rs.getInt("version"),
                    rs.getLong("updated_at"),
                    rs.getString("updated_by")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "从数据库加载公告版本失败", e);
        }

        return versions;
    }

    /**
     * 解析公告对象
     */
    private Announcement parseAnnouncementFromResultSet(ResultSet rs) throws SQLException {
        try {
            String id = rs.getString("id");
            boolean enabled = rs.getBoolean("enabled");
            int priority = rs.getInt("priority");

            // 解析内容
            String contentStr = rs.getString("content");
            List<String> content = contentStr != null && !contentStr.isEmpty() ? 
                new ArrayList<>(Arrays.asList(contentStr.split("\n"))) : new ArrayList<>();

            // 解析显示设置
            Announcement.DisplaySettings.DisplaySettingsBuilder displayBuilder = 
                Announcement.DisplaySettings.builder();
            String displayType = rs.getString("display_type");
            if (displayType != null) {
                displayBuilder.type(Announcement.DisplayType.fromString(displayType));
            }
            displayBuilder.fadeIn(rs.getInt("fade_in"));
            displayBuilder.stay(rs.getInt("stay"));
            displayBuilder.fadeOut(rs.getInt("fade_out"));
            displayBuilder.color(rs.getString("color"));
            displayBuilder.style(rs.getString("style"));
            displayBuilder.toastIcon(rs.getString("toast_icon"));
            displayBuilder.bossbarProgress(rs.getBoolean("bossbar_progress"));

            // 解析服务器列表
            String serversStr = rs.getString("servers");
            List<String> servers = serversStr != null && !serversStr.isEmpty() ?
                new ArrayList<>(Arrays.asList(serversStr.split(","))) : new ArrayList<>(List.of("*"));

            // 解析目标设置
            Announcement.TargetSettings.TargetSettingsBuilder targetBuilder = 
                Announcement.TargetSettings.builder();
            String targetType = rs.getString("target_type");
            if (targetType != null) {
                targetBuilder.type(Announcement.TargetType.fromString(targetType));
            }
            targetBuilder.value(rs.getString("target_value"));

            // 解析触发设置
            Announcement.TriggerSettings.TriggerSettingsBuilder triggerBuilder = 
                Announcement.TriggerSettings.builder();
            String triggerType = rs.getString("trigger_type");
            if (triggerType != null) {
                triggerBuilder.type(Announcement.TriggerType.fromString(triggerType));
            }
            triggerBuilder.schedule(rs.getString("trigger_value"));
            triggerBuilder.events(new ArrayList<>());

            // 解析冷却设置
            Announcement.CooldownSettings.CooldownSettingsBuilder cooldownBuilder = 
                Announcement.CooldownSettings.builder();
            cooldownBuilder.global(rs.getInt("global_cooldown"));
            cooldownBuilder.perPlayer(rs.getInt("player_cooldown"));

            return Announcement.builder()
                .id(id)
                .enabled(enabled)
                .priority(priority)
                .content(content)
                .display(displayBuilder.build())
                .servers(servers)
                .target(targetBuilder.build())
                .trigger(triggerBuilder.build())
                .cooldown(cooldownBuilder.build())
                .version(rs.getInt("version"))
                .build();

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "解析公告数据失败", e);
            return null;
        }
    }

    /**
     * 获取单个公告的版本信息
     */
    public AnnouncementVersion loadAnnouncementVersion(String id) {
        String sql = "SELECT id, version, updated_at, updated_by FROM announcements WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "获取公告版本失败: " + id, e);
        }

        return null;
    }

    /**
     * 获取单个公告（从数据库）
     */
    public Announcement loadAnnouncement(String id) {
        String sql = "SELECT * FROM announcements WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return parseAnnouncementFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "获取公告失败: " + id, e);
        }

        return null;
    }

    /**
     * 公告版本信息记录
     */
    public record AnnouncementVersion(String id, int version, long updatedAt, String updatedBy) {}
}
