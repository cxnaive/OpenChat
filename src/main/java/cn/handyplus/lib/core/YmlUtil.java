package cn.handyplus.lib.core;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.util.HandyConfigUtil;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.yaml.snakeyaml.Yaml;

public final class YmlUtil {
   private YmlUtil() {
   }

   public static void beanToYml(Object obj, String fileName) {
      try {
         beanToYml(obj, fileName, null);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public static <T> T ymlToBean(String fileName, Class<T> clazz) {
      try {
         return ymlToBean(fileName, clazz, null);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public static void beanToYml(Object obj, String fileName, String prefix) {
      try {
         fileName = setYml(fileName);
         HandyConfigUtil.createNewFile(fileName);
         FileConfiguration ymlConfig = HandyConfigUtil.load(fileName);
         if (ymlConfig != null) {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
               String key = property.getName();
               if (!"class".equalsIgnoreCase(key)) {
                  if (StrUtil.isNotEmpty(prefix)) {
                     key = prefix + "." + key;
                  }

                  HandyConfigUtil.setPathIsNotContains(ymlConfig, key, property.getReadMethod().invoke(obj), null, fileName);
               }
            }
         }
      } catch (Throwable var11) {
         throw new RuntimeException(var11);
      }
   }

   public static <T> T ymlToBean(String fileName, Class<T> clazz, String prefix) {
      try {
         fileName = setYml(fileName);
         FileConfiguration ymlConfig = HandyConfigUtil.load(fileName);
         T obj = BeanUtil.createBean(clazz);
         BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
         PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

         for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (!"class".equalsIgnoreCase(key)) {
               if (StrUtil.isNotEmpty(prefix)) {
                  key = prefix + "." + key;
               }

               if (ymlConfig.contains(key)) {
                  property.getWriteMethod().invoke(obj, ymlConfig.get(key));
               }
            }
         }

         return obj;
      } catch (Throwable var12) {
         throw new RuntimeException(var12);
      }
   }

   public static Map<String, Object> ymlToMap(String fileName) {
      try {
         return (Map<String, Object>)new Yaml().load(new BufferedReader(new FileReader(InitApi.PLUGIN.getDataFolder() + "/" + setYml(fileName))));
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static String setYml(String fileName) {
      if (!fileName.contains(".yml")) {
         fileName = fileName + ".yml";
      }

      return fileName;
   }
}
