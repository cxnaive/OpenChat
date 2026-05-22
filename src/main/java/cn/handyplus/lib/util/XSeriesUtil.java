package cn.handyplus.lib.util;

import cn.handyplus.lib.internal.XBiome;
import cn.handyplus.lib.internal.XEnchantment;
import cn.handyplus.lib.internal.XEntityType;
import cn.handyplus.lib.internal.XMaterial;
import cn.handyplus.lib.internal.XPotion;
import cn.handyplus.lib.internal.XSound;
import cn.handyplus.lib.internal.base.XModule;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class XSeriesUtil {
   public static Optional<PotionEffectType> getPotion(@NotNull String name) {
      return XPotion.of(name).map(XPotion::get);
   }

   public static Optional<Sound> getSound(@NotNull String sound) {
      return XSound.of(sound).map(XModule::get);
   }

   public static Optional<Biome> getBiome(@NotNull String biome) {
      return XBiome.of(biome).map(XModule::get);
   }

   public static Optional<EntityType> getEntityType(@NotNull String key) {
      return XEntityType.of(key).map(XEntityType::get);
   }

   public static Material getMaterial(@NotNull String materialStr, @NotNull Material defaultMaterial) {
      return XMaterial.matchXMaterial(materialStr).orElse(XMaterial.matchXMaterial(defaultMaterial)).get();
   }

   public static ItemStack getMaterialItem(@NotNull String materialStr, @NotNull Material defaultMaterial) {
      return XMaterial.matchXMaterial(materialStr).orElse(XMaterial.matchXMaterial(defaultMaterial)).parseItem();
   }

   public static Optional<Enchantment> getEnchantment(@NotNull String key) {
      return XEnchantment.of(key).map(XModule::get);
   }

   public static Enchantment getDurability() {
      return XEnchantment.UNBREAKING.get();
   }

   public static Enchantment getArrowDamage() {
      return XEnchantment.POWER.get();
   }
}
