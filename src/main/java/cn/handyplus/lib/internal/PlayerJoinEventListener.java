package cn.handyplus.lib.internal;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {
   @EventHandler
   public void onLoginEvent(PlayerJoinEvent event) {
      Bukkit.getServer().getPluginManager().callEvent(new HandyLoginEvent(event.getPlayer()));
   }
}
