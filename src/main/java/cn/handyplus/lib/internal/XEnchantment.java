package cn.handyplus.lib.internal;

import cn.handyplus.lib.internal.base.XModule;
import cn.handyplus.lib.internal.base.XRegistry;
import com.google.common.base.Enums;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class XEnchantment extends XModule<XEnchantment, Enchantment> {
   private static final boolean ISFLAT;
   private static final boolean IS_SUPER_FLAT;
   private static final boolean USES_WRAPPER;
   public static final XRegistry<XEnchantment, Enchantment> REGISTRY;
   public static final XEnchantment AQUA_AFFINITY;
   public static final XEnchantment BANE_OF_ARTHROPODS;
   public static final XEnchantment BINDING_CURSE;
   public static final XEnchantment BLAST_PROTECTION;
   public static final XEnchantment BREACH;
   public static final XEnchantment CHANNELING;
   public static final XEnchantment DENSITY;
   public static final XEnchantment DEPTH_STRIDER;
   public static final XEnchantment EFFICIENCY;
   public static final XEnchantment FEATHER_FALLING;
   public static final XEnchantment FIRE_ASPECT;
   public static final XEnchantment FIRE_PROTECTION;
   public static final XEnchantment FLAME;
   public static final XEnchantment FORTUNE;
   public static final XEnchantment FROST_WALKER;
   public static final XEnchantment IMPALING;
   public static final XEnchantment INFINITY;
   public static final XEnchantment KNOCKBACK;
   public static final XEnchantment LOOTING;
   public static final XEnchantment LOYALTY;
   public static final XEnchantment LUCK_OF_THE_SEA;
   public static final XEnchantment LURE;
   public static final XEnchantment MENDING;
   public static final XEnchantment MULTISHOT;
   public static final XEnchantment PIERCING;
   public static final XEnchantment POWER;
   public static final XEnchantment PROJECTILE_PROTECTION;
   public static final XEnchantment PROTECTION;
   public static final XEnchantment PUNCH;
   public static final XEnchantment QUICK_CHARGE;
   public static final XEnchantment RESPIRATION;
   public static final XEnchantment RIPTIDE;
   public static final XEnchantment SHARPNESS;
   public static final XEnchantment SILK_TOUCH;
   public static final XEnchantment SMITE;
   public static final XEnchantment SOUL_SPEED;
   public static final XEnchantment SWIFT_SNEAK;
   public static final XEnchantment THORNS;
   public static final XEnchantment UNBREAKING;
   public static final XEnchantment VANISHING_CURSE;
   public static final XEnchantment WIND_BURST;
   public static final XEnchantment SWEEPING_EDGE;
   public static final XEnchantment LUNGE;
   @Deprecated
   public static final XEnchantment[] VALUES;
   @Deprecated
   public static final Set<EntityType> EFFECTIVE_SMITE_ENTITIES;
   @Deprecated
   public static final Set<EntityType> EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES;

   private XEnchantment(Enchantment enchantment, String[] names) {
      super(enchantment, names);
   }

   @NotNull
   public static XEnchantment of(@NotNull Enchantment enchantment) {
      return REGISTRY.getByBukkitForm(enchantment);
   }

   public static Optional<XEnchantment> of(@NotNull String enchantment) {
      return REGISTRY.getByName(enchantment);
   }

   @Deprecated
   @NotNull
   public static XEnchantment[] values() {
      return REGISTRY.values();
   }

   @NotNull
   private static XEnchantment std(@NotNull String... names) {
      XEnchantment std = REGISTRY.std(names);
      if (USES_WRAPPER && std.isSupported()) {
         Enchantment enchantment = std.get();
         if (enchantment instanceof EnchantmentWrapper) {
            Enchantment wrapped = ((EnchantmentWrapper)enchantment).getEnchantment();
            REGISTRY.bukkitMapping().put(wrapped, std);
         }
      }

      return std;
   }

   @Deprecated
   private static Enchantment getBukkitEnchant(String name) {
      if (IS_SUPER_FLAT) {
         return (Enchantment)Registry.ENCHANTMENT.get(NamespacedKey.minecraft(name.toLowerCase(Locale.ENGLISH)));
      } else {
         return ISFLAT ? Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase(Locale.ENGLISH))) : Enchantment.getByName(name);
      }
   }

   @Deprecated
   public static boolean isSmiteEffectiveAgainst(@Nullable EntityType type) {
      return type != null && EFFECTIVE_SMITE_ENTITIES.contains(type);
   }

   @Deprecated
   public static boolean isArthropodsEffectiveAgainst(@Nullable EntityType type) {
      return type != null && EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES.contains(type);
   }

   @Deprecated
   @NotNull
   public static Optional<XEnchantment> matchXEnchantment(@NotNull String enchantment) {
      if (enchantment != null && !enchantment.isEmpty()) {
         return of(enchantment);
      } else {
         throw new IllegalArgumentException("Enchantment name cannot be null or empty");
      }
   }

   @Deprecated
   @NotNull
   public static XEnchantment matchXEnchantment(@NotNull Enchantment enchantment) {
      Objects.requireNonNull(enchantment, "Cannot parse XEnchantment of a null enchantment");
      return of(enchantment);
   }

   @NotNull
   public ItemStack getBook(int level) {
      ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
      EnchantmentStorageMeta meta = (EnchantmentStorageMeta)book.getItemMeta();
      meta.addStoredEnchant(this.get(), level, true);
      book.setItemMeta(meta);
      return book;
   }

   @Deprecated
   @Nullable
   public Enchantment getEnchant() {
      return this.get();
   }

   static {
      boolean usesWrapper = false;

      boolean flat;
      try {
         Class<?> namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey");
         Class<?> enchantmentClass = Class.forName("org.bukkit.enchantments.Enchantment");
         enchantmentClass.getDeclaredMethod("getByKey", namespacedKeyClass);
         flat = true;
      } catch (NoSuchMethodException | ClassNotFoundException var12) {
         flat = false;
      }

      boolean superFlat;
      try {
         Class.forName("org.bukkit.Registry");
         superFlat = true;
      } catch (ClassNotFoundException var11) {
         superFlat = false;
      }

      for (Field field : Enchantment.class.getDeclaredFields()) {
         int mods = field.getModifiers();
         if (Modifier.isPublic(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods) && field.getType() == Enchantment.class) {
            try {
               Object enchant = field.get(null);
               if (enchant instanceof EnchantmentWrapper) {
                  usesWrapper = true;
               }
            } catch (IllegalAccessException var10) {
               throw new IllegalStateException("Cannot get enchantment field for " + field, var10);
            }
         }
      }

      ISFLAT = flat;
      IS_SUPER_FLAT = superFlat;
      USES_WRAPPER = usesWrapper;
      REGISTRY = new XRegistry<>(Enchantment.class, XEnchantment.class, () -> Registry.ENCHANTMENT, XEnchantment::new, x$0 -> new XEnchantment[x$0]);
      AQUA_AFFINITY = std("WATER_WORKER", "WATER_WORKER", "AQUA_AFFINITY", "WATER_MINE");
      BANE_OF_ARTHROPODS = std("BANE_OF_ARTHROPODS", "DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPOD", "ARTHROPOD");
      BINDING_CURSE = std("BINDING_CURSE", "BIND_CURSE", "BINDING", "BIND");
      BLAST_PROTECTION = std("PROTECTION_EXPLOSIONS", "BLAST_PROTECT", "EXPLOSIONS_PROTECTION", "EXPLOSION_PROTECTION", "BLAST_PROTECTION");
      BREACH = std("BREACH");
      CHANNELING = std("CHANNELING", "CHANNELLING", "CHANELLING", "CHANELING", "CHANNEL");
      DENSITY = std("DENSITY");
      DEPTH_STRIDER = std("DEPTH_STRIDER", "DEPTH", "STRIDER");
      EFFICIENCY = std("EFFICIENCY", "DIG_SPEED", "MINE_SPEED", "CUT_SPEED");
      FEATHER_FALLING = std("PROTECTION_FALL", "FEATHER_FALL", "FALL_PROTECTION", "FEATHER_FALLING");
      FIRE_ASPECT = std("FIRE_ASPECT", "FIRE", "MELEE_FIRE", "MELEE_FLAME");
      FIRE_PROTECTION = std("PROTECTION_FIRE", "FIRE_PROT", "FIRE_PROTECT", "FIRE_PROTECTION", "FLAME_PROTECTION", "FLAME_PROTECT");
      FLAME = std("FLAME", "ARROW_FIRE", "FLAME_ARROW", "FIRE_ARROW");
      FORTUNE = std("FORTUNE", "LOOT_BONUS_BLOCKS", "BLOCKS_LOOT_BONUS");
      FROST_WALKER = std("FROST_WALKER", "FROST", "WALKER");
      IMPALING = std("IMPALING", "IMPALE", "OCEAN_DAMAGE");
      INFINITY = std("INFINITY", "ARROW_INFINITE", "INFINITE_ARROWS", "INFINITE", "UNLIMITED_ARROWS");
      KNOCKBACK = std("KNOCKBACK");
      LOOTING = std("LOOTING", "LOOT_BONUS_MOBS", "MOB_LOOT", "MOBS_LOOT_BONUS");
      LOYALTY = std("LOYALTY", "LOYAL", "RETURN");
      LUCK_OF_THE_SEA = std("LUCK_OF_THE_SEA", "LUCK", "LUCK_OF_SEA", "LUCK_OF_SEAS", "ROD_LUCK");
      LURE = std("LURE", "ROD_LURE");
      MENDING = std("MENDING");
      MULTISHOT = std("MULTISHOT", "TRIPLE_SHOT");
      PIERCING = std("PIERCING");
      POWER = std("POWER", "ARROW_DAMAGE", "ARROW_POWER");
      PROJECTILE_PROTECTION = std("PROTECTION_PROJECTILE", "PROJECTILE_PROTECTION");
      PROTECTION = std("PROTECTION", "PROTECTION_ENVIRONMENTAL", "PROTECT");
      PUNCH = std("PUNCH", "ARROW_KNOCKBACK", "ARROW_PUNCH");
      QUICK_CHARGE = std("QUICK_CHARGE", "QUICKCHARGE", "QUICK_DRAW", "FAST_CHARGE", "FAST_DRAW");
      RESPIRATION = std("RESPIRATION", "OXYGEN", "BREATH", "BREATHING");
      RIPTIDE = std("RIPTIDE", "RIP", "TIDE", "LAUNCH");
      SHARPNESS = std("SHARPNESS", "DAMAGE_ALL", "ALL_DAMAGE", "ALL_DMG", "SHARP");
      SILK_TOUCH = std("SILK_TOUCH", "SOFT_TOUCH");
      SMITE = std("SMITE", "DAMAGE_UNDEAD", "UNDEAD_DAMAGE");
      SOUL_SPEED = std("SOUL_SPEED", "SPEED_SOUL", "SOUL_RUNNER");
      SWIFT_SNEAK = std("SWIFT_SNEAK", "SNEAK_SWIFT");
      THORNS = std("THORNS", "HIGHCRIT", "THORN", "HIGHERCRIT");
      UNBREAKING = std("UNBREAKING", "DURABILITY", "DURA");
      VANISHING_CURSE = std("VANISHING_CURSE", "VANISH_CURSE", "VANISHING", "VANISH");
      WIND_BURST = std("WIND_BURST");
      SWEEPING_EDGE = std("SWEEPING", "SWEEPING_EDGE", "SWEEP_EDGE");
      LUNGE = std("LUNGE");
      VALUES = values();
      EntityType bee = (EntityType)Enums.getIfPresent(EntityType.class, "BEE").orNull();
      EntityType phantom = (EntityType)Enums.getIfPresent(EntityType.class, "PHANTOM").orNull();
      EntityType drowned = (EntityType)Enums.getIfPresent(EntityType.class, "DROWNED").orNull();
      EntityType witherSkeleton = (EntityType)Enums.getIfPresent(EntityType.class, "WITHER_SKELETON").orNull();
      EntityType skeletonHorse = (EntityType)Enums.getIfPresent(EntityType.class, "SKELETON_HORSE").orNull();
      EntityType stray = (EntityType)Enums.getIfPresent(EntityType.class, "STRAY").orNull();
      EntityType husk = (EntityType)Enums.getIfPresent(EntityType.class, "HUSK").orNull();
      Set<EntityType> arthorposEffective = EnumSet.of(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SILVERFISH, EntityType.ENDERMITE);
      if (bee != null) {
         arthorposEffective.add(bee);
      }

      EFFECTIVE_BANE_OF_ARTHROPODS_ENTITIES = Collections.unmodifiableSet(arthorposEffective);
      Set<EntityType> smiteEffective = EnumSet.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITHER);
      if (phantom != null) {
         smiteEffective.add(phantom);
      }

      if (drowned != null) {
         smiteEffective.add(drowned);
      }

      if (witherSkeleton != null) {
         smiteEffective.add(witherSkeleton);
      }

      if (skeletonHorse != null) {
         smiteEffective.add(skeletonHorse);
      }

      if (stray != null) {
         smiteEffective.add(stray);
      }

      if (husk != null) {
         smiteEffective.add(husk);
      }

      EFFECTIVE_SMITE_ENTITIES = Collections.unmodifiableSet(smiteEffective);
      if (USES_WRAPPER) {
         for (Field fieldx : Enchantment.class.getDeclaredFields()) {
            int mods = fieldx.getModifiers();
            if (Modifier.isPublic(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods) && fieldx.getType() == Enchantment.class) {
               try {
                  Enchantment enchantment = (Enchantment)fieldx.get(null);
                  if (enchantment instanceof EnchantmentWrapper) {
                     EnchantmentWrapper wrapper = (EnchantmentWrapper)enchantment;
                     XEnchantment mainMapping = REGISTRY.bukkitMapping().get(wrapper.getEnchantment());
                     Objects.requireNonNull(mainMapping, () -> "No main mapping found for Enchantment." + fieldx.getName() + " (" + wrapper + ')');
                     REGISTRY.bukkitMapping().put(wrapper, mainMapping);
                  }
               } catch (IllegalAccessException var9) {
                  throw new IllegalStateException("Cannot get direct enchantment field for " + fieldx, var9);
               }
            }
         }
      }

      REGISTRY.discardMetadata();
   }
}
