package cn.handyplus.lib.internal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class LoginCompatUtil {

   public static void init(Plugin plugin) {
      Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), plugin);
   }

}
