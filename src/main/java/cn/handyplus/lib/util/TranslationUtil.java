package cn.handyplus.lib.util;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TranslationUtil {
   @NotNull
   public static String getEnchantmentTranslation(@NotNull String enchantment) {
      if (BaseConstants.JSON_CACHE_MAP.isEmpty()) {
         return enchantment;
      } else {
         String enchantmentKey = LegacyUtil.getEnchantmentKey(LegacyUtil.getEnchantmentByName(enchantment));
         return BaseConstants.JSON_CACHE_MAP.getOrDefault("enchantment.minecraft." + enchantmentKey, enchantment);
      }
   }

   @NotNull
   public static String getEnchantmentLevelTranslation(@NotNull String enchantmentLevel) {
      return BaseConstants.JSON_CACHE_MAP.isEmpty()
         ? enchantmentLevel
         : BaseConstants.JSON_CACHE_MAP.getOrDefault("enchantment.level." + enchantmentLevel, enchantmentLevel);
   }

   @NotNull
   public static String getEffectTranslation(@NotNull String effect) {
      return BaseConstants.JSON_CACHE_MAP.isEmpty() ? effect : BaseConstants.JSON_CACHE_MAP.getOrDefault("effect.minecraft." + effect.toLowerCase(), effect);
   }

   @NotNull
   public static String getEntityTranslation(@NotNull String entity) {
      return BaseConstants.JSON_CACHE_MAP.isEmpty() ? entity : BaseConstants.JSON_CACHE_MAP.getOrDefault("entity.minecraft." + entity.toLowerCase(), entity);
   }

   @NotNull
   public static String getColorTranslation(@NotNull String color) {
      return BaseConstants.JSON_CACHE_MAP.isEmpty() ? color : BaseConstants.JSON_CACHE_MAP.getOrDefault("color.minecraft." + color.toLowerCase(), color);
   }

   @Nullable
   public static String getMaterialTranslation(@NotNull String materialName) {
      String itemTranslation = getItemTranslation(materialName);
      return StrUtil.isNotEmpty(itemTranslation) ? itemTranslation : getBlockTranslation(materialName);
   }

   @Nullable
   public static String getItemTranslation(@NotNull String item) {
      return MapUtil.isEmpty(BaseConstants.JSON_CACHE_MAP) ? null : BaseConstants.JSON_CACHE_MAP.get("item.minecraft." + item.toLowerCase());
   }

   @Nullable
   public static String getBlockTranslation(@NotNull String block) {
      return MapUtil.isEmpty(BaseConstants.JSON_CACHE_MAP) ? null : BaseConstants.JSON_CACHE_MAP.get("block.minecraft." + block.toLowerCase());
   }
}
