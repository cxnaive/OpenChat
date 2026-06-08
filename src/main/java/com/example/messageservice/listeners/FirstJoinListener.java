package com.example.messageservice.listeners;

import com.example.messageservice.database.DatabaseManager;
import com.example.messageservice.database.PlayerFirstJoinDatabase;
import com.example.messageservice.managers.AnnouncementManager;
import com.example.messageservice.models.Announcement;
import com.example.messageservice.services.AnnouncementService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import com.example.messageservice.MessageServicePlugin;

import java.util.List;
import java.util.Optional;

/**
 * 玩家首次登录监听器
 * 处理 FIRST_JOIN 类型的公告触发
 */
public class FirstJoinListener implements Listener {

    private final MessageServicePlugin plugin;
    private final AnnouncementManager announcementManager;
    private final AnnouncementService announcementService;
    private final PlayerFirstJoinDatabase playerFirstJoinDatabase;
    private final String serverName;

    public FirstJoinListener(MessageServicePlugin plugin, AnnouncementManager announcementManager,
                            AnnouncementService announcementService, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.announcementManager = announcementManager;
        this.announcementService = announcementService;
        this.serverName = plugin.getConfig().getString("cross-server.server-name", "unknown");

        // 初始化首次登录数据库
        if (databaseManager != null && databaseManager.isConnected()) {
            this.playerFirstJoinDatabase = new PlayerFirstJoinDatabase(plugin, databaseManager);
            this.playerFirstJoinDatabase.createTable();
        } else {
            this.playerFirstJoinDatabase = null;
            plugin.getLogger().warning("数据库未连接，首次登录公告功能将不可用");
        }
    }

    /**
     * 玩家加入服务器事件
     * 检查是否是首次登录并触发相应公告
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 如果数据库未连接，不处理
        if (playerFirstJoinDatabase == null) {
            return;
        }

        Player player = event.getPlayer();

        // 异步检查并记录首次登录
        playerFirstJoinDatabase.checkAndRecordFirstJoinAsync(
            player.getUniqueId(),
            player.getName(),
            serverName,
            isFirstJoin -> {
                if (isFirstJoin) {
                    // 是首次登录，触发首次登录公告
                    triggerFirstJoinAnnouncements(player);
                }
            }
        );
    }

    /**
     * 触发所有 FIRST_JOIN 类型的公告
     *
     * @param player 首次登录的玩家
     */
    private void triggerFirstJoinAnnouncements(Player player) {
        // 获取所有启用的公告
        List<Announcement> announcements = announcementManager.getEnabledAnnouncements();

        for (Announcement announcement : announcements) {
            if (announcement.getTrigger() == null) {
                continue;
            }

            // 检查是否是 FIRST_JOIN 类型的触发器
            if (announcement.getTrigger().getType() != Announcement.TriggerType.FIRST_JOIN) {
                continue;
            }

            // 检查公告是否对当前服务器生效
            List<String> servers = announcement.getServers();
            if (servers != null && !servers.isEmpty() && !servers.contains("*")) {
                if (!servers.contains(serverName)) {
                    continue;
                }
            }

            // 检查冷却（全局冷却）
            if (announcement.getCooldown() != null && announcement.getCooldown().getGlobal() > 0) {
                if (!announcementManager.checkGlobalCooldown(announcement.getId(), announcement.getCooldown().getGlobal())) {
                    continue;
                }
            }

            // 检查冷却（玩家冷却）- 首次登录公告通常不需要玩家冷却
            if (announcement.getCooldown() != null && announcement.getCooldown().getPerPlayer() > 0) {
                if (!announcementManager.checkPlayerCooldown(player.getUniqueId(), announcement.getId(), announcement.getCooldown().getPerPlayer())) {
                    continue;
                }
            }

            // 发送公告给目标玩家
            sendFirstJoinAnnouncement(announcement, player);
        }
    }

    /**
     * 发送首次登录公告
     *
     * @param announcement 公告配置
     * @param player       首次登录的玩家
     */
    private void sendFirstJoinAnnouncement(Announcement announcement, Player player) {
        Announcement.TargetSettings target = announcement.getTarget();

        if (target == null) {
            // 默认发送给触发玩家自己
            announcementService.sendAnnouncementToPlayer(announcement.getId(), player);
            return;
        }

        Announcement.TargetType targetType = target.getType();
        if (targetType == null) {
            targetType = Announcement.TargetType.ALL;
        }

        switch (targetType) {
            case ALL -> {
                // 广播给所有玩家（欢迎新玩家）
                announcementService.broadcastAnnouncement(announcement.getId());
            }
            case PERMISSION -> {
                // 发送给有特定权限的玩家
                String permission = target.getValue();
                if (permission != null && !permission.isEmpty()) {
                    for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission(permission)) {
                            announcementService.sendAnnouncementToPlayer(announcement.getId(), onlinePlayer);
                        }
                    }
                }
            }
            case WORLD -> {
                // 发送给同一世界的玩家
                String worldName = target.getValue();
                if (worldName != null && !worldName.isEmpty() && player.getWorld().getName().equals(worldName)) {
                    for (Player onlinePlayer : player.getWorld().getPlayers()) {
                        announcementService.sendAnnouncementToPlayer(announcement.getId(), onlinePlayer);
                    }
                }
            }
            case TRIGGER_PLAYER -> {
                // 仅发送给触发玩家自己（使用新的方法支持 TRIGGER_PLAYER 目标类型）
                announcementService.executeAnnouncement(announcement, player);
            }
            case REGION, RANGE -> {
                // REGION 和 RANGE 类型对首次登录公告不适用，发送给触发玩家
                announcementService.sendAnnouncementToPlayer(announcement.getId(), player);
            }
            default -> {
                // 默认发送给触发玩家
                announcementService.sendAnnouncementToPlayer(announcement.getId(), player);
            }
        }

        // 设置冷却
        if (announcement.getCooldown() != null) {
            if (announcement.getCooldown().getGlobal() > 0) {
                announcementManager.setGlobalCooldown(announcement.getId());
            }
            if (announcement.getCooldown().getPerPlayer() > 0) {
                announcementManager.setPlayerCooldown(player.getUniqueId(), announcement.getId());
            }
        }
    }
}
