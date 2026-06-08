package com.example.messageservice.api.event;

import com.example.messageservice.models.Announcement;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 公告广播事件
 * 当公告被广播时触发
 */
public class AnnouncementBroadcastEvent extends AnnouncementEvent implements Cancellable {

    private final Announcement announcement;
    private final List<String> targetServers;
    private boolean cancelled = false;

    /**
     * 构造函数
     *
     * @param announcement 被广播的公告
     * @param targetServers 目标服务器列表（null表示所有服务器）
     */
    public AnnouncementBroadcastEvent(@NotNull Announcement announcement, @Nullable List<String> targetServers) {
        this.announcement = announcement;
        this.targetServers = targetServers;
    }

    /**
     * 获取被广播的公告
     *
     * @return 公告对象
     */
    @NotNull
    public Announcement getAnnouncement() {
        return announcement;
    }

    /**
     * 获取目标服务器列表
     *
     * @return 目标服务器列表（null表示所有服务器）
     */
    @Nullable
    public List<String> getTargetServers() {
        return targetServers;
    }

    /**
     * 检查是否广播到所有服务器
     *
     * @return true如果广播到所有服务器
     */
    public boolean isBroadcastToAll() {
        return targetServers == null || targetServers.isEmpty();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
