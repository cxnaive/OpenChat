package cn.handyplus.lib.constants;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.LegacyUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Generated;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public enum AttributeCompatEnum {
   MAX_HEALTH("MAX_HEALTH", "GENERIC_MAX_HEALTH", "生物的最大血量"),
   FOLLOW_RANGE("FOLLOW_RANGE", "GENERIC_FOLLOW_RANGE", "当一个生物正在跟随其他生物/人的时候的跟随范围"),
   KNOCKBACK_RESISTANCE("KNOCKBACK_RESISTANCE", "GENERIC_KNOCKBACK_RESISTANCE", "当生物被攻击的时候的击退抗性"),
   MOVEMENT_SPEED("MOVEMENT_SPEED", "GENERIC_MOVEMENT_SPEED", "生物的移动速度"),
   FLYING_SPEED("FLYING_SPEED", "GENERIC_FLYING_SPEED", "实体的飞行速度"),
   ATTACK_DAMAGE("ATTACK_DAMAGE", "GENERIC_ATTACK_DAMAGE", "当生物攻击其他生物/实体时所造成的伤害"),
   ATTACK_KNOCKBACK("ATTACK_KNOCKBACK", "GENERIC_ATTACK_KNOCKBACK", "生物的攻击击退力度"),
   ATTACK_SPEED("ATTACK_SPEED", "GENERIC_ATTACK_SPEED", "生物的攻击速率"),
   ARMOR("ARMOR", "GENERIC_ARMOR", "护甲的防御值"),
   ARMOR_TOUGHNESS("ARMOR_TOUGHNESS", "GENERIC_ARMOR_TOUGHNESS", "护甲的韧性"),
   FALL_DAMAGE_MULTIPLIER("FALL_DAMAGE_MULTIPLIER", "GENERIC_FALL_DAMAGE_MULTIPLIER", "实体的坠落伤害倍增器"),
   LUCK("LUCK", "GENERIC_LUCK", "生物的可能的掉落物"),
   MAX_ABSORPTION("MAX_ABSORPTION", "GENERIC_MAX_ABSORPTION", "实体的最大吸收值（即护甲吸收）"),
   SAFE_FALL_DISTANCE("SAFE_FALL_DISTANCE", "GENERIC_SAFE_FALL_DISTANCE", "实体可以坠落而不受伤害的高度"),
   SCALE("SCALE", "GENERIC_SCALE", "实体的相对大小"),
   STEP_HEIGHT("STEP_HEIGHT", "GENERIC_STEP_HEIGHT", "实体可以跨越的高度"),
   GRAVITY("GRAVITY", "GENERIC_GRAVITY", "施加在实体上的重力"),
   JUMP_STRENGTH("JUMP_STRENGTH", "GENERIC_JUMP_STRENGTH", "实体跳跃的力量"),
   BURNING_TIME("BURNING_TIME", "GENERIC_BURNING_TIME", "实体在着火后保持燃烧的时间"),
   EXPLOSION_KNOCKBACK_RESISTANCE("EXPLOSION_KNOCKBACK_RESISTANCE", "GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE", "对爆炸造成的击退的抗性"),
   MOVEMENT_EFFICIENCY("MOVEMENT_EFFICIENCY", "GENERIC_MOVEMENT_EFFICIENCY", "在困难地形中的移动速度"),
   OXYGEN_BONUS("OXYGEN_BONUS", "GENERIC_OXYGEN_BONUS", "水下使用的氧气"),
   WATER_MOVEMENT_EFFICIENCY("WATER_MOVEMENT_EFFICIENCY", "GENERIC_WATER_MOVEMENT_EFFICIENCY", "在水中移动的速度"),
   TEMPT_RANGE("TEMPT_RANGE", "GENERIC_TEMPT_RANGE", "生物受诱导范围"),
   BLOCK_INTERACTION_RANGE("BLOCK_INTERACTION_RANGE", "PLAYER_BLOCK_INTERACTION_RANGE", "玩家可以触及的方块距离"),
   ENTITY_INTERACTION_RANGE("ENTITY_INTERACTION_RANGE", "PLAYER_ENTITY_INTERACTION_RANGE", "玩家可以触及的实体距离"),
   BLOCK_BREAK_SPEED("BLOCK_BREAK_SPEED", "PLAYER_BLOCK_BREAK_SPEED", "玩家破坏方块的速度"),
   MINING_EFFICIENCY("MINING_EFFICIENCY", "PLAYER_MINING_EFFICIENCY", "正确工具的挖矿速度"),
   SNEAKING_SPEED("SNEAKING_SPEED", "PLAYER_SNEAKING_SPEED", "潜行速度"),
   SUBMERGED_MINING_SPEED("SUBMERGED_MINING_SPEED", "PLAYER_SUBMERGED_MINING_SPEED", "水下挖掘速度"),
   SWEEPING_DAMAGE_RATIO("SWEEPING_DAMAGE_RATIO", "PLAYER_SWEEPING_DAMAGE_RATIO", "横扫伤害"),
   SPAWN_REINFORCEMENTS("SPAWN_REINFORCEMENTS", "ZOMBIE_SPAWN_REINFORCEMENTS", "僵尸增援的几率"),
   WAYPOINT_TRANSMIT_RANGE("WAYPOINT_TRANSMIT_RANGE", "", "实体传送路径点的最大传输范围"),
   WAYPOINT_RECEIVE_RANGE("WAYPOINT_RECEIVE_RANGE", "", "实体接收路径点更新的最大范围");

   private final String newName;
   private final String oldName;
   private final String desc;

   public Attribute resolve() {
      return LegacyUtil.getAttribute(BaseConstants.VERSION_ID < VersionCheckEnum.V_1_21_3.getVersionId() ? this.oldName : this.newName);
   }

   public static AttributeCompatEnum getByName(@NotNull String name) {
      name = name.toUpperCase();

      for (AttributeCompatEnum attributeCompatEnum : values()) {
         if (attributeCompatEnum.getOldName().equalsIgnoreCase(name)) {
            return attributeCompatEnum;
         }
      }

      return valueOf(name);
   }

   public static void setAttributeModifier(@NotNull Map<EquipmentSlot, Map<String, Double>> slotAttrList, @NotNull ItemStack itemStack) {
      modifyAttributes(slotAttrList, itemStack, true);
   }

   public static void addAttributeModifier(@NotNull Map<EquipmentSlot, Map<String, Double>> slotAttrList, @NotNull ItemStack itemStack) {
      modifyAttributes(slotAttrList, itemStack, false);
   }

   private static void modifyAttributes(@NotNull Map<EquipmentSlot, Map<String, Double>> slotAttrList, @NotNull ItemStack itemStack, boolean removeOld) {
      if (!MapUtil.isEmpty(slotAttrList)) {
         ItemMeta itemMeta = ItemStackUtil.getItemMeta(itemStack);

         for (Entry<EquipmentSlot, Map<String, Double>> slotEntry : slotAttrList.entrySet()) {
            EquipmentSlot equipmentSlot = slotEntry.getKey();
            Map<String, Double> attrMap = slotEntry.getValue();

            for (Entry<String, Double> attrEntry : attrMap.entrySet()) {
               String attr = attrEntry.getKey();
               Double value = attrEntry.getValue();
               Attribute attribute = getByName(attr).resolve();
               if (removeOld) {
                  itemMeta.removeAttributeModifier(attribute);
               }

               Collection<AttributeModifier> existingModifiers = itemMeta.getAttributeModifiers(attribute);
               if (CollUtil.isNotEmpty(existingModifiers)) {
                  List<AttributeModifier> addNumberModifiers = existingModifiers.stream()
                     .filter(mod -> mod.getOperation() == Operation.ADD_NUMBER)
                     .collect(Collectors.toList());
                  if (CollUtil.isNotEmpty(addNumberModifiers)) {
                     double existingValue = addNumberModifiers.stream().mapToDouble(AttributeModifier::getAmount).sum();
                     addNumberModifiers.forEach(mod -> itemMeta.removeAttributeModifier(attribute, mod));
                     value = value + existingValue;
                  }
               }

               value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
               AttributeModifier attributeModifier = LegacyUtil.getAttributeModifier(attr, value, Operation.ADD_NUMBER, equipmentSlot);
               itemMeta.addAttributeModifier(attribute, attributeModifier);
            }
         }

         itemStack.setItemMeta(itemMeta);
      }
   }

   @Generated
   public String getNewName() {
      return this.newName;
   }

   @Generated
   public String getOldName() {
      return this.oldName;
   }

   @Generated
   public String getDesc() {
      return this.desc;
   }

   @Generated
   private AttributeCompatEnum(final String newName, final String oldName, final String desc) {
      this.newName = newName;
      this.oldName = oldName;
      this.desc = desc;
   }
}
