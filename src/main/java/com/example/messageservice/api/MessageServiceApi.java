package com.example.messageservice.api;

import cn.handyplus.chat.PlayerChat;
import com.example.messageservice.MessageServicePlugin;
import com.example.messageservice.api.builder.AnnouncementBuilder;
import com.example.messageservice.api.event.AnnouncementBroadcastEvent;
import com.example.messageservice.api.event.AnnouncementCreateEvent;
import com.example.messageservice.api.event.AnnouncementDeleteEvent;
import com.example.messageservice.api.event.AnnouncementUpdateEvent;
import com.example.messageservice.api.exception.AnnouncementException;
import com.example.messageservice.api.result.AnnouncementResult;
import com.example.messageservice.api.result.BroadcastResult;
import com.example.messageservice.managers.AnnouncementManager;
import com.example.messageservice.managers.CrossServerSyncManager;
import com.example.messageservice.models.Announcement;
import com.example.messageservice.services.AnnouncementService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * MessageService API 主接口
 * 为其他插件提供统一的公告操作接口
 *
 * @author MessageService
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageServiceApi {

    private static MessageServiceApi instance;
    private final MessageServicePlugin plugin;

    private MessageServiceApi(MessageServicePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 获取API实例
     * 合并到 PlayerChat 后，查找 "PlayerChat" 插件而非 "MessageService"
     *
     * @return MessageServiceApi实例
     * @throws IllegalStateException 如果MessageService未启用
     */
    public static MessageServiceApi getInstance() {
        if (instance == null) {
            // 合并后插件名为 PlayerChat
            Plugin playerChat = Bukkit.getPluginManager().getPlugin("PlayerChat");
            if (playerChat == null || !playerChat.isEnabled()) {
                throw new IllegalStateException("PlayerChat 插件未安装或未启用（公告模块不可用）");
            }
            instance = new MessageServiceApi(MessageServicePlugin.getInstance());
        }
        return instance;
    }

    /**
     * 检查MessageService是否可用
     * 合并到 PlayerChat 后，查找 "PlayerChat" 插件
     *
     * @return true如果PlayerChat已安装并启用（公告模块可用）
     */
    public static boolean isAvailable() {
        Plugin playerChat = Bukkit.getPluginManager().getPlugin("PlayerChat");
        return playerChat != null && playerChat.isEnabled();
    }

    // ==================== 公告创建 ====================

    /**
     * 创建公告
     *
     * @param announcement 公告对象
     * @return 创建结果
     * @throws AnnouncementException 如果创建失败
     */
    @NotNull
    public AnnouncementResult createAnnouncement(@NotNull Announcement announcement) throws AnnouncementException {
        return createAnnouncement(announcement, true);
    }

    /**
     * 创建公告
     *
     * @param announcement 公告对象
     * @param sync 是否同步到其他服务器
     * @return 创建结果
     * @throws AnnouncementException 如果创建失败
     */
    @NotNull
    public AnnouncementResult createAnnouncement(@NotNull Announcement announcement, boolean sync) throws AnnouncementException {
        if (announcement.getId() == null || announcement.getId().isEmpty()) {
            throw new AnnouncementException("公告ID不能为空");
        }

        AnnouncementManager manager = plugin.getAnnouncementManager();

        if (manager.exists(announcement.getId())) {
            throw new AnnouncementException("公告ID已存在: " + announcement.getId());
        }

        boolean success = manager.createAnnouncement(announcement, sync);

        if (success) {
            // 触发事件
            Bukkit.getPluginManager().callEvent(new AnnouncementCreateEvent(announcement, sync));
            return AnnouncementResult.success(announcement.getId(), "公告创建成功");
        } else {
            throw new AnnouncementException("创建公告失败");
        }
    }

    /**
     * 使用构建器创建公告
     *
     * @param builder 公告构建器
     * @return 创建结果
     * @throws AnnouncementException 如果创建失败
     */
    @NotNull
    public AnnouncementResult createAnnouncement(@NotNull AnnouncementBuilder builder) throws AnnouncementException {
        return createAnnouncement(builder.build());
    }

    /**
     * 创建公告（异步）
     *
     * @param announcement 公告对象
     * @param callback 回调函数
     */
    public void createAnnouncementAsync(@NotNull Announcement announcement, @NotNull Consumer<AnnouncementResult> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                AnnouncementResult result = createAnnouncement(announcement);
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () -> callback.accept(result));
            } catch (AnnouncementException e) {
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () ->
                    callback.accept(AnnouncementResult.failure(e.getMessage()))
                );
            }
        });
    }

    // ==================== 公告更新 ====================

    /**
     * 更新公告
     *
     * @param announcement 公告对象
     * @return 更新结果
     * @throws AnnouncementException 如果更新失败
     */
    @NotNull
    public AnnouncementResult updateAnnouncement(@NotNull Announcement announcement) throws AnnouncementException {
        return updateAnnouncement(announcement, true);
    }

    /**
     * 更新公告
     *
     * @param announcement 公告对象
     * @param sync 是否同步到其他服务器
     * @return 更新结果
     * @throws AnnouncementException 如果更新失败
     */
    @NotNull
    public AnnouncementResult updateAnnouncement(@NotNull Announcement announcement, boolean sync) throws AnnouncementException {
        if (announcement.getId() == null || announcement.getId().isEmpty()) {
            throw new AnnouncementException("公告ID不能为空");
        }

        AnnouncementManager manager = plugin.getAnnouncementManager();

        if (!manager.exists(announcement.getId())) {
            throw new AnnouncementException("公告不存在: " + announcement.getId());
        }

        boolean success = manager.updateAnnouncement(announcement, sync);

        if (success) {
            Bukkit.getPluginManager().callEvent(new AnnouncementUpdateEvent(announcement, sync));
            return AnnouncementResult.success(announcement.getId(), "公告更新成功");
        } else {
            throw new AnnouncementException("更新公告失败");
        }
    }

    /**
     * 更新公告（异步）
     *
     * @param announcement 公告对象
     * @param callback 回调函数
     */
    public void updateAnnouncementAsync(@NotNull Announcement announcement, @NotNull Consumer<AnnouncementResult> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                AnnouncementResult result = updateAnnouncement(announcement);
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () -> callback.accept(result));
            } catch (AnnouncementException e) {
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () ->
                    callback.accept(AnnouncementResult.failure(e.getMessage()))
                );
            }
        });
    }

    // ==================== 公告删除 ====================

    /**
     * 删除公告
     *
     * @param announcementId 公告ID
     * @return 删除结果
     * @throws AnnouncementException 如果删除失败
     */
    @NotNull
    public AnnouncementResult deleteAnnouncement(@NotNull String announcementId) throws AnnouncementException {
        return deleteAnnouncement(announcementId, true);
    }

    /**
     * 删除公告
     *
     * @param announcementId 公告ID
     * @param sync 是否同步到其他服务器
     * @return 删除结果
     * @throws AnnouncementException 如果删除失败
     */
    @NotNull
    public AnnouncementResult deleteAnnouncement(@NotNull String announcementId, boolean sync) throws AnnouncementException {
        AnnouncementManager manager = plugin.getAnnouncementManager();

        if (!manager.exists(announcementId)) {
            throw new AnnouncementException("公告不存在: " + announcementId);
        }

        boolean success = manager.deleteAnnouncement(announcementId, sync);

        if (success) {
            Bukkit.getPluginManager().callEvent(new AnnouncementDeleteEvent(announcementId, sync));
            return AnnouncementResult.success(announcementId, "公告删除成功");
        } else {
            throw new AnnouncementException("删除公告失败");
        }
    }

    /**
     * 删除公告（异步）
     *
     * @param announcementId 公告ID
     * @param callback 回调函数
     */
    public void deleteAnnouncementAsync(@NotNull String announcementId, @NotNull Consumer<AnnouncementResult> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                AnnouncementResult result = deleteAnnouncement(announcementId);
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () -> callback.accept(result));
            } catch (AnnouncementException e) {
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () ->
                    callback.accept(AnnouncementResult.failure(e.getMessage()))
                );
            }
        });
    }

    // ==================== 公告查询 ====================

    /**
     * 获取公告
     *
     * @param announcementId 公告ID
     * @return 公告对象（可能为空）
     */
    @NotNull
    public Optional<Announcement> getAnnouncement(@NotNull String announcementId) {
        return plugin.getAnnouncementManager().getAnnouncement(announcementId);
    }

    /**
     * 获取公告（带版本刷新）
     *
     * @param announcementId 公告ID
     * @return 公告对象（可能为空）
     */
    @NotNull
    public Optional<Announcement> getAnnouncementWithRefresh(@NotNull String announcementId) {
        return plugin.getAnnouncementManager().getAnnouncementWithRefresh(announcementId);
    }

    /**
     * 获取所有公告
     *
     * @return 公告列表
     */
    @NotNull
    public List<Announcement> getAllAnnouncements() {
        return plugin.getAnnouncementManager().getAllAnnouncements();
    }

    /**
     * 获取启用的公告
     *
     * @return 启用的公告列表
     */
    @NotNull
    public List<Announcement> getEnabledAnnouncements() {
        return plugin.getAnnouncementManager().getEnabledAnnouncements();
    }

    /**
     * 检查公告是否存在
     *
     * @param announcementId 公告ID
     * @return true如果存在
     */
    public boolean exists(@NotNull String announcementId) {
        return plugin.getAnnouncementManager().exists(announcementId);
    }

    // ==================== 公告广播 ====================

    /**
     * 广播公告
     *
     * @param announcementId 公告ID
     * @return 广播结果
     * @throws AnnouncementException 如果广播失败
     */
    @NotNull
    public BroadcastResult broadcast(@NotNull String announcementId) throws AnnouncementException {
        return broadcast(announcementId, null);
    }

    /**
     * 广播公告到指定服务器
     *
     * @param announcementId 公告ID
     * @param targetServers 目标服务器列表（null表示所有服务器）
     * @return 广播结果
     * @throws AnnouncementException 如果广播失败
     */
    @NotNull
    public BroadcastResult broadcast(@NotNull String announcementId, @Nullable List<String> targetServers) throws AnnouncementException {
        Optional<Announcement> optional = getAnnouncement(announcementId);
        if (optional.isEmpty()) {
            throw new AnnouncementException("公告不存在: " + announcementId);
        }

        Announcement announcement = optional.get();

        if (!announcement.isEnabled()) {
            throw new AnnouncementException("公告已禁用: " + announcementId);
        }

        CrossServerSyncManager syncManager = plugin.getCrossServerSyncManager();
        boolean isCrossServer = syncManager != null && syncManager.isEnabled();

        // 触发事件
        AnnouncementBroadcastEvent event = new AnnouncementBroadcastEvent(announcement, targetServers);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return BroadcastResult.cancelled("广播被事件取消");
        }

        if (targetServers == null || targetServers.isEmpty()) {
            // 广播到所有服务器
            if (isCrossServer) {
                syncManager.broadcastAnnouncementExecute(announcementId);
            } else {
                plugin.getAnnouncementService().broadcastAnnouncement(announcementId);
            }
        } else {
            // 广播到指定服务器
            if (isCrossServer) {
                syncManager.broadcastAnnouncementExecuteToServers(announcementId, targetServers);
            } else {
                // 检查当前服务器是否在目标列表中
                String currentServer = plugin.getConfig().getString("cross-server.server-name", "unknown");
                if (targetServers.contains(currentServer)) {
                    plugin.getAnnouncementService().broadcastAnnouncement(announcementId);
                }
            }
        }

        return BroadcastResult.success(announcementId, targetServers, isCrossServer);
    }

    /**
     * 广播公告（异步）
     *
     * @param announcementId 公告ID
     * @param callback 回调函数
     */
    public void broadcastAsync(@NotNull String announcementId, @NotNull Consumer<BroadcastResult> callback) {
        broadcastAsync(announcementId, null, callback);
    }

    /**
     * 广播公告（异步）
     *
     * @param announcementId 公告ID
     * @param targetServers 目标服务器列表
     * @param callback 回调函数
     */
    public void broadcastAsync(@NotNull String announcementId, @Nullable List<String> targetServers, @NotNull Consumer<BroadcastResult> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                BroadcastResult result = broadcast(announcementId, targetServers);
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () -> callback.accept(result));
            } catch (AnnouncementException e) {
                Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, () ->
                    callback.accept(BroadcastResult.failure(announcementId, e.getMessage()))
                );
            }
        });
    }

    // ==================== 临时公告 ====================

    /**
     * 发送临时公告
     *
     * @param content 内容
     * @param displayType 显示类型
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendTemporary(@NotNull String content, @NotNull Announcement.DisplayType displayType) {
        return sendTemporary(content, displayType, null, null);
    }

    /**
     * 发送临时公告
     *
     * @param content 内容
     * @param displayType 显示类型
     * @param soundName 音效名称（可选）
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendTemporary(@NotNull String content, @NotNull Announcement.DisplayType displayType, @Nullable String soundName) {
        return sendTemporary(content, displayType, soundName, null);
    }

    /**
     * 发送临时公告到指定服务器
     *
     * @param content 内容
     * @param displayType 显示类型
     * @param soundName 音效名称（可选）
     * @param targetServers 目标服务器列表（可选）
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendTemporary(@NotNull String content, @NotNull Announcement.DisplayType displayType, 
                                          @Nullable String soundName, @Nullable List<String> targetServers) {
        CrossServerSyncManager syncManager = plugin.getCrossServerSyncManager();
        boolean isCrossServer = syncManager != null && syncManager.isEnabled();

        if (targetServers == null || targetServers.isEmpty()) {
            // 发送到所有服务器
            if (isCrossServer) {
                syncManager.broadcastTemporaryAnnouncement(content, displayType, soundName);
            } else {
                // 本地发送
                sendLocalTemporary(content, displayType, soundName);
            }
        } else {
            // 发送到指定服务器
            if (isCrossServer) {
                syncManager.broadcastToSpecificServers(content, displayType, soundName, targetServers);
            } else {
                // 检查当前服务器是否在目标列表中
                String currentServer = plugin.getConfig().getString("cross-server.server-name", "unknown");
                if (targetServers.contains(currentServer)) {
                    sendLocalTemporary(content, displayType, soundName);
                }
            }
        }

        return BroadcastResult.success("TEMP_" + System.currentTimeMillis(), targetServers, isCrossServer);
    }

    /**
     * 本地发送临时公告
     */
    private void sendLocalTemporary(String content, Announcement.DisplayType displayType, String soundName) {
        Announcement tempAnnouncement = Announcement.builder()
            .id("TEMP_" + System.currentTimeMillis())
            .enabled(true)
            .priority(100)
            .content(Arrays.asList(content))
            .display(Announcement.DisplaySettings.builder()
                .type(displayType)
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
            .servers(Arrays.asList("*"))
            .version(1)
            .build();

        plugin.getAnnouncementService().executeAnnouncement(tempAnnouncement);
    }

    // ==================== 便捷方法 ====================

    /**
     * 发送聊天消息
     *
     * @param message 消息内容
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendChatMessage(@NotNull String message) {
        return sendTemporary(message, Announcement.DisplayType.CHAT);
    }

    /**
     * 发送标题
     *
     * @param title 标题内容
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendTitle(@NotNull String title) {
        return sendTemporary(title, Announcement.DisplayType.TITLE);
    }

    /**
     * 发送标题（带音效）
     *
     * @param title 标题内容
     * @param soundName 音效名称
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendTitle(@NotNull String title, @Nullable String soundName) {
        return sendTemporary(title, Announcement.DisplayType.TITLE, soundName);
    }

    /**
     * 发送副标题
     *
     * @param subtitle 副标题内容
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendSubtitle(@NotNull String subtitle) {
        // 副标题需要配合标题使用，这里发送一个空的标题
        return sendTemporary("&r", Announcement.DisplayType.TITLE);
    }

    /**
     * 发送操作栏消息
     *
     * @param message 消息内容
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendActionBar(@NotNull String message) {
        return sendTemporary(message, Announcement.DisplayType.ACTIONBAR);
    }

    /**
     * 发送Boss栏消息
     *
     * @param message 消息内容
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendBossBar(@NotNull String message) {
        return sendTemporary(message, Announcement.DisplayType.BOSSBAR);
    }

    /**
     * 发送Toast消息
     *
     * @param message 消息内容
     * @return 广播结果
     */
    @NotNull
    public BroadcastResult sendToast(@NotNull String message) {
        return sendTemporary(message, Announcement.DisplayType.TOAST);
    }

    // ==================== 状态查询 ====================

    /**
     * 检查跨服同步是否启用
     *
     * @return true如果启用
     */
    public boolean isCrossServerEnabled() {
        CrossServerSyncManager syncManager = plugin.getCrossServerSyncManager();
        return syncManager != null && syncManager.isEnabled();
    }

    /**
     * 获取当前服务器名称
     *
     * @return 服务器名称
     */
    @NotNull
    public String getServerName() {
        CrossServerSyncManager syncManager = plugin.getCrossServerSyncManager();
        if (syncManager != null && syncManager.isEnabled()) {
            return syncManager.getServerName();
        }
        return plugin.getConfig().getString("cross-server.server-name", "unknown");
    }

    /**
     * 获取公告构建器
     *
     * @return 公告构建器
     */
    @NotNull
    public AnnouncementBuilder builder() {
        return new AnnouncementBuilder();
    }

    /**
     * 获取公告构建器（带ID）
     *
     * @param id 公告ID
     * @return 公告构建器
     */
    @NotNull
    public AnnouncementBuilder builder(@NotNull String id) {
        return new AnnouncementBuilder(id);
    }

    /**
     * 获取插件版本
     *
     * @return 版本号
     */
    @NotNull
    public String getVersion() {
        return PlayerChat.INSTANCE.getDescription().getVersion();
    }
}
