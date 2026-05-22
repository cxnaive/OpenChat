package cn.handyplus.lib.util;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.TexturesParam;
import com.google.common.collect.MultimapBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public final class ItemMetaUtil {
   private ItemMetaUtil() {
   }

   public static void setUnbreakable(ItemMeta itemMeta) {
      if (itemMeta != null) {
         itemMeta.setUnbreakable(true);
      }
   }

   public static void setEnchant(ItemMeta itemMeta) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_20_5.getVersionId()) {
         itemMeta.setEnchantmentGlintOverride(true);
      } else {
         itemMeta.addEnchant(XSeriesUtil.getDurability(), 1, true);
         hideEnchant(itemMeta);
      }
   }

   public static void hideEnchant(ItemMeta itemMeta) {
      if (BaseConstants.VERSION_ID > VersionCheckEnum.V_1_7.getVersionId()) {
         itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      }
   }

   public static void hideAttributes(ItemMeta itemMeta) {
      if (BaseConstants.VERSION_ID > VersionCheckEnum.V_1_20_4.getVersionId()) {
         setHideTooltip(itemMeta);
      }

      if (BaseConstants.VERSION_ID > VersionCheckEnum.V_1_7.getVersionId()) {
         itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      }

      if (BaseConstants.VERSION_ID > VersionCheckEnum.V_1_19_3.getVersionId()) {
         itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
      }
   }

   public static void setCustomModelData(ItemMeta itemMeta, int id) {
      if (itemMeta != null && BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_14.getVersionId() && id != 0) {
         itemMeta.setCustomModelData(id);
      }
   }

   public static void setTooltipStyle(ItemMeta itemMeta, String tooltipStyle) {
      if (itemMeta != null && BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_21_2.getVersionId() && !StrUtil.isEmpty(tooltipStyle)) {
         itemMeta.setTooltipStyle(NamespacedKey.fromString(tooltipStyle));
      }
   }

   public static void setItemModel(ItemMeta itemMeta, String itemModel) {
      if (itemMeta != null && BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_21_2.getVersionId() && !StrUtil.isEmpty(itemModel)) {
         itemMeta.setItemModel(NamespacedKey.fromString(itemModel));
      }
   }

   public static void setPersistentData(ItemMeta itemMeta, String customData, String key) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_14.getVersionId() && itemMeta != null && !StrUtil.isEmpty(customData)) {
         PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
         String namespacedKeyStr = "handy_data";
         if (StrUtil.isNotEmpty(key)) {
            namespacedKeyStr = namespacedKeyStr + "_" + key;
         }

         dataContainer.set(new NamespacedKey(InitApi.PLUGIN, namespacedKeyStr), PersistentDataType.STRING, customData);
      }
   }

   public static Optional<String> getPersistentData(ItemMeta itemMeta, String key) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_14.getVersionId() && itemMeta != null) {
         String namespacedKeyStr = "handy_data";
         if (StrUtil.isNotEmpty(key)) {
            namespacedKeyStr = namespacedKeyStr + "_" + key;
         }

         String persistentData = (String)itemMeta.getPersistentDataContainer()
            .get(new NamespacedKey(InitApi.PLUGIN, namespacedKeyStr), PersistentDataType.STRING);
         return Optional.ofNullable(persistentData);
      } else {
         return Optional.empty();
      }
   }

   public static void removePersistentData(ItemMeta itemMeta, String key) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_14.getVersionId() && itemMeta != null) {
         String namespacedKeyStr = "handy_data";
         if (StrUtil.isNotEmpty(key)) {
            namespacedKeyStr = namespacedKeyStr + "_" + key;
         }

         itemMeta.getPersistentDataContainer().remove(new NamespacedKey(InitApi.PLUGIN, namespacedKeyStr));
      }
   }

   public static void setOwner(SkullMeta itemMeta, String playerName) {
      if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_12.getVersionId()) {
         itemMeta.setOwner(playerName);
      } else {
         itemMeta.setOwningPlayer(BaseUtil.getOfflinePlayer(playerName));
      }
   }

   public static void setSkull(SkullMeta itemMeta, String base64) {
      try {
         if (itemMeta != null && !StrUtil.isEmpty(base64)) {
            if (BaseConstants.VERSION_ID < VersionCheckEnum.V_1_18.getVersionId()) {
               setTexture(itemMeta, base64);
            } else {
               PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
               PlayerTextures textures = profile.getTextures();
               byte[] decode = Base64.getDecoder().decode(base64);
               String decodedString = new String(decode, StandardCharsets.UTF_8);
               TexturesParam texturesParam = JsonUtil.toBean(decodedString, TexturesParam.class);
               textures.setSkin(new URL(texturesParam.getUrl()));
               profile.setTextures(textures);
               itemMeta.setOwnerProfile(profile);
            }
         }
      } catch (Throwable var7) {
         throw new RuntimeException(var7);
      }
   }

   public static void setTrim(ArmorMeta armorMeta, String materialKey, String patternKey) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_19_4.getVersionId()) {
         setTrim(armorMeta, getTrimMaterial(materialKey), getTrimPattern(patternKey));
      }
   }

   public static void setTrim(ArmorMeta armorMeta, TrimMaterial trimMaterial, TrimPattern trimPattern) {
      if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_19_4.getVersionId()) {
         armorMeta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
      }
   }

   public static void setDisplayName(ItemMeta itemMeta, String displayName) {
      if (itemMeta != null && !StrUtil.isEmpty(displayName)) {
         if (BaseUtil.supportsComponentApi()) {
            itemMeta.displayName(ComponentUtil.parseColor(displayName));
         } else {
            itemMeta.setDisplayName(LegacyUtil.parseColor(displayName));
         }

         if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_20_5.getVersionId()) {
            if (BaseUtil.supportsComponentApi()) {
               itemMeta.itemName(itemMeta.displayName());
            } else {
               itemMeta.setItemName(itemMeta.getDisplayName());
            }
         }
      }
   }

   public static void setLore(ItemMeta itemMeta, List<String> loreList) {
      if (itemMeta != null && !CollUtil.isEmpty(loreList)) {
         if (BaseUtil.supportsComponentApi()) {
            itemMeta.lore(ComponentUtil.parseColor(loreList));
         } else {
            itemMeta.setLore(LegacyUtil.parseColor(loreList));
         }
      }
   }

   public static String stripColorDisplayName(ItemMeta itemMeta) {
      if (itemMeta == null) {
         return null;
      } else {
         return BaseUtil.supportsComponentApi() ? ComponentUtil.stripColor(itemMeta.displayName()) : BaseUtil.stripColor(itemMeta.getDisplayName());
      }
   }

   private static TrimMaterial getTrimMaterial(String materialKey) {
      TrimMaterial trimMaterial = (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(materialKey));
      if (trimMaterial == null) {
         throw new RuntimeException("trim material type error:" + materialKey);
      } else {
         return trimMaterial;
      }
   }

   private static TrimPattern getTrimPattern(String patternKey) {
      TrimPattern trimPattern = (TrimPattern)Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(patternKey));
      if (trimPattern == null) {
         throw new RuntimeException("trim pattern type error:" + patternKey);
      } else {
         return trimPattern;
      }
   }

   private static void setTexture(ItemMeta itemMeta, String url) {
      try {
         GameProfile profile = new GameProfile(UUID.randomUUID(), UUID.randomUUID() + "");
         profile.getProperties().put("textures", new Property("textures", url));
         Field profileField = itemMeta.getClass().getDeclaredField("profile");
         profileField.setAccessible(true);
         profileField.set(itemMeta, profile);
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   private static void setHideTooltip(ItemMeta itemMeta) {
      itemMeta.setAttributeModifiers(MultimapBuilder.hashKeys().hashSetValues().build());
      itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ADDITIONAL_TOOLTIP});
   }
}
