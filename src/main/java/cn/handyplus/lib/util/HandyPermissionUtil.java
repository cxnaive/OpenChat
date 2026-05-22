package cn.handyplus.lib.util;

import cn.handyplus.lib.InitApi;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HandyPermissionUtil {
   private HandyPermissionUtil() {
   }

   public static Double getDoubleNumber(Player player, @NotNull FileConfiguration config, @NotNull String type) {
      return getDoubleNumber(player, config, type, ">");
   }

   public static int getIntNumber(Player player, @NotNull FileConfiguration config, @NotNull String type) {
      return getDoubleNumber(player, config, type).intValue();
   }

   public static long getLongNumber(Player player, @NotNull FileConfiguration config, @NotNull String type) {
      return getDoubleNumber(player, config, type).longValue();
   }

   public static Double getReverseDoubleNumber(Player player, @NotNull FileConfiguration config, @NotNull String type) {
      return getDoubleNumber(player, config, type, "<");
   }

   public static int getReverseIntNumber(Player player, @NotNull FileConfiguration config, @NotNull String type) {
      return getReverseDoubleNumber(player, config, type).intValue();
   }

   public static long getReverseLongNumber(Player player, @NotNull FileConfiguration config, @NotNull String type) {
      return getReverseDoubleNumber(player, config, type).longValue();
   }

   private static Double getDoubleNumber(Player player, @NotNull FileConfiguration config, @NotNull String type, @NotNull String operator) {
      double defaultNumber = config.getDouble(type + ".default", 1.0);
      ConfigurationSection configurationSection = config.getConfigurationSection(type);
      if (configurationSection == null) {
         return defaultNumber;
      } else {
         Map<String, Object> values = configurationSection.getValues(false);

         for (String key : values.keySet()) {
            if (player != null && player.hasPermission(InitApi.PLUGIN.getName() + "." + type + "." + key)) {
               double number = config.getDouble(type + "." + key);
               defaultNumber = ">".equals(operator) ? Math.max(number, defaultNumber) : Math.min(number, defaultNumber);
            }
         }

         return defaultNumber;
      }
   }
}
