package cn.handyplus.lib.util;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.PatternUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.internal.GameRuleUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LegacyUtil {
   private static Class<?> ATTRIBUTE_CLASS;

   private LegacyUtil() {
   }

   public static String parseColor(@Nullable String str) {
      if (StrUtil.isEmpty(str)) {
         return "";
      } else {
         String replaceStr = str.replace("&", "§");
         return BaseConstants.VERSION_ID < VersionCheckEnum.V_1_16.getVersionId() ? replaceStr : translateColorCodes(replaceStr);
      }
   }

   public static List<String> parseColor(@Nullable List<String> strList) {
      List<String> loreList = new ArrayList<>();
      if (CollUtil.isEmpty(strList)) {
         return loreList;
      } else {
         strList.forEach(lore -> loreList.add(parseColor(lore)));
         return loreList;
      }
   }

   public static void setMaxHealth(@NotNull LivingEntity livingEntity, double maxHealth) {
      livingEntity.setMaxHealth(maxHealth);
   }

   public static double getMaxHealth(@NotNull LivingEntity livingEntity) {
      return livingEntity.getMaxHealth();
   }

   public static void setGameRuleValue(@NotNull World world, @NotNull String ruleName, @NotNull Object value) {
      GameRuleUtil.setGameRuleValue(world, ruleName, value);
   }

   public static Particle getFirework() {
      return BaseConstants.VERSION_ID > VersionCheckEnum.V_1_20_4.getVersionId() ? Particle.FIREWORK : Particle.valueOf("FIREWORKS_SPARK");
   }

   public static Particle getDripLava() {
      return BaseConstants.VERSION_ID > VersionCheckEnum.V_1_20_4.getVersionId() ? Particle.DRIPPING_LAVA : Particle.valueOf("DRIP_LAVA");
   }

   public static Particle getRedStone() {
      return BaseConstants.VERSION_ID > VersionCheckEnum.V_1_20_4.getVersionId() ? Particle.DUST : Particle.valueOf("REDSTONE");
   }

   public static Enchantment getEnchantmentByName(@NotNull String name) {
      return XSeriesUtil.getEnchantment(name).orElse(null);
   }

   public static String getEnchantmentName(@NotNull Enchantment enchantment) {
      return enchantment.getName();
   }

   public static String getEnchantmentKey(@NotNull Enchantment enchantment) {
      return BaseConstants.VERSION_ID > VersionCheckEnum.V_1_12.getVersionId() ? enchantment.getKey().getKey() : null;
   }

   public static String getEntityTypeName(@NotNull EntityType entityType) {
      return BaseConstants.VERSION_ID > VersionCheckEnum.V_1_13_2.getVersionId() ? entityType.getKey().getKey() : entityType.getName();
   }

   public static List<String> getEntityTypeList() {
      return BaseConstants.VERSION_ID > VersionCheckEnum.V_1_13_2.getVersionId()
         ? Arrays.stream(EntityType.values()).filter(e -> e != EntityType.UNKNOWN).map(e -> e.getKey().getKey()).collect(Collectors.toList())
         : Arrays.stream(EntityType.values()).<String>map(EntityType::getName).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public static List<String> getEntityTypeAliveList() {
      List<String> entityTypeList = new ArrayList<>();
      boolean versionRst = BaseConstants.VERSION_ID > VersionCheckEnum.V_1_13_2.getVersionId();

      for (EntityType type : EntityType.values()) {
         if (type.isAlive() && type.isSpawnable()) {
            if (versionRst) {
               entityTypeList.add(type.getKey().getKey());
            } else {
               entityTypeList.add(type.getName());
            }
         }
      }

      return entityTypeList;
   }

   public static List<String> getEnchantmentList() {
      return Arrays.stream(Enchantment.values()).<String>map(Enchantment::getName).collect(Collectors.toList());
   }

   public static EntityType getEntityTypeByName(@NotNull String name) {
      return XSeriesUtil.getEntityType(name).orElse(null);
   }

   public static PotionEffectType getPotionEffectTypeByName(@NotNull String name) {
      return XSeriesUtil.getPotion(name).orElse(null);
   }

   public static List<String> getPotionEffectTypeList() {
      return Arrays.stream(PotionEffectType.values()).<String>map(PotionEffectType::getName).collect(Collectors.toList());
   }

   public static AttributeModifier getAttributeModifier(@NotNull String name, double amount, @NotNull Operation operation, @NotNull EquipmentSlot slot) {
      UUID uuid = UUID.randomUUID();
      return BaseConstants.VERSION_ID < VersionCheckEnum.V_1_21.getVersionId()
         ? new AttributeModifier(uuid, name, amount, operation, slot)
         : new AttributeModifier(NamespacedKey.fromString(uuid.toString()), amount, operation, slot.getGroup());
   }

   public static void initClasses() {
      try {
         if (BaseConstants.VERSION_ID >= VersionCheckEnum.V_1_9.getVersionId()) {
            ATTRIBUTE_CLASS = Class.forName("org.bukkit.attribute.Attribute");
         } else {
            ATTRIBUTE_CLASS = null;
         }
      } catch (ClassNotFoundException var1) {
         throw new RuntimeException("无法找到类", var1);
      }
   }

   public static Attribute getAttribute(@NotNull String name) {
      if (ATTRIBUTE_CLASS == null) {
         return null;
      } else if (ATTRIBUTE_CLASS.isEnum()) {
         try {
            Object t = Enum.valueOf(ATTRIBUTE_CLASS.asSubclass(Enum.class), name);
            return (Attribute)t;
         } catch (IllegalArgumentException var2) {
            throw new RuntimeException("没有找到的属性: " + name, var2);
         }
      } else {
         try {
            Method getMethod = ATTRIBUTE_CLASS.getMethod("valueOf", String.class);
            return (Attribute)getMethod.invoke(null, name);
         } catch (Exception var3) {
            throw new RuntimeException("没有找到属性: " + name, var3);
         }
      }
   }

   private static String translateColorCodes(@NotNull String str) {
      Matcher matcher = PatternUtil.RPG_PATTERN.matcher(str);
      if (!matcher.find()) {
         return str;
      } else {
         StringBuffer sb = new StringBuffer();

         do {
            String hex = matcher.group();
            if (hex.length() == 5) {
               hex = hex.substring(0, 2) + doubleCharacters(hex.substring(2));
            }

            matcher.appendReplacement(sb, ChatColor.of(hex.substring(1)).toString());
         } while (matcher.find());

         matcher.appendTail(sb);
         return ChatColor.translateAlternateColorCodes('§', sb.toString());
      }
   }

   private static String doubleCharacters(@NotNull String str) {
      StringBuilder sb = new StringBuilder();

      for (char c : str.toCharArray()) {
         sb.append(c);
         sb.append(c);
      }

      return sb.toString();
   }
}
