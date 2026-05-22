package cn.handyplus.lib.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class HandyLoginEvent extends Event {
   private static final HandlerList HANDLERS = new HandlerList();
   private final Player player;

   public HandyLoginEvent(Player player) {
      this.player = player;
   }

   public @NonNull HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }

   public Player getPlayer() {
      return this.player;
   }
}
