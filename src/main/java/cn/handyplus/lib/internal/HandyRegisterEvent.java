package cn.handyplus.lib.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class HandyRegisterEvent extends Event {
   private static final HandlerList HANDLERS = new HandlerList();
   private final Player player;

   public HandyRegisterEvent(Player player) {
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
