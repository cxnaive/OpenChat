package cn.handyplus.lib.internal;

import cn.handyplus.lib.internal.base.XBase;
import cn.handyplus.lib.internal.base.XRegistry;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum XPotion implements XBase<XPotion, PotionEffectType> {
   ABSORPTION("ABSORB"),
   BAD_OMEN("OMEN_BAD", "PILLAGER"),
   BLINDNESS("BLIND"),
   CONDUIT_POWER("CONDUIT", "POWER_CONDUIT"),
   DARKNESS(),
   DOLPHINS_GRACE("DOLPHIN", "GRACE"),
   FIRE_RESISTANCE("FIRE_RESIST", "RESIST_FIRE", "FIRE_RESISTANCE"),
   GLOWING("GLOW", "SHINE", "SHINY"),
   HASTE("FAST_DIGGING", "SUPER_PICK", "DIGFAST", "DIG_SPEED", "QUICK_MINE", "SHARP"),
   HEALTH_BOOST("BOOST_HEALTH", "BOOST", "HP"),
   HERO_OF_THE_VILLAGE("HERO", "VILLAGE_HERO"),
   HUNGER("STARVE", "HUNGRY"),
   INFESTED(),
   INSTANT_DAMAGE("INJURE", "DAMAGE", "HARMING", "INFLICT", "HARM"),
   INSTANT_HEALTH("HEALTH", "INSTA_HEAL", "INSTANT_HEAL", "INSTA_HEALTH", "HEAL", "HEALING"),
   INVISIBILITY("INVISIBLE", "VANISH", "INVIS", "DISAPPEAR", "HIDE"),
   JUMP_BOOST("LEAP", "LEAPING", "JUMP"),
   LEVITATION("LEVITATE"),
   LUCK("LUCKY"),
   MINING_FATIGUE("SLOW_DIGGING", "FATIGUE", "DULL", "DIGGING", "SLOW_DIG", "DIG_SLOW"),
   NAUSEA("CONFUSION", "SICKNESS", "SICK"),
   NIGHT_VISION("VISION", "VISION_NIGHT"),
   OOZING(),
   POISON("VENOM"),
   RAID_OMEN(),
   REGENERATION("REGEN"),
   RESISTANCE("DAMAGE_RESISTANCE", "ARMOR", "DMG_RESIST", "DMG_RESISTANCE"),
   SATURATION("FOOD"),
   SLOWNESS("SLOW", "SLUGGISH"),
   SLOW_FALLING("SLOW_FALL", "FALL_SLOW"),
   SPEED("SPRINT", "RUNFAST", "SWIFT", "SWIFTNESS", "FAST"),
   STRENGTH("INCREASE_DAMAGE", "BULL", "STRONG", "ATTACK"),
   TRIAL_OMEN(),
   UNLUCK("UNLUCKY"),
   WATER_BREATHING("WATER_BREATH", "UNDERWATER_BREATHING", "UNDERWATER_BREATH", "AIR"),
   WEAKNESS("WEAK"),
   WEAVING(),
   WIND_CHARGED(),
   WITHER("DECAY"),
   BREATH_OF_THE_NAUTILUS();

   public static final XPotion[] VALUES = values();
   @Deprecated
   public static final Set<XPotion> DEBUFFS = Collections.unmodifiableSet(
      EnumSet.of(BAD_OMEN, BLINDNESS, NAUSEA, INSTANT_DAMAGE, HUNGER, LEVITATION, POISON, SLOWNESS, MINING_FATIGUE, UNLUCK, WEAKNESS, WITHER)
   );
   private static final XPotion[] POTIONEFFECTTYPE_MAPPING = new XPotion[VALUES.length + 1];
   public static final XRegistry<XPotion, PotionEffectType> REGISTRY;
   private final PotionEffectType potionEffectType;
   private final PotionType potionType;

   private XPotion(@NotNull String... aliases) {
      PotionEffectType tempType = PotionEffectType.getByName(this.name());

      for (String legacy : aliases) {
         if (tempType == null) {
            tempType = PotionEffectType.getByName(legacy);
         }
      }

      if (this.name().equals("TURTLE_MASTER")) {
         tempType = findSlowness();
      }

      this.potionEffectType = tempType;
      this.potionType = PotionType.getByEffect(this.potionEffectType);
      XPotion.Data.REGISTRY.stdEnum(this, aliases, this.potionEffectType);
      if (this.potionType != null) {
         String basePotionType = this.potionType.name();
         String strongPotionType = "STRONG_" + basePotionType;
         String longPotionType = "LONG_" + basePotionType;
         XPotion.Data.POTION_TYPE_MAPPING.put(this.potionType, this);

         try {
            XPotion.Data.POTION_TYPE_MAPPING.put(PotionType.valueOf(strongPotionType), this);
            XPotion.Data.REGISTRY.registerName(strongPotionType, this);
         } catch (IllegalArgumentException var10) {
         }

         try {
            XPotion.Data.POTION_TYPE_MAPPING.put(PotionType.valueOf(longPotionType), this);
            XPotion.Data.REGISTRY.registerName(longPotionType, this);
         } catch (IllegalArgumentException var9) {
         }
      }
   }

   private static PotionEffectType findSlowness() {
      return Stream.of("SLOWNESS", "SLOW", "SLUGGISH")
         .<PotionEffectType>map(PotionEffectType::getByName)
         .filter(Objects::nonNull)
         .findFirst()
         .orElseThrow(() -> new IllegalStateException("Cannot find slowness potion type"));
   }

   @Deprecated
   @NotNull
   public static Optional<XPotion> matchXPotion(@NotNull String potion) {
      return of(potion);
   }

   @NotNull
   public static XPotion of(@NotNull PotionType potion) {
      return XPotion.Data.POTION_TYPE_MAPPING.get(potion);
   }

   public static Optional<XPotion> of(@NotNull String potion) {
      if (potion != null && !potion.isEmpty()) {
         PotionEffectType idType = fromId(potion);
         if (idType != null) {
            Optional<XPotion> type = REGISTRY.getByName(idType.getName());
            if (!type.isPresent()) {
               throw new UnsupportedOperationException("Unsupported potion effect type ID: " + idType);
            } else {
               return type;
            }
         } else {
            return REGISTRY.getByName(potion);
         }
      } else {
         throw new IllegalArgumentException("Cannot match XPotion of a null or empty potion effect type");
      }
   }

   @Deprecated
   public static XPotion matchXPotion(@NotNull PotionType type) {
      return of(type);
   }

   @Deprecated
   @NotNull
   public static XPotion matchXPotion(@NotNull PotionEffectType type) {
      return of(type);
   }

   @NotNull
   public static XPotion of(@NotNull PotionEffectType type) {
      Objects.requireNonNull(type, "Cannot match XPotion of a null potion effect type");
      return REGISTRY.getByBukkitForm(type);
   }

   @Nullable
   private static PotionEffectType fromId(@NotNull String type) {
      try {
         int id = Integer.parseInt(type);
         return PotionEffectType.getById(id);
      } catch (NumberFormatException var2) {
         return null;
      }
   }

   private static List<String> split(@NotNull String str, char separatorChar) {
      List<String> list = new ArrayList<>(5);
      boolean match = false;
      boolean lastMatch = false;
      int len = str.length();
      int start = 0;

      for (int i = 0; i < len; i++) {
         if (str.charAt(i) == separatorChar) {
            if (match) {
               list.add(str.substring(start, i));
               match = false;
               lastMatch = true;
            }

            start = i + 1;
         } else {
            lastMatch = false;
            match = true;
         }
      }

      if (match || lastMatch) {
         list.add(str.substring(start, len));
      }

      return list;
   }

   @Nullable
   public static XPotion.Effect parseEffect(@Nullable String potion) {
      if (!Strings.isNullOrEmpty(potion) && !potion.equalsIgnoreCase("none")) {
         List<String> split = split(potion.replace(" ", ""), ',');
         if (split.isEmpty()) {
            split = split(potion, ' ');
         }

         double chance = 100.0;
         int chanceIndex = 0;
         if (split.size() > 2) {
            chanceIndex = split.get(2).indexOf(37);
            if (chanceIndex != -1) {
               try {
                  chance = Double.parseDouble(split.get(2).substring(chanceIndex + 1));
               } catch (NumberFormatException var9) {
               }
            }
         }

         Optional<XPotion> typeOpt = of(split.get(0));
         if (!typeOpt.isPresent()) {
            return null;
         } else {
            PotionEffectType type = typeOpt.get().potionEffectType;
            if (type == null) {
               return null;
            } else {
               int duration = 2400;
               int amplifier = 0;
               if (split.size() > 1) {
                  duration = toInt(split.get(1), 1) * 20;
                  if (split.size() > 2) {
                     amplifier = toInt(chanceIndex <= 0 ? split.get(2) : split.get(2).substring(0, chanceIndex), 1) - 1;
                  }
               }

               return new XPotion.Effect(new PotionEffect(type, duration, amplifier), chance);
            }
         }
      } else {
         return null;
      }
   }

   private static int toInt(String str, int defaultValue) {
      try {
         return Integer.parseInt(str);
      } catch (NumberFormatException var3) {
         return defaultValue;
      }
   }

   public static void addEffects(@NotNull LivingEntity entity, @Nullable List<String> effects) {
      Objects.requireNonNull(entity, "Cannot add potion effects to null entity");

      for (XPotion.Effect effect : parseEffects(effects)) {
         effect.apply(entity);
      }
   }

   public static List<XPotion.Effect> parseEffects(@Nullable List<String> effectsString) {
      if (effectsString != null && !effectsString.isEmpty()) {
         List<XPotion.Effect> effects = new ArrayList<>(effectsString.size());

         for (String effectStr : effectsString) {
            XPotion.Effect effect = parseEffect(effectStr);
            if (effect != null) {
               effects.add(effect);
            }
         }

         return effects;
      } else {
         return new ArrayList<>();
      }
   }

   @NotNull
   public static ThrownPotion throwPotion(@NotNull LivingEntity entity, @Nullable Color color, @Nullable PotionEffect... effects) {
      Objects.requireNonNull(entity, "Cannot throw potion from null entity");
      ItemStack potion = Material.getMaterial("SPLASH_POTION") == null
         ? new ItemStack(Material.POTION, 1, (short)16398)
         : new ItemStack(Material.SPLASH_POTION);
      PotionMeta meta = (PotionMeta)potion.getItemMeta();
      meta.setColor(color);
      if (effects != null) {
         for (PotionEffect effect : effects) {
            meta.addCustomEffect(effect, true);
         }
      }

      potion.setItemMeta(meta);
      ThrownPotion thrownPotion = (ThrownPotion)entity.launchProjectile(ThrownPotion.class);
      thrownPotion.setItem(potion);
      return thrownPotion;
   }

   @NotNull
   public static ItemStack buildItemWithEffects(@NotNull Material type, @Nullable Color color, @Nullable PotionEffect... effects) {
      Objects.requireNonNull(type, "Cannot build an effected item with null type");
      if (!canHaveEffects(type)) {
         throw new IllegalArgumentException("Cannot build item with " + type.name() + " potion type");
      } else {
         ItemStack item = new ItemStack(type);
         PotionMeta meta = (PotionMeta)item.getItemMeta();
         meta.setColor(color);
         meta.setDisplayName(
            type == Material.POTION
               ? "Potion"
               : (type == Material.SPLASH_POTION ? "Splash Potion" : (type == Material.TIPPED_ARROW ? "Tipped Arrow" : "Lingering Potion"))
         );
         if (effects != null) {
            for (PotionEffect effect : effects) {
               meta.addCustomEffect(effect, true);
            }
         }

         item.setItemMeta(meta);
         return item;
      }
   }

   public static boolean canHaveEffects(@Nullable Material material) {
      return material != null && (material.name().endsWith("POTION") || material.name().startsWith("TIPPED_ARROW"));
   }

   @Nullable
   public PotionEffectType getPotionEffectType() {
      return this.potionEffectType;
   }

   @Override
   public String[] getNames() {
      return new String[]{this.name()};
   }

   @Nullable
   public PotionEffectType get() {
      return this.potionEffectType;
   }

   @Nullable
   public PotionType getPotionType() {
      return this.potionType;
   }

   @Nullable
   public PotionEffect buildPotionEffect(int duration, int amplifier) {
      return this.potionEffectType == null ? null : new PotionEffect(this.potionEffectType, duration, amplifier - 1);
   }

   @Override
   public String toString() {
      return this.friendlyName();
   }

   static {
      for (XPotion pot : VALUES) {
         if (pot.potionEffectType != null) {
            POTIONEFFECTTYPE_MAPPING[pot.potionEffectType.getId()] = pot;
         }
      }

      REGISTRY = XPotion.Data.REGISTRY;
      REGISTRY.discardMetadata();
   }

   private static final class Data {
      private static final Map<PotionType, XPotion> POTION_TYPE_MAPPING = new EnumMap<>(PotionType.class);
      private static final XRegistry<XPotion, PotionEffectType> REGISTRY = new XRegistry<>(
         PotionEffectType.class, XPotion.class, () -> Registry.EFFECT, null, x$0 -> new XPotion[x$0]
      );
   }

   public static class Effect {
      private PotionEffect effect;
      private double chance;

      public Effect(PotionEffect effect, double chance) {
         this.effect = effect;
         this.chance = chance;
      }

      public XPotion getXPotion() {
         return XPotion.of(this.effect.getType());
      }

      public double getChance() {
         return this.chance;
      }

      public boolean hasChance() {
         return this.chance >= 100.0 || ThreadLocalRandom.current().nextDouble(0.0, 100.0) <= this.chance;
      }

      public void setChance(double chance) {
         this.chance = chance;
      }

      public void apply(LivingEntity entity) {
         if (this.hasChance()) {
            entity.addPotionEffect(this.effect);
         }
      }

      public PotionEffect getEffect() {
         return this.effect;
      }

      public void setEffect(PotionEffect effect) {
         this.effect = effect;
      }
   }
}
