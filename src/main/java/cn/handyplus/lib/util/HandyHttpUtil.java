package cn.handyplus.lib.util;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.LanguageTypeEnum;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.HttpUtil;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.internal.HandyRunnable;
import cn.handyplus.lib.internal.HandySchedulerUtil;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.bukkit.entity.Player;

public final class HandyHttpUtil {
   private static final String CLOUD_GET_URL = "https://admin.ljxmc.top/api/public/getItemName";
   private static final String IP_CHINA_URL = "https://admin.ljxmc.top/api/public/getIp";
   private static final String VERSION_URL = "https://ricedoc.handyplus.cn/version.json";

   private HandyHttpUtil() {
   }

   public static void checkVersion(Player player) {
      if (player != null) {
         if (!player.isOp()) {
            return;
         }

         if (!BaseConstants.IS_CHECK_UPDATE_TO_OP_MSG) {
            return;
         }
      }

      HandySchedulerUtil.runTaskAsynchronously(() -> {
         String version = InitApi.PLUGIN.getDescription().getVersion();
         Optional<String> tagNameOpt = getOfficialVersion();
         if (tagNameOpt.isPresent()) {
            if (BaseUtil.convertVersion(tagNameOpt.get()) > BaseUtil.convertVersion(version)) {
               String oneMsg = "&7_________________/ &e" + InitApi.PLUGIN.getDescription().getName() + "&7 \\_________________\n";
               RgbTextUtil rgbTextUtil = RgbTextUtil.init(oneMsg);
               String twoMsg = "&7| &a最新版本: &d" + tagNameOpt.get() + " &a当前版本: &d" + version + " &a点击&d&n此处查看&a更新内容 &7|\n";
               rgbTextUtil.addExtra(RgbTextUtil.init("     " + twoMsg).addClickUrl(InitApi.PLUGIN.getDescription().getWebsite()));
               rgbTextUtil.addExtra(RgbTextUtil.init("&7-----------------------------------------------"));
               if (player == null) {
                  rgbTextUtil.sendConsole();
               } else {
                  rgbTextUtil.send(player);
               }
            }
         }
      });
   }

   public static void getCloudItem(final LanguageTypeEnum language) {
      if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_13.getVersionId()) {
         final AtomicInteger retryNumber = new AtomicInteger(6);
         HandyRunnable handyRunnable = new HandyRunnable() {
            @Override
            public void run() {
               try {
                  HashMap<String, String> paramMap = Maps.newHashMapWithExpectedSize(1);
                  paramMap.put("version", BaseConstants.VERSION_CHECK_ENUM.getMainVersion());
                  paramMap.put("language", language.getValue());
                  String result = HttpUtil.get("https://admin.ljxmc.top/api/public/getItemName", paramMap);
                  if (StrUtil.isNotEmpty(result)) {
                     BaseConstants.CLOUD_ITEM_JSON_CACHE_MAP = JsonUtil.toMap(result);
                  }

                  this.cancel();
               } catch (Throwable var3) {
                  retryNumber.getAndDecrement();
                  if (retryNumber.get() < 1) {
                     this.cancel();
                  }
               }
            }
         };
         HandySchedulerUtil.runTaskTimerAsynchronously(handyRunnable, 60L, 1200L);
      }
   }

   public static String getIp() {
      try {
         return HttpUtil.get("https://admin.ljxmc.top/api/public/getIp");
      } catch (Exception var1) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "getIp 发生异常,请联系开发者", (Throwable)var1);
         return "";
      }
   }

   public static void getCloudI18nJson(final LanguageTypeEnum language) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_13.getVersionId()) {
         final AtomicInteger retryNumber = new AtomicInteger(6);
         HandyRunnable handyRunnable = new HandyRunnable() {
            @Override
            public void run() {
               try {
                  HttpUtil.downloadFile(language.getUrl(), InitApi.PLUGIN.getDataFolder(), language.getValue() + ".json");
                  File i18nFile = new File(InitApi.PLUGIN.getDataFolder(), language.getValue() + ".json");
                  if (i18nFile.exists()) {
                     BaseUtil.readJsonFileToJsonCacheMap(i18nFile);
                  }

                  this.cancel();
               } catch (Exception var2) {
                  retryNumber.getAndDecrement();
                  if (retryNumber.get() < 1) {
                     this.cancel();
                  }
               }
            }
         };
         HandySchedulerUtil.runTaskTimerAsynchronously(handyRunnable, 40L, 1200L);
      }
   }

   private static Optional<String> getOfficialVersion() {
      try {
         String result = HttpUtil.get("https://ricedoc.handyplus.cn/version.json");
         if (StrUtil.isEmpty(result)) {
            return Optional.empty();
         } else {
            JsonObject jsonObject = JsonUtil.toBean(result, JsonObject.class);
            return Optional.ofNullable(jsonObject.get(InitApi.PLUGIN.getDescription().getName()).getAsString());
         }
      } catch (Throwable var2) {
         return Optional.empty();
      }
   }
}
