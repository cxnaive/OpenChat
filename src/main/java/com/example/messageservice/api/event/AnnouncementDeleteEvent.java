package com.example.messageservice.api.event;

import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * 公告删除事件
 * 当公告被删除时触发
 */
public class AnnouncementDeleteEvent extends AnnouncementEvent implements Cancellable {

    private final String announcementId;
    private final boolean sync;
    private boolean cancelled = false;

    /**
     * 构造函数
     *
     * @param announcementId 被删除的公告ID
     * @param sync 是否同步到其他服务器
     */
    public AnnouncementDeleteEvent(@NotNull String announcementId, boolean sync) {
        this.announcementId = announcementId;
        this.sync = sync;
    }

    /**
     * 获取被删除的公告ID
     *
     * @return 公告ID
     */
    @NotNull
    public String getAnnouncementId() {
        return announcementId;
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
