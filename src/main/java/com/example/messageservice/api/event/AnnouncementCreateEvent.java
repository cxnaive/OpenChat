package com.example.messageservice.api.event;

import com.example.messageservice.models.Announcement;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * 公告创建事件
 * 当公告被创建时触发
 */
public class AnnouncementCreateEvent extends AnnouncementEvent implements Cancellable {

    private final Announcement announcement;
    private final boolean sync;
    private boolean cancelled = false;

    /**
     * 构造函数
     *
     * @param announcement 被创建的公告
     * @param sync 是否同步到其他服务器
     */
    public AnnouncementCreateEvent(@NotNull Announcement announcement, boolean sync) {
        this.announcement = announcement;
        this.sync = sync;
    }

    /**
     * 获取被创建的公告
     *
     * @return 公告对象
     */
    @NotNull
    public Announcement getAnnouncement() {
        return announcement;
    }

    /**
     * 检查是否同步到其他服务器
     *
     * @return true如果同步
     */
    public boolean isSync() {
        return sync;
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
