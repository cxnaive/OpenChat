package cn.handyplus.lib.util;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.LanguageTypeEnum;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class HandyConfigUtil {
   private static final String IS_CHECK_UPDATE = "isCheckUpdate";
   private static final String IS_CHECK_UPDATE_TO_OP_MSG = "isCheckUpdateToOpMsg";

   private HandyConfigUtil() {
   }

   public static FileConfiguration loadConfig() {
      BaseConstants.CONFIG = load("config.yml");
      return setConfig(BaseConstants.CONFIG);
   }

   public static FileConfiguration loadLangConfig(boolean isI18nJson) {
      if (BaseConstants.CONFIG == null) {
         loadConfig();
      }

      return loadLangConfig(BaseConstants.CONFIG.getString("language", "zh_CN"), isI18nJson);
   }

   public static FileConfiguration loadLangConfig(String language) {
      return loadLangConfig(language, false);
   }

   public static FileConfiguration loadLangConfig(String language, boolean isI18nJson) {
      String lang = "languages/" + language + ".yml";
      FileConfiguration langConfig = load(lang);
      if (langConfig == null) {
         langConfig = load("languages/zh_CN.yml");
      }

      BaseConstants.LANG_CONFIG = langConfig;
      if (isI18nJson) {
         LanguageTypeEnum languageType = LanguageTypeEnum.getByValue(language);
         if (languageType == null) {
            initItemJson();
         } else {
            initI18nJson(languageType);
         }
      }

      return langConfig;
   }

   public static FileConfiguration load(String child) {
      File langFile = new File(InitApi.PLUGIN.getDataFolder(), child);
      if (!langFile.exists()) {
         InputStream resource = InitApi.PLUGIN.getResource(child);
         if (resource == null) {
            MessageUtil.sendConsoleMessage("加载文件异常原因:没有发现对应的文件:" + child);
            return null;
         }

         InitApi.PLUGIN.saveResource(child, false);
      }

      return YamlConfiguration.loadConfiguration(langFile);
   }

   public static Map<String, FileConfiguration> loadDirectory(String directoryStr) {
      Map<String, FileConfiguration> map = new HashMap<>();
      File directory = new File(InitApi.PLUGIN.getDataFolder(), directoryStr);
      if (!directory.exists()) {
         InitApi.PLUGIN.saveResource(directoryStr, false);
      }

      File[] spawnFileList = directory.listFiles();
      if (spawnFileList == null) {
         return map;
      } else {
         for (File file : spawnFileList) {
            map.put(file.getName(), load(directoryStr + file.getName()));
         }

         return map;
      }
   }

   public static boolean createNewFile(String fileName) {
      try {
         File file = new File(InitApi.PLUGIN.getDataFolder(), fileName);
         File parent = file.getParentFile();
         if (parent != null && !parent.exists()) {
            boolean rst = parent.mkdirs();
            MessageUtil.sendConsoleDebugMessage("创建文件目录结果: " + rst);
         }

         return !file.exists() ? file.createNewFile() : true;
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   public static File getOrCreateFile(String fileName) {
      try {
         File file = new File(InitApi.PLUGIN.getDataFolder(), fileName);
         File parent = file.getParentFile();
         if (parent != null && !parent.exists()) {
            boolean rst = parent.mkdirs();
            MessageUtil.sendConsoleDebugMessage("创建文件目录结果: " + rst);
         }

         if (!file.exists()) {
            boolean newFile = file.createNewFile();
            MessageUtil.sendConsoleDebugMessage("创建文件结果: " + newFile);
         }

         return file;
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   public static void setPath(FileConfiguration fileConfiguration, String path, Object value, String child) {
      setPath(fileConfiguration, path, value, null, child);
   }

   public static void setPath(FileConfiguration fileConfiguration, String path, Object value, List<String> comments, String child) {
      try {
         fileConfiguration.set(path, value);
         if (CollUtil.isNotEmpty(comments) && BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_18.getVersionId()) {
            fileConfiguration.setComments(path, comments);
         }

         fileConfiguration.save(new File(InitApi.PLUGIN.getDataFolder(), child));
      } catch (IOException var6) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "setPath 发生异常", (Throwable)var6);
      } catch (NoSuchMethodError var7) {
      }
   }

   public static void setPathIsNotContains(FileConfiguration fileConfiguration, String path, Object value, List<String> comments, String child) {
      if (!fileConfiguration.contains(path)) {
         setPath(fileConfiguration, path, value, comments, child);
      }
   }

   public static boolean exists(String child) {
      File file = new File(InitApi.PLUGIN.getDataFolder(), child);
      return file.exists();
   }

   public static boolean contains(FileConfiguration fileConfiguration, String path) {
      return fileConfiguration != null && !StrUtil.isEmpty(path) ? fileConfiguration.contains(path) : false;
   }

   public static Map<String, String> getStringMapChild(FileConfiguration config, String type) {
      Map<String, String> map = new LinkedHashMap<>();
      Map<String, Object> values = getChildMap(config, type);

      for (String key : values.keySet()) {
         map.put(key, config.getString(type + "." + key));
      }

      return map;
   }

   public static Map<String, List<String>> getStringListMapChild(FileConfiguration config, String type) {
      Map<String, List<String>> map = new LinkedHashMap<>();
      Map<String, Object> values = getChildMap(config, type);

      for (String key : values.keySet()) {
         map.put(key, config.getStringList(type + "." + key));
      }

      return map;
   }

   public static Map<String, Object> getChildMap(FileConfiguration config, String type) {
      if (StrUtil.isEmpty(type)) {
         return config.getValues(false);
      } else {
         Map<String, Object> map = new LinkedHashMap<>();
         ConfigurationSection configurationSection = config.getConfigurationSection(type);
         return configurationSection == null ? map : configurationSection.getValues(false);
      }
   }

   public static Set<String> getKey(FileConfiguration config, String type) {
      if (StrUtil.isEmpty(type)) {
         return config.getKeys(false);
      } else {
         Set<String> set = new HashSet<>();
         ConfigurationSection configurationSection = config.getConfigurationSection(type);
         return configurationSection == null ? set : configurationSection.getKeys(false);
      }
   }

   private static void initI18nJson(LanguageTypeEnum language) {
      File zhChFile = new File(InitApi.PLUGIN.getDataFolder(), language.getValue() + ".json");
      if (zhChFile.exists()) {
         BaseUtil.readJsonFileToJsonCacheMap(zhChFile);
      } else {
         HandyHttpUtil.getCloudI18nJson(language);
      }

      initItemJson();
      HandyHttpUtil.getCloudItem(language);
   }

   private static void initItemJson() {
      File itemFile = new File(InitApi.PLUGIN.getDataFolder(), "item.json");
      if (!itemFile.exists()) {
         InitApi.PLUGIN.saveResource("item.json", false);
      }

      try {
         BaseUtil.readJsonFileToItemJsonCacheMap(itemFile);
      } catch (Exception var2) {
         MessageUtil.sendConsoleMessage("item.json 加载失败,原因:json格式异常");
      }
   }

   private static FileConfiguration setConfig(FileConfiguration config) {
      BaseConstants.DEBUG = BaseConstants.CONFIG.getBoolean("debug");
      BaseConstants.WARN = BaseConstants.CONFIG.getBoolean("warn");
      BaseConstants.IS_CHECK_UPDATE = BaseConstants.CONFIG.getBoolean("isCheckUpdate");
      BaseConstants.IS_CHECK_UPDATE_TO_OP_MSG = BaseConstants.CONFIG.getBoolean("isCheckUpdateToOpMsg");
      return config;
   }
}
