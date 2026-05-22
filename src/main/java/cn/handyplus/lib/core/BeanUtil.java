package cn.handyplus.lib.core;

import cn.handyplus.lib.InitApi;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.logging.Level;

public final class BeanUtil {
   private BeanUtil() {
   }

   public static Map<String, Object> beanToMap(Object obj) {
      if (obj == null) {
         return null;
      } else {
         Map<String, Object> map = MapUtil.of();

         try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
               String key = property.getName();
               if (!"class".equals(key)) {
                  map.put(key, property.getReadMethod().invoke(obj));
               }
            }
         } catch (Exception var9) {
            InitApi.PLUGIN.getLogger().log(Level.SEVERE, "beanToMap 发生异常", (Throwable)var9);
         }

         return map;
      }
   }

   public static <T> T mapToBean(Class<T> clazz, Map<?, ?> map) {
      T obj = null;

      try {
         BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
         obj = clazz.getDeclaredConstructor().newInstance();
         PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

         for (PropertyDescriptor descriptor : propertyDescriptors) {
            String propertyName = descriptor.getName();
            if (map.containsKey(propertyName)) {
               descriptor.getWriteMethod().invoke(obj, map.get(propertyName));
            }
         }
      } catch (Exception var10) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "mapToBean 发生异常", (Throwable)var10);
      }

      return obj;
   }

   public static <T> T createBean(Class<T> clazz) {
      T obj = null;

      try {
         obj = clazz.getDeclaredConstructor().newInstance();
      } catch (Exception var3) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "createBean 发生异常", (Throwable)var3);
      }

      return obj;
   }
}
