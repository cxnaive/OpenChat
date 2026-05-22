package cn.handyplus.lib.internal.base;

import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

class XNamespacedKey {
   private static final boolean SUPPORTS_NamespacedKey_fromString;

   private static boolean isValidNamespaceChar(char c) {
      return c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.' || c == '_' || c == '-';
   }

   private static boolean isValidKeyChar(char c) {
      return isValidNamespaceChar(c) || c == '/';
   }

   private static boolean isValidNamespace(String namespace) {
      int len = namespace.length();
      if (len == 0) {
         return false;
      } else {
         for (int i = 0; i < len; i++) {
            if (!isValidNamespaceChar(namespace.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isValidKey(String key) {
      int len = key.length();
      if (len == 0) {
         return false;
      } else {
         for (int i = 0; i < len; i++) {
            if (!isValidKeyChar(key.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   protected static NamespacedKey fromString(@NotNull String string) {
      if (SUPPORTS_NamespacedKey_fromString) {
         return NamespacedKey.fromString(string);
      } else {
         Preconditions.checkArgument(string != null && !string.isEmpty(), "Input string must not be empty or null");
         String[] components = string.split(":", 3);
         if (components.length > 2) {
            return null;
         } else {
            String key = components.length == 2 ? components[1] : "";
            if (components.length == 1) {
               String namespace = components[0];
               return !namespace.isEmpty() && isValidKey(namespace) ? NamespacedKey.minecraft(namespace) : null;
            } else if (components.length == 2 && !isValidKey(key)) {
               return null;
            } else {
               String namespace = components[0];
               if (namespace.isEmpty()) {
                  return NamespacedKey.minecraft(key);
               } else {
                  return !isValidNamespace(namespace) ? null : new NamespacedKey(namespace, key);
               }
            }
         }
      }
   }

   static {
      boolean supportsFromString;
      try {
         Class<?> NamespacedKey = Class.forName("org.bukkit.NamespacedKey");
         NamespacedKey.getDeclaredMethod("fromString", String.class);
         supportsFromString = true;
      } catch (Throwable var2) {
         supportsFromString = false;
      }

      SUPPORTS_NamespacedKey_fromString = supportsFromString;
   }
}
