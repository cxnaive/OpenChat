package cn.handyplus.lib.util;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.NetUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.internal.HandySchedulerUtil;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BaseUtil {
   private BaseUtil() {
   }

   public static Boolean isPlayer(@NotNull CommandSender sender) {
      return sender instanceof Player;
   }

   public static Boolean isNotPlayer(@NotNull CommandSender sender) {
      return !isPlayer(sender);
   }

   public static Optional<Player> getOnlinePlayer(@NotNull String playerName) {
      Player player = Bukkit.getPlayerExact(playerName);
      return player != null && player.isOnline() ? Optional.of(player) : Optional.empty();
   }

   public static Optional<Player> getOnlinePlayer(@NotNull UUID playerUuid) {
      Player player = Bukkit.getPlayer(playerUuid);
      return player != null && player.isOnline() ? Optional.of(player) : Optional.empty();
   }

   public static OfflinePlayer getOfflinePlayer(@NotNull String playerName) {
      return Bukkit.getOfflinePlayer(playerName);
   }

   public static OfflinePlayer getOfflinePlayer(@NotNull UUID playerUuid) {
      return Bukkit.getOfflinePlayer(playerUuid);
   }

   public static String stripColor(@Nullable String str) {
      if (StrUtil.isEmpty(str)) {
         return "";
      } else {
         return supportsComponentApi() ? ComponentUtil.stripColor(str) : ChatColor.stripColor(LegacyUtil.parseColor(str));
      }
   }

   public static List<String> stripColor(@Nullable List<String> list) {
      List<String> result = new ArrayList<>();
      if (CollUtil.isEmpty(list)) {
         return result;
      } else {
         for (String str : list) {
            result.add(stripColor(str));
         }

         return result;
      }
   }

   public static String getLangMsg(@Nullable String langKey) {
      return getLangMsg(langKey, "");
   }

   public static String getLangMsg(@Nullable String langKey, @Nullable String defaultMsg) {
      if (StrUtil.isEmpty(langKey)) {
         return defaultMsg;
      } else {
         FileConfiguration langConfig = BaseConstants.LANG_CONFIG;
         return langConfig == null ? defaultMsg : langConfig.getString(langKey, defaultMsg);
      }
   }

   public static String getLangMsg(@Nullable String langKey, @Nullable Map<String, String> replaceMap) {
      String msg = getLangMsg(langKey, "");
      if (MapUtil.isNotEmpty(replaceMap)) {
         for (String key : replaceMap.keySet()) {
            msg = StrUtil.replace(msg, key, replaceMap.get(key));
         }
      }

      return msg;
   }

   @NotNull
   public static String getDisplayName(@Nullable ItemStack itemStack) {
      if (itemStack == null) {
         itemStack = new ItemStack(Material.AIR);
      }

      ItemMeta itemMeta = ItemStackUtil.getItemMeta(itemStack);
      String type = itemStack.getType().name();
      if (itemMeta.hasDisplayName()) {
         return itemMeta.getDisplayName();
      } else if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_20_5.getVersionId() && itemMeta.hasItemName()) {
         return itemMeta.getItemName();
      } else {
         String materialTranslation = TranslationUtil.getMaterialTranslation(type);
         if (StrUtil.isNotEmpty(materialTranslation)) {
            return materialTranslation;
         } else {
            if (!BaseConstants.ITEM_JSON_CACHE_MAP.isEmpty()) {
               String name = BaseConstants.ITEM_JSON_CACHE_MAP.get(type);
               if (name != null) {
                  return name;
               }
            }

            if (!BaseConstants.CLOUD_ITEM_JSON_CACHE_MAP.isEmpty()) {
               if (itemStack.getDurability() > 0) {
                  String legacyName = BaseConstants.CLOUD_ITEM_JSON_CACHE_MAP.get(type + "_" + itemStack.getDurability());
                  if (StrUtil.isNotEmpty(legacyName)) {
                     return legacyName;
                  }
               }

               String name = BaseConstants.CLOUD_ITEM_JSON_CACHE_MAP.get(type);
               if (name != null) {
                  return name;
               }
            }

            return type;
         }
      }
   }

   public static void readJsonFileToItemJsonCacheMap(@NotNull File file) {
      Optional<String> jsonOpt = readJsonFile(file);
      if (jsonOpt.isPresent() && jsonOpt.get().length() > 1) {
         try {
            BaseConstants.ITEM_JSON_CACHE_MAP = JsonUtil.toMap(jsonOpt.get());
         } catch (Throwable var3) {
         }
      }
   }

   public static void readJsonFileToJsonCacheMap(@NotNull File file) {
      try {
         Optional<String> jsonOpt = readJsonFile(file);
         jsonOpt.ifPresent(s -> BaseConstants.JSON_CACHE_MAP = JsonUtil.toMap(s));
      } catch (Throwable var2) {
         MessageUtil.sendConsoleDebugMessage("读取item.json异常");
      }
   }

   public static Optional<String> readJsonFile(@NotNull File fileName) {
      try {
         FileReader fileReader = new FileReader(fileName);
         Reader reader = new InputStreamReader(Files.newInputStream(fileName.toPath()), StandardCharsets.UTF_8);
         StringBuilder sb = new StringBuilder();

         int ch;
         while ((ch = reader.read()) != -1) {
            sb.append((char)ch);
         }

         fileReader.close();
         reader.close();
         return Optional.of(sb.toString());
      } catch (Exception var5) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "readJsonFile 发生异常", (Throwable)var5);
         return Optional.empty();
      }
   }

   public static int getFirstPluginVersion(@NotNull Plugin plugin) {
      String version = plugin.getDescription().getVersion();
      String[] split = version.split("\\.");
      return Integer.parseInt(split[0]);
   }

   public static int getTwoPluginVersion(@NotNull Plugin plugin) {
      String version = plugin.getDescription().getVersion();
      String[] split = version.split("\\.");
      return Integer.parseInt(split[1]);
   }

   public static Integer convertVersion(@NotNull String version) {
      String cleanedVersion = version.replaceAll("[^\\d.]", "");
      if (!cleanedVersion.contains(".")) {
         return Integer.parseInt(cleanedVersion);
      } else {
         StringBuilder result = new StringBuilder();
         String[] versionParts = cleanedVersion.split("\\.");

         for (String part : versionParts) {
            if (part.length() == 1) {
               result.append("0");
            }

            result.append(part);
         }

         return Integer.parseInt(result.toString());
      }
   }

   public static void sendTip(@NotNull CommandSender sender) {
      String signType = BaseConstants.CONFIG.getString("signType", "mac");
      String mac = "mac".equalsIgnoreCase(signType) ? NetUtil.getLocalMacAddress() : HandyHttpUtil.getIp();
      if (isPlayer(sender) && BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_15.getVersionId()) {
         RgbTextUtil rgbTextUtil = RgbTextUtil.init(mac);
         String copy = getLangMsg("copy", "&r   &8[&a点击复制&8]");
         rgbTextUtil.addExtra(RgbTextUtil.init(copy).addClickCopyToClipboard(mac));
         rgbTextUtil.send(sender);
      } else {
         MessageUtil.sendMessage(sender, mac);
      }
   }

   public static boolean supportsComponentApi() {
      return (HandySchedulerUtil.isFolia() || HandySchedulerUtil.isPaper()) && BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_16_5.getVersionId();
   }

   public static boolean isHigherVersion() {
      return supportsComponentApi() && BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_20_4.getVersionId();
   }

   public static boolean isLatestVersion() {
      return supportsComponentApi() && BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_21_9.getVersionId();
   }

   public static String headComponent(@Nullable String str, @Nullable String playerName) {
      return !StrUtil.isEmpty(str) && !StrUtil.isEmpty(playerName) && isLatestVersion() ? StrUtil.replace(str, "head", "<head:" + playerName + ">") : str;
   }

   public static String spriteComponent(@Nullable String str, @NotNull ItemStack itemStack) {
      if (!StrUtil.isEmpty(str) && isLatestVersion()) {
         Material type = itemStack.getType();
         String typeName = type.name().toLowerCase();
         String spriteTag;
         if (type.isBlock()) {
            spriteTag = "<sprite:blocks:block/" + typeName + ">";
         } else {
            spriteTag = "<sprite:\"minecraft:items\":item/" + typeName + ">";
         }

         return StrUtil.replace(str, "sprite", spriteTag);
      } else {
         return str;
      }
   }
}
