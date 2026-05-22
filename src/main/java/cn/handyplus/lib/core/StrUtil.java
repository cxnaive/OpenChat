package cn.handyplus.lib.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StrUtil {
   private StrUtil() {
   }

   public static boolean isEmpty(@Nullable CharSequence str) {
      return str == null || str.length() == 0;
   }

   public static boolean isNotEmpty(@Nullable CharSequence str) {
      return !isEmpty(str);
   }

   @Nullable
   public static String toLowerCase(@Nullable String str) {
      return str != null ? str.toLowerCase() : null;
   }

   @Nullable
   public static String replaceSpace(@Nullable String str) {
      return isEmpty(str) ? str : str.replace("#", " ");
   }

   @NotNull
   public static List<String> strToStrList(@Nullable String str) {
      return strToStrList(str, ",");
   }

   @NotNull
   public static List<String> strToStrList(@Nullable String str, @NotNull String split) {
      List<String> list = new ArrayList<>();
      return isEmpty(str) ? list : Arrays.stream(str.split(split)).map(String::trim).collect(Collectors.toList());
   }

   @NotNull
   public static List<Long> strToLongList(@Nullable String str) {
      List<Long> list = new ArrayList<>();
      return isEmpty(str) ? list : Arrays.stream(str.split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
   }

   @NotNull
   public static List<Integer> strToIntList(@Nullable String str) {
      List<Integer> list = new ArrayList<>();
      return isEmpty(str) ? list : Arrays.stream(str.split(",")).map(s -> Integer.valueOf(s.trim())).collect(Collectors.toList());
   }

   @NotNull
   public static List<Double> strToDoubleList(@Nullable String str) {
      List<Double> list = new ArrayList<>();
      return isEmpty(str) ? list : Arrays.stream(str.split(",")).map(s -> Double.valueOf(s.trim())).collect(Collectors.toList());
   }

   @NotNull
   public static String lineToHump(@NotNull String str) {
      str = str.toLowerCase();
      Matcher matcher = PatternUtil.LINE_PATTERN.matcher(str);
      StringBuffer sb = new StringBuffer();

      while (matcher.find()) {
         matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
      }

      matcher.appendTail(sb);
      return sb.toString();
   }

   @NotNull
   public static String humpToLine(@NotNull String str) {
      Matcher matcher = PatternUtil.HUMP_PATTERN.matcher(str);
      StringBuffer sb = new StringBuffer();

      while (matcher.find()) {
         matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
      }

      matcher.appendTail(sb);
      return sb.toString();
   }

   @Nullable
   public static String deleteWhitespace(@Nullable String str) {
      if (isEmpty(str)) {
         return str;
      } else {
         int sz = str.length();
         char[] chs = new char[sz];
         int count = 0;

         for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
               chs[count++] = str.charAt(i);
            }
         }

         return count == sz ? str : new String(chs, 0, count);
      }
   }

   public static String replace(@Nullable String str, @Nullable String format, String value) {
      if (!isEmpty(str) && !isEmpty(format)) {
         String key = format.startsWith("${") && format.endsWith("}") ? format : "${" + format + "}";
         return str.replace(key, value);
      } else {
         return str;
      }
   }

   public static boolean equals(@Nullable CharSequence str1, @Nullable CharSequence str2) {
      if (null == str1) {
         return str2 == null;
      } else {
         return null == str2 ? false : str1.toString().contentEquals(str2);
      }
   }

   public static boolean contains(@Nullable CharSequence str, @Nullable CharSequence search) {
      if (str != null && search != null) {
         int searchLen = search.length();
         if (searchLen == 0) {
            return true;
         } else {
            String strVal = str.toString();
            String searchVal = search.toString();
            int max = strVal.length() - searchLen;

            for (int i = 0; i <= max; i++) {
               if (strVal.regionMatches(true, i, searchVal, 0, searchLen)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static String replaceLast(@NotNull String text, @NotNull String regex, @NotNull String replacement) {
      int lastIndex = text.lastIndexOf(regex);
      return lastIndex != -1 ? text.substring(0, lastIndex) + replacement + text.substring(lastIndex + regex.length()) : text;
   }

   public static boolean isWrap(@Nullable CharSequence str, char prefixChar, char suffixChar) {
      return null != str && str.length() >= 2 ? str.charAt(0) == prefixChar && str.charAt(str.length() - 1) == suffixChar : false;
   }

   public static boolean contains(@Nullable String str, @Nullable List<String> list) {
      return !isEmpty(str) && !CollUtil.isEmpty(list) ? list.stream().anyMatch(str::contains) : false;
   }
}
