package cn.handyplus.lib.constants;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

public final class BaseConstants {
   public static final int GUI_SIZE_54 = 54;
   public static final int GUI_SIZE_27 = 27;
   public static final String CLASS = "class";
   public static Map<String, String> JSON_CACHE_MAP = new HashMap<>();
   public static Map<String, String> ITEM_JSON_CACHE_MAP = new HashMap<>();
   public static Map<String, String> CLOUD_ITEM_JSON_CACHE_MAP = new HashMap<>();
   public static FileConfiguration CONFIG;
   public static FileConfiguration LANG_CONFIG;
   public static FileConfiguration STORAGE_CONFIG;
   public static boolean DEBUG = false;
   public static boolean WARN = false;
   public static VersionCheckEnum VERSION_CHECK_ENUM;
   public static Integer VERSION_ID;
   public static boolean IS_CHECK_UPDATE = false;
   public static boolean IS_CHECK_UPDATE_TO_OP_MSG = false;
   public static final String POINT = ".";
   public static final String COLON = ":";
   public static final String MAC = "mac";
   public static final String SIGN_TYPE = "signType";
   public static final int DEFAULT_DIV_SCALE = 2;
   public static final String PREVIOUS_PAGE = "previousPage";
   public static final String NEXT_PAGE = "nextPage";
   public static final int DROP_ITEM_BATCH_THRESHOLD = 300;
   public static final int DROP_ITEM_BATCH_SIZE = 64;

   private BaseConstants() {
   }
}
