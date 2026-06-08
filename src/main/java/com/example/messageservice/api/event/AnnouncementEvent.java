package com.example.messageservice.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * 公告事件基类
 */
public abstract class AnnouncementEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public AnnouncementEvent() {
        super();
    }

    public AnnouncementEvent(boolean isAsync) {
        super(isAsync);
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
