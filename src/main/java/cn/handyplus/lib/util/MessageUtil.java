package cn.handyplus.lib.util;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageUtil {
   private MessageUtil() {
   }

   public static void sendMessage(UUID playerUuid, String msg) {
      Optional<Player> playerOpt = BaseUtil.getOnlinePlayer(playerUuid);
      playerOpt.ifPresent(player -> sendMessage(player, msg));
   }

   public static void sendMessage(String playerName, String msg) {
      Optional<Player> playerOpt = BaseUtil.getOnlinePlayer(playerName);
      playerOpt.ifPresent(player -> sendMessage(player, msg));
   }

   public static void sendMessage(Player player, String msg) {
      sendMessage(true, player, msg);
   }

   public static void sendMessage(Player player, List<String> msgList) {
      if (!CollUtil.isEmpty(msgList)) {
         msgList.forEach(msg -> sendMessage(player, msg));
      }
   }

   public static void sendMessage(boolean rst, Player player, String msg) {
      if (rst && !StrUtil.isEmpty(msg)) {
         if (BaseUtil.supportsComponentApi()) {
            player.sendMessage(ComponentUtil.parseColor(msg));
         } else {
            player.sendMessage(LegacyUtil.parseColor(msg));
         }
      }
   }

   public static void sendDebugMessage(Player player, String msg) {
      if (BaseConstants.DEBUG) {
         sendMessage(player, msg);
      }
   }

   public static void sendWarnMessage(CommandSender sender, String msg) {
      if (BaseConstants.WARN) {
         sendMessage(sender, msg);
      }
   }

   public static void sendDebugMessage(CommandSender sender, String msg) {
      if (BaseConstants.DEBUG) {
         sendMessage(sender, msg);
      }
   }

   public static void sendMessage(CommandSender sender, String msg) {
      if (!StrUtil.isEmpty(msg) && sender != null) {
         if (BaseUtil.supportsComponentApi()) {
            sender.sendMessage(ComponentUtil.parseColor(msg));
         } else {
            sender.sendMessage(LegacyUtil.parseColor(msg));
         }
      }
   }

   public static void sendAllMessage(String msg) {
      if (!StrUtil.isEmpty(msg)) {
         Bukkit.getOnlinePlayers().forEach(player -> sendMessage(player, msg));
      }
   }

   public static void sendAllMessage(List<String> msgList) {
      if (!CollUtil.isEmpty(msgList)) {
         msgList.forEach(MessageUtil::sendAllMessage);
      }
   }

   public static void sendConsoleMessage(String msg) {
      sendConsoleMessage(true, msg);
   }

   public static void sendConsoleMessage(boolean rst, String msg) {
      if (rst && !StrUtil.isEmpty(msg)) {
         String fullMessage = "&7[" + InitApi.PLUGIN.getName() + "] &r" + msg;
         if (BaseUtil.supportsComponentApi()) {
            Bukkit.getConsoleSender().sendMessage(ComponentUtil.parseColor(fullMessage));
         } else {
            Bukkit.getConsoleSender().sendMessage(LegacyUtil.parseColor(fullMessage));
         }
      }
   }

   public static void sendConsoleMessage(List<String> msgList) {
      if (!CollUtil.isEmpty(msgList)) {
         msgList.forEach(MessageUtil::sendConsoleMessage);
      }
   }

   public static void sendConsoleWarnMessage(String msg) {
      if (BaseConstants.WARN) {
         sendConsoleMessage(msg);
      }
   }

   public static void sendConsoleDebugMessage(String msg) {
      if (BaseConstants.DEBUG) {
         sendConsoleMessage(msg);
      }
   }

   public static void sendTitle(Player player, String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_9.getVersionId()) {
         if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_11.getVersionId()) {
            player.sendTitle(LegacyUtil.parseColor(title), LegacyUtil.parseColor(subtitle));
         } else if (BaseConstants.VERSION_ID > VersionCheckEnum.V_1_10.getVersionId() && !BaseUtil.supportsComponentApi()) {
            player.sendTitle(LegacyUtil.parseColor(title), LegacyUtil.parseColor(subtitle), fadeInTicks, stayTicks, fadeOutTicks);
         } else if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_21_8.getVersionId()) {
            player.showTitle(
               Title.title(
                  ComponentUtil.parseColor(title),
                  ComponentUtil.parseColor(subtitle),
                  Times.of(Ticks.duration(fadeInTicks), Ticks.duration(stayTicks), Ticks.duration(fadeOutTicks))
               )
            );
         } else {
            player.showTitle(Title.title(ComponentUtil.parseColor(title), ComponentUtil.parseColor(subtitle), fadeInTicks, stayTicks, fadeOutTicks));
         }
      }
   }

   public static void sendTitle(Player player, String title, String subtitle) {
      sendTitle(player, title, subtitle, 10, 70, 20);
   }

   public static void sendAllTitle(String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
      Bukkit.getOnlinePlayers().forEach(player -> sendTitle(player, title, subtitle, fadeInTicks, stayTicks, fadeOutTicks));
   }

   public static void sendAllTitle(String title, String subtitle) {
      Bukkit.getOnlinePlayers().forEach(player -> sendTitle(player, title, subtitle));
   }
}
