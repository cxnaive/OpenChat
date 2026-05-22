package cn.handyplus.lib.internal;

import cn.handyplus.lib.internal.base.XBase;
import cn.handyplus.lib.internal.base.XRegistry;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public enum XEntityType implements XBase<XEntityType, EntityType> {
   ACACIA_BOAT(),
   ACACIA_CHEST_BOAT(),
   ALLAY(),
   AREA_EFFECT_CLOUD(),
   ARMADILLO(),
   ARMOR_STAND(),
   ARROW(),
   AXOLOTL(),
   BAMBOO_CHEST_RAFT(),
   BAMBOO_RAFT(),
   BAT(),
   BEE(),
   BIRCH_BOAT(),
   BIRCH_CHEST_BOAT(),
   BLAZE(),
   BLOCK_DISPLAY(),
   BOGGED(),
   BREEZE(),
   BREEZE_WIND_CHARGE(),
   CAMEL(),
   CAT(),
   CAVE_SPIDER(),
   CHERRY_BOAT(),
   CHERRY_CHEST_BOAT(),
   CHEST_MINECART("MINECART_CHEST"),
   CHICKEN(),
   COD(),
   COMMAND_BLOCK_MINECART("MINECART_COMMAND"),
   COW(),
   CREAKING(),
   @Deprecated
   CREAKING_TRANSIENT(),
   CREEPER(),
   DARK_OAK_BOAT(),
   DARK_OAK_CHEST_BOAT(),
   DOLPHIN(),
   DONKEY(),
   DRAGON_FIREBALL(),
   DROWNED(),
   EGG(),
   ELDER_GUARDIAN(),
   ENDERMAN(),
   ENDERMITE(),
   ENDER_DRAGON(),
   ENDER_PEARL(),
   END_CRYSTAL("ENDER_CRYSTAL"),
   EVOKER(),
   EVOKER_FANGS(),
   EXPERIENCE_BOTTLE("THROWN_EXP_BOTTLE"),
   EXPERIENCE_ORB(),
   EYE_OF_ENDER("ENDER_SIGNAL"),
   FALLING_BLOCK(),
   FIREBALL(),
   FIREWORK_ROCKET("FIREWORK"),
   FISHING_BOBBER("FISHING_HOOK"),
   FOX(),
   FROG(),
   FURNACE_MINECART(),
   GHAST(),
   GIANT(),
   GLOW_ITEM_FRAME(),
   GLOW_SQUID(),
   GOAT(),
   GUARDIAN(),
   HAPPY_GHAST(),
   HOGLIN(),
   HOPPER_MINECART("MINECART_HOPPER"),
   HORSE(),
   HUSK(),
   ILLUSIONER(),
   INTERACTION(),
   IRON_GOLEM(),
   ITEM("DROPPED_ITEM"),
   ITEM_DISPLAY(),
   ITEM_FRAME(),
   JUNGLE_BOAT(),
   JUNGLE_CHEST_BOAT(),
   LEASH_KNOT("LEASH_HITCH"),
   LIGHTNING_BOLT("LIGHTNING"),
   LINGERING_POTION(),
   LLAMA(),
   LLAMA_SPIT(),
   MAGMA_CUBE(),
   MANGROVE_BOAT(),
   MANGROVE_CHEST_BOAT(),
   MARKER(),
   MINECART(),
   MOOSHROOM("MUSHROOM_COW"),
   MULE(),
   OAK_BOAT("BOAT"),
   OAK_CHEST_BOAT("CHEST_BOAT"),
   OCELOT(),
   OMINOUS_ITEM_SPAWNER(),
   PAINTING(),
   PALE_OAK_BOAT(),
   PALE_OAK_CHEST_BOAT(),
   PANDA(),
   PARROT(),
   PHANTOM(),
   PIG(),
   PIGLIN(),
   PIGLIN_BRUTE(),
   PILLAGER(),
   PLAYER(),
   POLAR_BEAR(),
   PUFFERFISH(),
   RABBIT(),
   RAVAGER(),
   SALMON(),
   SHEEP(),
   SHULKER(),
   SHULKER_BULLET(),
   SILVERFISH(),
   SKELETON(),
   SKELETON_HORSE(),
   SLIME(),
   SMALL_FIREBALL(),
   SNIFFER(),
   SNOWBALL(),
   SNOW_GOLEM("SNOWMAN"),
   SPAWNER_MINECART("MINECART_MOB_SPAWNER"),
   SPECTRAL_ARROW(),
   SPIDER(),
   SPLASH_POTION("POTION"),
   SPRUCE_BOAT(),
   SPRUCE_CHEST_BOAT(),
   SQUID(),
   STRAY(),
   STRIDER(),
   TADPOLE(),
   TEXT_DISPLAY(),
   TNT("PRIMED_TNT"),
   TNT_MINECART("MINECART_TNT"),
   TRADER_LLAMA(),
   TRIDENT(),
   TROPICAL_FISH(),
   TURTLE(),
   UNKNOWN(),
   VEX(),
   VILLAGER(),
   VINDICATOR(),
   WANDERING_TRADER(),
   WARDEN(),
   WIND_CHARGE(),
   WITCH(),
   WITHER(),
   WITHER_SKELETON(),
   WITHER_SKULL(),
   WOLF(),
   ZOGLIN(),
   ZOMBIE(),
   ZOMBIE_HORSE(),
   ZOMBIE_VILLAGER(),
   ZOMBIFIED_PIGLIN(),
   COPPER_GOLEM(),
   MANNEQUIN(),
   CAMEL_HUSK(),
   NAUTILUS(),
   PARCHED(),
   ZOMBIE_NAUTILUS();

   public static final XRegistry<XEntityType, EntityType> REGISTRY = XEntityType.Data.REGISTRY;
   private final EntityType entityType;

   private XEntityType(String... names) {
      this.entityType = XEntityType.Data.REGISTRY.stdEnum(this, names);
   }

   @NotNull
   public static @Unmodifiable Collection<XEntityType> getValues() {
      return REGISTRY.getValues();
   }

   @NotNull
   public static XEntityType of(@NotNull Entity entity) {
      Objects.requireNonNull(entity, "Cannot match entity type from null entity");
      return of(entity.getType());
   }

   @NotNull
   public static XEntityType of(@NotNull EntityType entityType) {
      return REGISTRY.getByBukkitForm(entityType);
   }

   public static Optional<XEntityType> of(@NotNull String entityType) {
      return REGISTRY.getByName(entityType);
   }

   @Override
   public String[] getNames() {
      return new String[]{this.name()};
   }

   public EntityType get() {
      return this.entityType;
   }

   static {
      REGISTRY.discardMetadata();
   }

   private static final class Data {
      public static final XRegistry<XEntityType, EntityType> REGISTRY = new XRegistry<>(EntityType.class, XEntityType.class, x$0 -> new XEntityType[x$0]);
   }
}
