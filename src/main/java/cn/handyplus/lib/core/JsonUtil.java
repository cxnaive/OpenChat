package cn.handyplus.lib.core;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class JsonUtil {
   private static final Gson GSON = new Gson();
   private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Type STRING_MAP_TYPE = (new TypeToken<Map<String, String>>() {}).getType();
   private static final Type INT_STRING_MAP_TYPE = (new TypeToken<Map<Integer, String>>() {}).getType();
   private static final Type OBJ_MAP_TYPE = (new TypeToken<Map<String, Object>>() {}).getType();

   private JsonUtil() {
   }

   public static String toJson(@NotNull Object obj) {
      return GSON.toJson(obj);
   }

   public static <T> T toBean(@NotNull String json, @NotNull Class<T> t) {
      return (T)GSON.fromJson(json, t);
   }

   public static <T> List<T> toList(@NotNull String json, @NotNull Class<T> t) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_12.getVersionId()) {
         return (List<T>)GSON.fromJson(json, TypeToken.getParameterized(List.class, new Type[]{t}).getType());
      } else {
         JsonArray arr = new JsonParser().parse(json).getAsJsonArray();
         List<T> list = new ArrayList<>(arr.size());

         for (JsonElement el : arr) {
            list.add((T)GSON.fromJson(el, t));
         }

         return list;
      }
   }

   public static Map<String, String> toMap(@NotNull String json) {
      return (Map<String, String>)GSON.fromJson(json, STRING_MAP_TYPE);
   }

   public static Map<Integer, String> toIntMap(@NotNull String json) {
      return (Map<Integer, String>)GSON.fromJson(json, INT_STRING_MAP_TYPE);
   }

   public static Map<String, Object> toObjMap(@NotNull String json) {
      return (Map<String, Object>)GSON.fromJson(json, OBJ_MAP_TYPE);
   }

   public static Map<String, String> objectToMap(@NotNull Object obj) {
      return toMap(toJson(obj));
   }

   public static <T> boolean writeListToJson(@NotNull List<T> list, File file) {
      try {
         FileWriter writer = new FileWriter(file);

         boolean var3;
         try {
            PRETTY_GSON.toJson(list, writer);
            var3 = true;
         } catch (Throwable var6) {
            try {
               writer.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw new RuntimeException(var6);
         }

         writer.close();
         return var3;
      } catch (Throwable var7) {
         throw new RuntimeException(var7);
      }
   }

   public static boolean isJson(String str) {
      return StrUtil.isEmpty(str) ? false : isTypeJsonObject(str) || isTypeJsonArray(str);
   }

   public static boolean isTypeJsonObject(String str) {
      return StrUtil.isEmpty(str) ? false : StrUtil.isWrap(str, '{', '}');
   }

   public static boolean isTypeJsonArray(String str) {
      return StrUtil.isEmpty(str) ? false : StrUtil.isWrap(str, '[', ']');
   }
}
