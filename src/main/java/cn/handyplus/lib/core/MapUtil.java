package cn.handyplus.lib.core;

import java.util.HashMap;
import java.util.Map;

public final class MapUtil {
   private static final Integer MIN_EXPECTED_SIZE = 3;

   private MapUtil() {
   }

   public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
      return new HashMap<>(capacity(expectedSize));
   }

   public static <K, V> HashMap<K, V> of() {
      return new HashMap<>(capacity(0));
   }

   public static <K, V> HashMap<K, V> of(K k1, V v1) {
      HashMap<K, V> map = new HashMap<>(capacity(1));
      map.put(k1, v1);
      return map;
   }

   public static <K, V> HashMap<K, V> of(K k1, V v1, K k2, V v2) {
      HashMap<K, V> map = new HashMap<>(capacity(2));
      map.put(k1, v1);
      map.put(k2, v2);
      return map;
   }

   public static <K, V> HashMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
      HashMap<K, V> map = new HashMap<>(capacity(3));
      map.put(k1, v1);
      map.put(k2, v2);
      map.put(k3, v3);
      return map;
   }

   public static <K, V> HashMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
      HashMap<K, V> map = new HashMap<>(capacity(4));
      map.put(k1, v1);
      map.put(k2, v2);
      map.put(k3, v3);
      map.put(k4, v4);
      return map;
   }

   public static <K, V> HashMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
      HashMap<K, V> map = new HashMap<>(capacity(5));
      map.put(k1, v1);
      map.put(k2, v2);
      map.put(k3, v3);
      map.put(k4, v4);
      map.put(k5, v5);
      return map;
   }

   private static int capacity(int expectedSize) {
      if (expectedSize < 0) {
         expectedSize = 0;
      }

      if (expectedSize < MIN_EXPECTED_SIZE) {
         return expectedSize + 1;
      } else {
         return expectedSize < 1073741824 ? (int)(expectedSize / 0.75F + 1.0F) : Integer.MAX_VALUE;
      }
   }

   public static boolean isEmpty(Map<?, ?> map) {
      return null == map || map.isEmpty();
   }

   public static boolean isNotEmpty(Map<?, ?> map) {
      return !isEmpty(map);
   }
}
