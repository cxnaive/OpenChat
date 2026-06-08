package com.example.messageservice.api.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 广播操作结果
 */
public class BroadcastResult {

    private final boolean success;
    private final boolean cancelled;
    private final String announcementId;
    private final List<String> targetServers;
    private final boolean crossServer;
    private final String message;

    private BroadcastResult(boolean success, boolean cancelled, @Nullable String announcementId, 
                            @Nullable List<String> targetServers, boolean crossServer, @Nullable String message) {
        this.success = success;
        this.cancelled = cancelled;
        this.announcementId = announcementId;
        this.targetServers = targetServers;
        this.crossServer = crossServer;
        this.message = message;
    }

    /**
     * 创建成功结果
     *
     * @param announcementId 公告ID
     * @param targetServers 目标服务器列表
     * @param crossServer 是否跨服
     * @return 结果对象
     */
    @NotNull
    public static BroadcastResult success(@NotNull String announcementId, @Nullable List<String> targetServers, boolean crossServer) {
        return new BroadcastResult(true, false, announcementId, targetServers, crossServer, "广播成功");
    }

    /**
     * 创建失败结果
     *
     * @param announcementId 公告ID
     * @param message 错误信息
     * @return 结果对象
     */
    @NotNull
    public static BroadcastResult failure(@NotNull String announcementId, @NotNull String message) {
        return new BroadcastResult(false, false, announcementId, null, false, message);
    }

    /**
     * 创建取消结果
     *
     * @param message 取消原因
     * @return 结果对象
     */
    @NotNull
    public static BroadcastResult cancelled(@NotNull String message) {
        return new BroadcastResult(false, true, null, null, false, message);
    }

    /**
     * 检查是否成功
     *
     * @return true如果成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 检查是否被取消
     *
     * @return true如果被取消
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * 获取公告ID
     *
     * @return 公告ID（可能为空）
     */
    @Nullable
    public String getAnnouncementId() {
        return announcementId;
    }

    /**
     * 获取目标服务器列表
     *
     * @return 目标服务器列表（可能为空）
     */
    @Nullable
    public List<String> getTargetServers() {
        return targetServers;
    }

    /**
     * 检查是否跨服广播
     *
     * @return true如果跨服
     */
    public boolean isCrossServer() {
        return crossServer;
    }

    /**
     * 获取信息
     *
     * @return 信息（可能为空）
     */
    @Nullable
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "BroadcastResult{" +
                "success=" + success +
                ", cancelled=" + cancelled +
                ", announcementId='" + announcementId + '\'' +
                ", crossServer=" + crossServer +
                ", message='" + message + '\'' +
                '}';
    }
}
