package cn.handyplus.lib.db;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.core.StrUtil;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DbColumnUtil {
   private static final Map<Class<?>, String> FIELD_NAME_CACHE = new ConcurrentHashMap<>();

   private DbColumnUtil() {
   }

   protected static <T> String getFieldName(DbFunction<T, ?> fn) {
      Class<?> fnClass = fn.getClass();
      String cached = FIELD_NAME_CACHE.get(fnClass);
      if (cached != null) {
         return cached;
      } else {
         Method writeReplaceMethod;
         try {
            writeReplaceMethod = fnClass.getDeclaredMethod("writeReplace");
         } catch (NoSuchMethodException var12) {
            throw new RuntimeException(var12);
         }

         boolean isAccessible = writeReplaceMethod.isAccessible();
         writeReplaceMethod.setAccessible(true);

         SerializedLambda serializedLambda;
         try {
            serializedLambda = (SerializedLambda)writeReplaceMethod.invoke(fn);
         } catch (InvocationTargetException | IllegalAccessException var11) {
            throw new RuntimeException(var11);
         }

         writeReplaceMethod.setAccessible(isAccessible);
         String fieldName = serializedLambda.getImplMethodName().substring("get".length());
         fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());

         Field field;
         try {
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
         } catch (NoSuchFieldException | ClassNotFoundException var10) {
            throw new RuntimeException(var10);
         }

         TableField tableField = field.getAnnotation(TableField.class);
         if (tableField != null && !StrUtil.isEmpty(tableField.value())) {
            String tableFieldName = tableField.value();
            FIELD_NAME_CACHE.put(fnClass, tableFieldName);
            return tableFieldName;
         } else {
            throw new RuntimeException("TableField 为空");
         }
      }
   }
}
