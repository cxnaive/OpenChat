package com.example.messageservice.managers;

import com.example.messageservice.MessageServicePlugin;
import com.example.messageservice.database.DatabaseManager;
import com.example.messageservice.models.Announcement;
import com.example.messageservice.services.AnnouncementService;
import cn.handyplus.chat.PlayerChat;
import cn.handyplus.lib.util.BcUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 跨服务器同步管理器
 * 通道注册已合并到 ChatPluginMessageListener，本类只负责消息处理逻辑
 */
public class CrossServerSyncManager {

    private final MessageServicePlugin plugin;
    private final AnnouncementManager announcementManager;
    private final AnnouncementService announcementService;

    // 配置
    private boolean enabled;
    private String serverName;

    // 防重集合（参考 PlayerChat）
    private final Set<String> processedMessages;

    // BungeeCord 自动发现的区服列表
    private volatile List<String> discoveredServers;
    // BungeeCord 自动发现的本服名称
    private volatile String discoveredServerName;
    // 是否已发起过自动发现请求
    private volatile boolean hasRequestedDiscovery;

    public CrossServerSyncManager(MessageServicePlugin plugin, AnnouncementManager announcementManager,
                                  AnnouncementService announcementService, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.announcementManager = announcementManager;
        this.announcementService = announcementService;

        this.processedMessages = ConcurrentHashMap.newKeySet();
    }

    /**
     * 初始化跨服同步
     * 通道注册已由 ChatPluginMessageListener 统一管理，此处仅加载配置
     */
    public void initialize() {
        loadConfig();

        // 无论是否启用跨服同步，都尝试自动发现区服列表（GUI 需要）
        scheduleAutoDiscovery();

        if (!enabled) {
            plugin.getLogger().info("跨服务器同步已禁用");
            return;
        }

        plugin.getLogger().info("跨服务器同步管理器已启动，服务器ID: " + getEffectiveServerName());
    }

    /**
     * 关闭跨服同步
     */
    public void shutdown() {
        if (!enabled) return;
        processedMessages.clear();
        plugin.getLogger().info("跨服务器同步管理器已关闭");
    }

    /**
     * 加载配置
     */
    private void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("cross-server.enabled", false);
        this.serverName = plugin.getConfig().getString("cross-server.server-name", "server-" + System.currentTimeMillis());
    }

    // ==================== BungeeCord 区服自动发现 ====================

    /**
     * 调度自动发现任务：启动后等待玩家上线，自动请求区服列表和本服名称
     */
    private void scheduleAutoDiscovery() {
        // 延迟 3 秒后开始，每 3 秒检查一次，最多尝试 30 次（90秒）
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(PlayerChat.INSTANCE, task -> {
            if (hasRequestedDiscovery) {
                task.cancel();
                return;
            }
            tryAutoDiscover();
            if (hasRequestedDiscovery) {
                task.cancel();
            }
        }, 60L, 60L); // 3秒后开始，每3秒检查
    }

    /**
     * 尝试自动发现（有在线玩家时发送请求）
     */
    public void tryAutoDiscover() {
        if (hasRequestedDiscovery) return;
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player != null) {
            requestServerList(player);
            requestCurrentServerName(player);
            hasRequestedDiscovery = true;
        }
    }

    /**
     * 获取实际使用的服务器名称（优先自动发现，回退到配置）
     */
    public String getEffectiveServerName() {
        return discoveredServerName != null ? discoveredServerName : serverName;
    }

    /**
     * 向 BungeeCord 请求区服列表
     *
     * @param player 用于发送消息的玩家
     */
    public void requestServerList(Player player) {
        if (player == null || !player.isOnline()) return;
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("GetServers");
            player.sendPluginMessage(PlayerChat.INSTANCE, "BungeeCord", b.toByteArray());
            plugin.getLogger().fine("已向 BungeeCord 请求区服列表");
        } catch (Exception e) {
            plugin.getLogger().warning("请求 BungeeCord 区服列表失败: " + e.getMessage());
        }
    }

    /**
     * 向 BungeeCord 请求本服名称
     *
     * @param player 用于发送消息的玩家
     */
    public void requestCurrentServerName(Player player) {
        if (player == null || !player.isOnline()) return;
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("GetServer");
            player.sendPluginMessage(PlayerChat.INSTANCE, "BungeeCord", b.toByteArray());
            plugin.getLogger().fine("已向 BungeeCord 请求本服名称");
        } catch (Exception e) {
            plugin.getLogger().warning("请求 BungeeCord 本服名称失败: " + e.getMessage());
        }
    }

    /**
     * 处理 BungeeCord 返回的区服列表
     * 由 ChatPluginMessageListener 调用
     *
     * @param servers 发现的区服列表
     */
    public void setDiscoveredServers(List<String> servers) {
        if (servers != null && !servers.isEmpty()) {
            Collections.sort(servers);
            this.discoveredServers = servers;
            plugin.getLogger().info("从 BungeeCord 发现 " + servers.size() + " 个区服: " + String.join(", ", servers));
        }
    }

    /**
     * 获取自动发现的区服列表
     *
     * @return 发现的区服列表，可能为 null 或空
     */
    public List<String> getDiscoveredServers() {
        return discoveredServers;
    }

    /**
     * 处理 BungeeCord 返回的本服名称
     * 由 ChatPluginMessageListener 调用
     *
     * @param name 本服名称
     */
    public void setDiscoveredServerName(String name) {
        if (name != null && !name.isEmpty()) {
            this.discoveredServerName = name;
            plugin.getLogger().info("从 BungeeCord 发现本服名称: " + name);
        }
    }

    /**
     * 处理已解析的跨服公告消息
     * 由 ChatPluginMessageListener 在识别到公告类型消息后调用
     *
     * @param param 已解析的消息参数
     */
    public void handleIncomingMessage(BcUtil.BcMessageParam param) {
        if (!enabled) return;

        // 忽略自己发送的消息
        if (serverName.equals(param.getServerName())) {
            return;
        }

        // 防重检测
        String messageId = param.getServerName() + ":" + param.getTimestamp() + ":" + param.getMessage();
        if (processedMessages.contains(messageId)) {
            return;
        }
        processedMessages.add(messageId);

        // 清理旧消息（防止内存泄漏）
        if (processedMessages.size() > 1000) {
            processedMessages.clear();
        }

        // 处理公告执行消息
        if ("ANNOUNCEMENT_EXECUTE".equals(param.getType())) {
            String announcementId = param.getMessage();
            Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () -> {
                announcementService.broadcastAnnouncement(announcementId);
            });
        }

        // 处理公告更新消息（实时同步）
        if ("ANNOUNCEMENT_UPDATE".equals(param.getType())) {
            String updateMessage = param.getMessage();
            plugin.getLogger().info("[调试] 收到公告更新通知: " + updateMessage);

            String[] parts = updateMessage.split(":");
            if (parts.length >= 2) {
                try {
                    long remoteVersion = Long.parseLong(parts[0]);
                    String updatedBy = parts[1];

                    if (announcementService.getAnnouncementManager().getAnnouncementCache() != null) {
                        boolean needReload = announcementService.getAnnouncementManager().getAnnouncementCache().handleVersionUpdate(remoteVersion, updatedBy);
                        if (needReload) {
                            plugin.getLogger().info("检测到公告版本更新，重新加载缓存");
                            announcementService.getAnnouncementManager().getAnnouncementCache().loadFromDatabase();
                            announcementService.getAnnouncementManager().reload();
                        }
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("解析版本号失败: " + updateMessage);
                }
            }
        }

        // 处理临时公告消息（sendnow 命令）
        if ("TEMPORARY_ANNOUNCEMENT".equals(param.getType())) {
            String tempMessage = param.getMessage();
            String[] parts = tempMessage.split("\\|", -1);
            if (parts.length >= 2) {
                String content = parts[0];
                Announcement.DisplayType displayType = Announcement.DisplayType.fromString(parts[1]);
                String soundName = parts.length >= 3 ? parts[2] : null;
                executeTemporaryAnnouncement(content, displayType, soundName);
            }
        }

        // 处理公告同步消息
        if ("ANNOUNCEMENT_SYNC".equals(param.getType())) {
            // 公告已通过数据库同步，这里只需刷新缓存
            plugin.getLogger().info("收到公告同步通知: " + param.getMessage());
            if (announcementService.getAnnouncementManager().getAnnouncementCache() != null) {
                announcementService.getAnnouncementManager().getAnnouncementCache().loadFromDatabase();
                announcementService.getAnnouncementManager().reload();
            }
        }

        // 处理公告删除消息
        if ("ANNOUNCEMENT_DELETE".equals(param.getType())) {
            String announcementId = param.getMessage();
            plugin.getLogger().info("收到公告删除通知: " + announcementId);
            announcementService.getAnnouncementManager().deleteAnnouncement(announcementId, false);
        }
    }

    /**
     * 判断消息类型是否为公告相关
     */
    public static boolean isAnnouncementType(String type) {
        if (type == null) return false;
        return type.startsWith("ANNOUNCEMENT_") || "TEMPORARY_ANNOUNCEMENT".equals(type)
                || type.startsWith("IMMEDIATE_MESSAGE_");
    }

    /**
     * 广播公告执行命令到所有服务器
     * 参考 PlayerChat 的发送方式
     */
    public void broadcastAnnouncementExecute(String announcementId) {
        broadcastAnnouncementExecuteToServers(announcementId, null);
    }

    /**
     * 广播公告执行命令到指定服务器
     * @param announcementId 公告ID
     * @param targetServers 目标服务器列表，为null或空则发送到所有服务器
     */
    public void broadcastAnnouncementExecuteToServers(String announcementId, List<String> targetServers) {
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        
        // 如果没有指定服务器，则发送到所有服务器
        if (targetServers == null || targetServers.isEmpty()) {
            if (player == null) {
                plugin.getLogger().warning("没有在线玩家，无法发送跨服消息");
                return;
            }

            // 构建消息参数
            BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
            param.setPluginName(PlayerChat.INSTANCE.getName());
            param.setType("ANNOUNCEMENT_EXECUTE");
            param.setPlayerName("CONSOLE");
            param.setMessage(announcementId);
            param.setTimestamp(System.currentTimeMillis());
            param.setServerName(serverName);

            // 先标记消息ID，防止重复处理
            String messageId = serverName + ":" + param.getTimestamp() + ":" + announcementId;
            processedMessages.add(messageId);

            // 发送（使用 PlayerChat 的 sendParamForward 方法）
            BcUtil.sendParamForward(player, param);

            // 立即执行本地公告（在标记之后，确保不会重复）
            announcementService.broadcastAnnouncement(announcementId);
        } else {
            // 发送到指定服务器
            if (player == null) {
                // 检查当前服务器是否在目标列表中
                if (targetServers.contains(serverName)) {
                    announcementService.broadcastAnnouncement(announcementId);
                } else {
                    plugin.getLogger().warning("没有在线玩家，无法发送跨服消息");
                }
                return;
            }

            for (String targetServer : targetServers) {
                if (targetServer.equalsIgnoreCase(serverName)) {
                    // 当前服务器，直接执行
                    announcementService.broadcastAnnouncement(announcementId);
                    continue;
                }

                // 发送到其他服务器
                BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
                param.setPluginName(PlayerChat.INSTANCE.getName());
                param.setType("ANNOUNCEMENT_EXECUTE");
                param.setPlayerName("CONSOLE");
                param.setMessage(announcementId);
                param.setTimestamp(System.currentTimeMillis());
                param.setServerName(serverName);

                String messageId = serverName + ":" + param.getTimestamp() + ":" + announcementId + ":" + targetServer;
                processedMessages.add(messageId);

                BcUtil.sendParamForwardToServer(player, targetServer, param);
            }
        }
    }

    /**
     * 广播公告更新通知到所有服务器
     * 用于实时同步公告变更
     */
    public void broadcastAnnouncementUpdate(long version, String updatedBy) {
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) {
            plugin.getLogger().warning("没有在线玩家，无法发送跨服消息");
            return;
        }

        // 构建消息参数
        BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
        param.setPluginName(PlayerChat.INSTANCE.getName());
        param.setType("ANNOUNCEMENT_UPDATE");
        param.setMessage(version + ":" + updatedBy);
        param.setTimestamp(System.currentTimeMillis());
        param.setServerName(serverName);

        // 发送（使用 PlayerChat 的 sendParamForward 方法）
        BcUtil.sendParamForward(player, param);
    }

    /**
     * 保存并广播公告（兼容旧接口）
     * 公告数据已保存到数据库，这里只发送同步通知
     */
    public void saveAndBroadcast(com.example.messageservice.models.Announcement announcement) {
        // 公告已保存到数据库，发送同步消息通知其他服务器
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) return;

        BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
        param.setPluginName(PlayerChat.INSTANCE.getName());
        param.setType("ANNOUNCEMENT_SYNC");
        param.setPlayerName("CONSOLE");
        param.setMessage(announcement.getId());
        param.setTimestamp(System.currentTimeMillis());
        param.setServerName(serverName);

        BcUtil.sendParamForward(player, param);
    }

    /**
     * 删除并广播（兼容旧接口）
     */
    public void deleteAndBroadcast(String announcementId) {
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) return;

        BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
        param.setPluginName(PlayerChat.INSTANCE.getName());
        param.setType("ANNOUNCEMENT_DELETE");
        param.setPlayerName("CONSOLE");
        param.setMessage(announcementId);
        param.setTimestamp(System.currentTimeMillis());
        param.setServerName(serverName);

        BcUtil.sendParamForward(player, param);
    }

    /**
     * 广播即时消息（兼容旧接口）
     */
    public void broadcastImmediateMessage(String message, String type, String soundPreset) {
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) {
            plugin.getLogger().warning("没有在线玩家，无法发送跨服消息");
            return;
        }

        // 构建包含消息和音效的JSON
        String jsonMessage = "{\"message\":\"" + message.replace("\"", "\\\"") + "\",\"sound\":\"" + soundPreset + "\"}";

        BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
        param.setPluginName(PlayerChat.INSTANCE.getName());
        param.setType("IMMEDIATE_MESSAGE_" + type);
        param.setPlayerName("CONSOLE");
        param.setMessage(jsonMessage);
        param.setTimestamp(System.currentTimeMillis());
        param.setServerName(serverName);

        BcUtil.sendParamForward(player, param);
    }

    /**
     * 检查是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 广播临时公告（sendnow 命令）
     * 创建临时公告对象并立即执行
     */
    public void broadcastTemporaryAnnouncement(String content, Announcement.DisplayType displayType, String soundName) {
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) {
            plugin.getLogger().warning("没有在线玩家，无法发送跨服消息");
            return;
        }

        // 构建临时公告数据
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(content).append("|");
        messageBuilder.append(displayType != null ? displayType.name() : "CHAT").append("|");
        messageBuilder.append(soundName != null ? soundName : "");

        // 构建消息参数
        BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
        param.setPluginName(PlayerChat.INSTANCE.getName());
        param.setType("TEMPORARY_ANNOUNCEMENT");
        param.setPlayerName("CONSOLE");
        param.setMessage(messageBuilder.toString());
        param.setTimestamp(System.currentTimeMillis());
        param.setServerName(serverName);

        // 先标记消息ID，防止重复处理
        String messageId = serverName + ":" + param.getTimestamp() + ":" + content.hashCode();
        processedMessages.add(messageId);

        // 发送（使用 PlayerChat 的 sendParamForward 方法）
        BcUtil.sendParamForward(player, param);

        // 立即在本地执行
        executeTemporaryAnnouncement(content, displayType, soundName);
    }

    /**
     * 执行临时公告
     */
    private void executeTemporaryAnnouncement(String content, Announcement.DisplayType displayType, String soundPreset) {
        // 创建临时公告对象
        Announcement.SoundSettings soundSettings = null;
        if (soundPreset != null && !soundPreset.isEmpty()) {
            // 解析音效预设
            String soundName = parseSoundPreset(soundPreset);
            if (soundName != null) {
                soundSettings = Announcement.SoundSettings.builder()
                        .sound(soundName)
                        .volume(1.0f)
                        .pitch(1.0f)
                        .build();
            }
        }

        Announcement temporaryAnnouncement = Announcement.builder()
                .id("TEMP_" + System.currentTimeMillis())
                .enabled(true)
                .priority(100)
                .content(Arrays.asList(content))
                .display(Announcement.DisplaySettings.builder()
                        .type(displayType != null ? displayType : Announcement.DisplayType.CHAT)
                        .fadeIn(10)
                        .stay(70)
                        .fadeOut(20)
                        .build())
                .target(Announcement.TargetSettings.builder()
                        .type(Announcement.TargetType.ALL)
                        .value("*")
                        .build())
                .trigger(Announcement.TriggerSettings.builder()
                        .type(Announcement.TriggerType.MANUAL)
                        .build())
                .cooldown(Announcement.CooldownSettings.builder()
                        .global(0)
                        .perPlayer(0)
                        .build())
                .servers(Arrays.asList("*"))
                .version(1)
                .sound(soundSettings)
                .build();

        // 执行公告
        announcementService.executeAnnouncement(temporaryAnnouncement);
    }

    /**
     * 解析音效预设名称
     */
    private String parseSoundPreset(String preset) {
        return switch (preset.toLowerCase()) {
            case "pling" -> "BLOCK_NOTE_BLOCK_PLING";
            case "anvil" -> "BLOCK_ANVIL_LAND";
            case "lvlup" -> "ENTITY_PLAYER_LEVELUP";
            case "bell" -> "BLOCK_NOTE_BLOCK_BELL";
            default -> null;
        };
    }

    /**
     * 获取服务器名称（优先自动发现）
     */
    public String getServerName() {
        return getEffectiveServerName();
    }

    /**
     * 广播临时公告到指定服务器
     * 
     * @param content 消息内容
     * @param displayType 显示类型
     * @param soundName 音效名称
     * @param targetServers 目标服务器列表
     */
    public void broadcastToSpecificServers(String content, Announcement.DisplayType displayType, 
                                           String soundName, List<String> targetServers) {
        if (targetServers == null || targetServers.isEmpty()) {
            broadcastTemporaryAnnouncement(content, displayType, soundName);
            return;
        }

        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) {
            plugin.getLogger().warning("没有在线玩家，无法发送跨服消息");
            return;
        }

        for (String targetServer : targetServers) {
            if (targetServer.equalsIgnoreCase(serverName)) {
                executeTemporaryAnnouncement(content, displayType, soundName);
                continue;
            }

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(content).append("|");
            messageBuilder.append(displayType != null ? displayType.name() : "CHAT").append("|");
            messageBuilder.append(soundName != null ? soundName : "");

            BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
            param.setPluginName(PlayerChat.INSTANCE.getName());
            param.setType("TEMPORARY_ANNOUNCEMENT");
            param.setPlayerName("CONSOLE");
            param.setMessage(messageBuilder.toString());
            param.setTimestamp(System.currentTimeMillis());
            param.setServerName(serverName);

            String messageId = serverName + ":" + param.getTimestamp() + ":" + content.hashCode() + ":" + targetServer;
            processedMessages.add(messageId);

            BcUtil.sendParamForwardToServer(player, targetServer, param);
        }
    }

    /**
     * 广播即时消息到指定服务器
     * 
     * @param message 消息内容
     * @param type 显示类型
     * @param soundPreset 音效预设
     * @param targetServers 目标服务器列表
     */
    public void broadcastImmediateMessageToServers(String message, String type, String soundPreset, 
                                                    List<String> targetServers) {
        if (targetServers == null || targetServers.isEmpty()) {
            broadcastImmediateMessage(message, type, soundPreset);
            return;
        }

        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (player == null) {
            plugin.getLogger().warning("没有在线玩家，无法发送跨服消息");
            return;
        }

        for (String targetServer : targetServers) {
            if (targetServer.equalsIgnoreCase(serverName)) {
                List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
                announcementService.sendImmediateMessage(message, targets, Announcement.DisplayType.CHAT);
                continue;
            }

            String jsonMessage = "{\"message\":\"" + message.replace("\"", "\\\"") + 
                                "\",\"sound\":\"" + soundPreset + "\"}";

            BcUtil.BcMessageParam param = new BcUtil.BcMessageParam();
            param.setPluginName(PlayerChat.INSTANCE.getName());
            param.setType("IMMEDIATE_MESSAGE_" + type);
            param.setPlayerName("CONSOLE");
            param.setMessage(jsonMessage);
            param.setTimestamp(System.currentTimeMillis());
            param.setServerName(serverName);

            BcUtil.sendParamForwardToServer(player, targetServer, param);
        }
    }
}
