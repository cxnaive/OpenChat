package cn.handyplus.lib.expand;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.internal.HandyRunnable;
import cn.handyplus.lib.internal.HandySchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.LegacyUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class BossBarUtil {
   private BossBarUtil() {
   }

   public static KeyedBossBar createBossBar(String title) {
      KeyedBossBar bossBar = Bukkit.createBossBar(
         new NamespacedKey(InitApi.PLUGIN, UUID.randomUUID().toString()), LegacyUtil.parseColor(title), BarColor.PINK, BarStyle.SOLID, new BarFlag[0]
      );
      bossBar.setProgress(1.0);
      return bossBar;
   }

   public static void setStyle(KeyedBossBar bossBar, BarStyle barStyle) {
      bossBar.setStyle(barStyle);
   }

   public static void setColor(KeyedBossBar bossBar, BarColor barColor) {
      bossBar.setColor(barColor);
   }

   public static void setProgress(KeyedBossBar bossBar, double progress) {
      bossBar.setProgress(progress);
   }

   public static void setProgress(final NamespacedKey namespacedKey, Integer delay) {
      final BigDecimal bigDecimal = BigDecimal.valueOf(0.1 / delay.intValue()).setScale(5, RoundingMode.HALF_UP);
      final BigDecimal[] one = new BigDecimal[]{BigDecimal.ONE};
      HandyRunnable handyRunnable = new HandyRunnable() {
         @Override
         public void run() {
            BossBar bossBar = BossBarUtil.getBossBar(namespacedKey);
            if (bossBar != null && bossBar.isVisible() && one[0].compareTo(BigDecimal.ZERO) > 0) {
               one[0] = one[0].subtract(bigDecimal);
               if (one[0].compareTo(BigDecimal.ZERO) <= 0) {
                  one[0] = BigDecimal.ZERO;
               }

               bossBar.setProgress(one[0].doubleValue());
            } else {
               this.cancel();
            }
         }
      };
      HandySchedulerUtil.runTaskTimer(handyRunnable, 2L, 2L);
   }

   public static KeyedBossBar createBossBar(FileConfiguration config, String type, String title) {
      String color = config.getString(type + ".color", "PINK");
      String style = config.getString(type + ".style", "SOLID");
      BarColor barColor = BarColor.valueOf(color.toUpperCase());
      BarStyle barStyle = BarStyle.valueOf(style.toUpperCase());
      return createBossBar(title, barColor, barStyle);
   }

   public static KeyedBossBar createBossBar(String title, BarColor barColor, BarStyle barStyle) {
      KeyedBossBar bossBar = Bukkit.createBossBar(
         new NamespacedKey(InitApi.PLUGIN, UUID.randomUUID().toString()), LegacyUtil.parseColor(title), barColor, barStyle, new BarFlag[0]
      );
      bossBar.setProgress(1.0);
      return bossBar;
   }

   public static KeyedBossBar createBossBar(String title, BarColor barColor, BarStyle barStyle, BarFlag barFlag) {
      KeyedBossBar bossBar = Bukkit.createBossBar(
         new NamespacedKey(InitApi.PLUGIN, UUID.randomUUID().toString()), LegacyUtil.parseColor(title), barColor, barStyle, new BarFlag[]{barFlag}
      );
      bossBar.setProgress(1.0);
      return bossBar;
   }

   public static void addPlayer(NamespacedKey namespacedKey, Player player) {
      addPlayer(namespacedKey, Collections.singletonList(player));
   }

   public static void addPlayer(NamespacedKey namespacedKey, String playerName) {
      Optional<Player> playerOpt = BaseUtil.getOnlinePlayer(playerName);
      playerOpt.ifPresent(player -> addPlayer(namespacedKey, player));
   }

   public static void addPlayer(NamespacedKey namespacedKey, UUID playerUuid) {
      Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerUuid);
      onlinePlayer.ifPresent(player -> addPlayer(namespacedKey, player));
   }

   public static void addPlayerByUuid(NamespacedKey namespacedKey, List<UUID> playerUuidList) {
      for (UUID playerUuid : playerUuidList) {
         addPlayer(namespacedKey, playerUuid);
      }
   }

   public static void addPlayerByName(NamespacedKey namespacedKey, List<String> playerNameList) {
      for (String playerName : playerNameList) {
         addPlayer(namespacedKey, playerName);
      }
   }

   public static void addPlayer(NamespacedKey namespacedKey, List<Player> playerList) {
      BossBar bossBar = getBossBar(namespacedKey);
      if (bossBar != null) {
         for (Player player : playerList) {
            bossBar.addPlayer(player);
         }
      }
   }

   public static void addAllPlayer(KeyedBossBar bossBar) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
         bossBar.addPlayer(onlinePlayer);
      }
   }

   public static void removePlayer(NamespacedKey namespacedKey, Player player) {
      removePlayer(namespacedKey, Collections.singletonList(player));
   }

   public static void removePlayer(NamespacedKey namespacedKey, String playerName) {
      Optional<Player> playerOpt = BaseUtil.getOnlinePlayer(playerName);
      playerOpt.ifPresent(player -> removePlayer(namespacedKey, player));
   }

   public static void removePlayer(NamespacedKey namespacedKey, UUID playerUuid) {
      Optional<Player> playerOpt = BaseUtil.getOnlinePlayer(playerUuid);
      playerOpt.ifPresent(player -> removePlayer(namespacedKey, player));
   }

   public static void removePlayerByUuid(NamespacedKey namespacedKey, List<UUID> playerUuidList) {
      for (UUID playerUuid : playerUuidList) {
         removePlayer(namespacedKey, playerUuid);
      }
   }

   public static void removePlayerByName(NamespacedKey namespacedKey, List<String> playerNameList) {
      for (String playerName : playerNameList) {
         removePlayer(namespacedKey, playerName);
      }
   }

   public static void removePlayer(NamespacedKey namespacedKey, List<Player> playerList) {
      BossBar bossBar = getBossBar(namespacedKey);
      if (bossBar != null) {
         for (Player player : playerList) {
            bossBar.removePlayer(player);
         }
      }
   }

   public static BossBar getBossBar(NamespacedKey namespacedKey) {
      return Bukkit.getBossBar(namespacedKey);
   }

   public static void removeBossBar(NamespacedKey namespacedKey) {
      Bukkit.removeBossBar(namespacedKey);
   }

   public static void removeBossBar(NamespacedKey namespacedKey, Integer delay) {
      HandySchedulerUtil.runTaskLater(() -> {
         BossBar bossBar = getBossBar(namespacedKey);
         bossBar.removeAll();
         removeBossBar(namespacedKey);
      }, (long)(delay * 20));
   }

   public static void removeAllBossBar() {
      Iterator<KeyedBossBar> bossBars = Bukkit.getBossBars();

      while (bossBars.hasNext()) {
         KeyedBossBar keyedBossBar = bossBars.next();
         keyedBossBar.removeAll();
         removeBossBar(keyedBossBar.getKey());
      }
   }
}
