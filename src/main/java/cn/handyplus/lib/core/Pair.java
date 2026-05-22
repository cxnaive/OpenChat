package cn.handyplus.lib.core;

import lombok.Generated;

public class Pair<K, V> {
   private final K key;
   private final V value;

   public static <K, V> Pair<K, V> of(K key, V value) {
      return new Pair<>(key, value);
   }

   private Pair(K key, V value) {
      this.key = key;
      this.value = value;
   }

   @Generated
   public K getKey() {
      return this.key;
   }

   @Generated
   public V getValue() {
      return this.value;
   }
}
