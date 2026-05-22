package cn.handyplus.lib.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CollUtil {
   private CollUtil() {
   }

   @SafeVarargs
   @NotNull
   public static <T> List<T> of(T... elements) {
      ArrayList<T> list = new ArrayList<>(elements.length);
      list.addAll(Arrays.asList(elements));
      return list;
   }

   @NotNull
   public static <T> String listToStr(@Nullable List<T> list) {
      return listToStr(list, ",");
   }

   @NotNull
   public static <T> String listToStr(@Nullable List<T> list, @NotNull CharSequence delimiter) {
      return isEmpty(list) ? "" : list.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
   }

   public static boolean isEmpty(@Nullable Collection<?> collection) {
      return collection == null || collection.isEmpty();
   }

   public static boolean isNotEmpty(@Nullable Collection<?> collection) {
      return !isEmpty(collection);
   }

   public static boolean equals(@Nullable List<String> list, @Nullable List<String> list1) {
      if (list == list1) {
         return true;
      } else {
         return list == null ? false : list.equals(list1);
      }
   }

   @NotNull
   public static <T> List<List<T>> partition(@NotNull List<T> list, int toIndex) {
      List<List<T>> listGroup = new ArrayList<>();
      int listSize = list.size();
      int i = 0;

      while (i < list.size()) {
         if (i + toIndex > listSize) {
            toIndex = listSize - i;
         }

         List<T> newList = list.subList(i, i + toIndex);
         listGroup.add(newList);
         i += toIndex;
      }

      return listGroup;
   }

   @Nullable
   public static String getContainsStr(@Nullable List<String> list, @Nullable String str) {
      if (!isEmpty(list) && !StrUtil.isEmpty(str)) {
         for (String string : list) {
            if (string.contains(str)) {
               return string;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Nullable
   public static String getStr(@Nullable List<String> list, @Nullable String str) {
      if (!isEmpty(list) && !StrUtil.isEmpty(str)) {
         int i = list.indexOf(str);
         return i != -1 ? list.get(i) : null;
      } else {
         return null;
      }
   }

   @NotNull
   public static <T> List<List<T>> splitList(@Nullable List<T> list, int splitSize) {
      if (isEmpty(list)) {
         return Collections.emptyList();
      } else {
         int listSize = list.size();
         int maxSize = (listSize + splitSize - 1) / splitSize;
         List<List<T>> result = new ArrayList<>(maxSize);

         for (int i = 0; i < listSize; i += splitSize) {
            int end = Math.min(i + splitSize, listSize);
            result.add(new ArrayList<>(list.subList(i, end)));
         }

         return result;
      }
   }

   @Nullable
   public static <T> T randomElement(@Nullable List<T> list) {
      return isEmpty(list) ? null : list.get(new Random().nextInt(list.size()));
   }

   public static boolean contains(@NotNull List<String> list, @NotNull String target) {
      for (String str : list) {
         if (target.equalsIgnoreCase(str)) {
            return true;
         }
      }

      return false;
   }
}
