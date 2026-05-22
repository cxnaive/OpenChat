package cn.handyplus.lib.internal;

import cn.handyplus.lib.internal.base.XModule;
import cn.handyplus.lib.internal.base.XRegistry;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class XBiome extends XModule<XBiome, Biome> {
   public static final XRegistry<XBiome, Biome> REGISTRY = new XRegistry<>(Biome.class, XBiome.class, () -> Registry.BIOME, XBiome::new, x$0 -> new XBiome[x$0]);
   public static final XBiome WINDSWEPT_HILLS = std("WINDSWEPT_HILLS", "MOUNTAINS", "EXTREME_HILLS");
   public static final XBiome SNOWY_PLAINS = std("SNOWY_PLAINS", "SNOWY_TUNDRA", "ICE_FLATS", "ICE_PLAINS");
   public static final XBiome SPARSE_JUNGLE = std("SPARSE_JUNGLE", "JUNGLE_EDGE", "JUNGLE_EDGE");
   public static final XBiome STONY_SHORE = std("STONY_SHORE", "STONE_SHORE", "STONE_BEACH");
   public static final XBiome CHERRY_GROVE = std("CHERRY_GROVE");
   public static final XBiome PALE_GARDEN = std("PALE_GARDEN");
   public static final XBiome OLD_GROWTH_PINE_TAIGA = std("OLD_GROWTH_PINE_TAIGA", "GIANT_TREE_TAIGA", "REDWOOD_TAIGA", "MEGA_TAIGA");
   public static final XBiome WINDSWEPT_FOREST = std("WINDSWEPT_FOREST", "WOODED_MOUNTAINS", "EXTREME_HILLS_WITH_TREES", "EXTREME_HILLS_PLUS");
   public static final XBiome WOODED_BADLANDS = std("WOODED_BADLANDS", "WOODED_BADLANDS_PLATEAU", "MESA_ROCK", "MESA_PLATEAU_FOREST");
   public static final XBiome WINDSWEPT_GRAVELLY_HILLS = std(
      "WINDSWEPT_GRAVELLY_HILLS", "GRAVELLY_MOUNTAINS", "MUTATED_EXTREME_HILLS", "EXTREME_HILLS_MOUNTAINS"
   );
   public static final XBiome OLD_GROWTH_BIRCH_FOREST = std("OLD_GROWTH_BIRCH_FOREST", "TALL_BIRCH_FOREST", "MUTATED_BIRCH_FOREST", "BIRCH_FOREST_MOUNTAINS");
   public static final XBiome OLD_GROWTH_SPRUCE_TAIGA = std("OLD_GROWTH_SPRUCE_TAIGA", "GIANT_SPRUCE_TAIGA", "MUTATED_REDWOOD_TAIGA", "MEGA_SPRUCE_TAIGA");
   public static final XBiome WINDSWEPT_SAVANNA = std("WINDSWEPT_SAVANNA", "SHATTERED_SAVANNA", "MUTATED_SAVANNA", "SAVANNA_MOUNTAINS");
   public static final XBiome MEADOW = std("MEADOW");
   public static final XBiome MANGROVE_SWAMP = std("MANGROVE_SWAMP");
   public static final XBiome DEEP_DARK = std("DEEP_DARK");
   public static final XBiome GROVE = std("GROVE");
   public static final XBiome SNOWY_SLOPES = std("SNOWY_SLOPES");
   public static final XBiome FROZEN_PEAKS = std("FROZEN_PEAKS");
   public static final XBiome JAGGED_PEAKS = std("JAGGED_PEAKS");
   public static final XBiome STONY_PEAKS = std("STONY_PEAKS");
   public static final XBiome BADLANDS = std("BADLANDS", "MESA");
   public static final XBiome BADLANDS_PLATEAU = std(WOODED_BADLANDS, "BADLANDS_PLATEAU", "MESA_CLEAR_ROCK", "MESA_PLATEAU");
   public static final XBiome BEACH = std("BEACH", "BEACHES");
   public static final XBiome BIRCH_FOREST = std(OLD_GROWTH_BIRCH_FOREST, "BIRCH_FOREST");
   public static final XBiome BIRCH_FOREST_HILLS = std(OLD_GROWTH_BIRCH_FOREST, "BIRCH_FOREST_HILLS");
   public static final XBiome COLD_OCEAN = std("COLD_OCEAN");
   public static final XBiome DARK_FOREST = std("DARK_FOREST", "ROOFED_FOREST");
   public static final XBiome DARK_FOREST_HILLS = std("DARK_FOREST_HILLS", "MUTATED_ROOFED_FOREST", "ROOFED_FOREST_MOUNTAINS");
   public static final XBiome DEEP_COLD_OCEAN = std("DEEP_COLD_OCEAN", "COLD_DEEP_OCEAN");
   public static final XBiome DEEP_FROZEN_OCEAN = std("DEEP_FROZEN_OCEAN", "FROZEN_DEEP_OCEAN");
   public static final XBiome DEEP_LUKEWARM_OCEAN = std("DEEP_LUKEWARM_OCEAN", "LUKEWARM_DEEP_OCEAN");
   public static final XBiome DEEP_OCEAN = std("DEEP_OCEAN");
   public static final XBiome DEEP_WARM_OCEAN = std("DEEP_WARM_OCEAN", "WARM_DEEP_OCEAN");
   public static final XBiome DESERT = std("DESERT");
   public static final XBiome DESERT_HILLS = std("DESERT_HILLS");
   public static final XBiome DESERT_LAKES = std("DESERT_LAKES", "MUTATED_DESERT", "DESERT_MOUNTAINS");
   public static final XBiome END_BARRENS = std(Environment.THE_END, "END_BARRENS", "SKY_ISLAND_BARREN");
   public static final XBiome END_HIGHLANDS = std(Environment.THE_END, "END_HIGHLANDS", "SKY_ISLAND_HIGH");
   public static final XBiome END_MIDLANDS = std(Environment.THE_END, "END_MIDLANDS", "SKY_ISLAND_MEDIUM");
   public static final XBiome ERODED_BADLANDS = std("ERODED_BADLANDS", "MUTATED_MESA", "MESA_BRYCE");
   public static final XBiome FLOWER_FOREST = std("FLOWER_FOREST", "MUTATED_FOREST");
   public static final XBiome FOREST = std("FOREST");
   public static final XBiome FROZEN_OCEAN = std("FROZEN_OCEAN");
   public static final XBiome FROZEN_RIVER = std("FROZEN_RIVER");
   public static final XBiome GIANT_SPRUCE_TAIGA = std(OLD_GROWTH_SPRUCE_TAIGA, "GIANT_SPRUCE_TAIGA", "MUTATED_REDWOOD_TAIGA", "MEGA_SPRUCE_TAIGA");
   public static final XBiome GIANT_SPRUCE_TAIGA_HILLS = std(
      OLD_GROWTH_SPRUCE_TAIGA, "GIANT_SPRUCE_TAIGA_HILLS", "MUTATED_REDWOOD_TAIGA_HILLS", "MEGA_SPRUCE_TAIGA_HILLS"
   );
   public static final XBiome GIANT_TREE_TAIGA = std(OLD_GROWTH_PINE_TAIGA, "GIANT_TREE_TAIGA", "REDWOOD_TAIGA", "MEGA_TAIGA");
   public static final XBiome GIANT_TREE_TAIGA_HILLS = std(OLD_GROWTH_PINE_TAIGA, "GIANT_TREE_TAIGA_HILLS", "REDWOOD_TAIGA_HILLS", "MEGA_TAIGA_HILLS");
   public static final XBiome ICE_SPIKES = std("ICE_SPIKES", "MUTATED_ICE_FLATS", "ICE_PLAINS_SPIKES");
   public static final XBiome JUNGLE = std("JUNGLE");
   public static final XBiome JUNGLE_HILLS = std("JUNGLE_HILLS");
   public static final XBiome LUKEWARM_OCEAN = std("LUKEWARM_OCEAN");
   public static final XBiome MODIFIED_BADLANDS_PLATEAU = std(WOODED_BADLANDS, "MODIFIED_BADLANDS_PLATEAU", "MUTATED_MESA_CLEAR_ROCK", "MESA_PLATEAU");
   public static final XBiome MODIFIED_GRAVELLY_MOUNTAINS = std(
      WINDSWEPT_GRAVELLY_HILLS, "MODIFIED_GRAVELLY_MOUNTAINS", "MUTATED_EXTREME_HILLS_WITH_TREES", "EXTREME_HILLS_MOUNTAINS"
   );
   public static final XBiome MODIFIED_JUNGLE = std("MODIFIED_JUNGLE", "MUTATED_JUNGLE", "JUNGLE_MOUNTAINS");
   public static final XBiome MODIFIED_JUNGLE_EDGE = std(SPARSE_JUNGLE, "MODIFIED_JUNGLE_EDGE", "MUTATED_JUNGLE_EDGE", "JUNGLE_EDGE_MOUNTAINS");
   public static final XBiome MODIFIED_WOODED_BADLANDS_PLATEAU = std(
      WOODED_BADLANDS, "MODIFIED_WOODED_BADLANDS_PLATEAU", "MUTATED_MESA_ROCK", "MESA_PLATEAU_FOREST_MOUNTAINS"
   );
   public static final XBiome MOUNTAIN_EDGE = std(SPARSE_JUNGLE, "MOUNTAIN_EDGE", "SMALLER_EXTREME_HILLS");
   public static final XBiome MUSHROOM_FIELDS = std("MUSHROOM_FIELDS", "MUSHROOM_ISLAND");
   public static final XBiome MUSHROOM_FIELD_SHORE = std(STONY_SHORE, "MUSHROOM_FIELD_SHORE", "MUSHROOM_ISLAND_SHORE", "MUSHROOM_SHORE");
   public static final XBiome SOUL_SAND_VALLEY = std(Environment.NETHER, "SOUL_SAND_VALLEY");
   public static final XBiome CRIMSON_FOREST = std(Environment.NETHER, "CRIMSON_FOREST");
   public static final XBiome WARPED_FOREST = std(Environment.NETHER, "WARPED_FOREST");
   public static final XBiome BASALT_DELTAS = std(Environment.NETHER, "BASALT_DELTAS");
   public static final XBiome NETHER_WASTES = std(Environment.NETHER, "NETHER_WASTES", "NETHER", "HELL");
   public static final XBiome OCEAN = std("OCEAN");
   public static final XBiome PLAINS = std("PLAINS");
   public static final XBiome RIVER = std("RIVER");
   public static final XBiome SAVANNA = std("SAVANNA");
   public static final XBiome SAVANNA_PLATEAU = std(WINDSWEPT_SAVANNA, "SAVANNA_ROCK", "SAVANNA_PLATEAU");
   public static final XBiome SHATTERED_SAVANNA_PLATEAU = std(
      WINDSWEPT_SAVANNA, "SHATTERED_SAVANNA_PLATEAU", "MUTATED_SAVANNA_ROCK", "SAVANNA_PLATEAU_MOUNTAINS"
   );
   public static final XBiome SMALL_END_ISLANDS = std(Environment.THE_END, "SMALL_END_ISLANDS", "SKY_ISLAND_LOW");
   public static final XBiome SNOWY_BEACH = std("SNOWY_BEACH", "COLD_BEACH");
   public static final XBiome SNOWY_MOUNTAINS = std(WINDSWEPT_HILLS, "SNOWY_MOUNTAINS", "ICE_MOUNTAINS");
   public static final XBiome SNOWY_TAIGA = std("SNOWY_TAIGA", "TAIGA_COLD", "COLD_TAIGA");
   public static final XBiome SNOWY_TAIGA_HILLS = std("SNOWY_TAIGA_HILLS", "TAIGA_COLD_HILLS", "COLD_TAIGA_HILLS");
   public static final XBiome SNOWY_TAIGA_MOUNTAINS = std(WINDSWEPT_FOREST, "SNOWY_TAIGA_MOUNTAINS", "MUTATED_TAIGA_COLD", "COLD_TAIGA_MOUNTAINS");
   public static final XBiome SUNFLOWER_PLAINS = std("SUNFLOWER_PLAINS", "MUTATED_PLAINS");
   public static final XBiome SWAMP = std("SWAMP", "SWAMPLAND");
   public static final XBiome SWAMP_HILLS = std("SWAMP_HILLS", "MUTATED_SWAMPLAND", "SWAMPLAND_MOUNTAINS");
   public static final XBiome TAIGA = std("TAIGA");
   public static final XBiome TAIGA_HILLS = std("TAIGA_HILLS");
   public static final XBiome TAIGA_MOUNTAINS = std(WINDSWEPT_FOREST, "TAIGA_MOUNTAINS", "MUTATED_TAIGA");
   public static final XBiome CUSTOM = std("CUSTOM");
   public static final XBiome TALL_BIRCH_FOREST = std(OLD_GROWTH_BIRCH_FOREST, "TALL_BIRCH_FOREST", "MUTATED_BIRCH_FOREST", "BIRCH_FOREST_MOUNTAINS");
   public static final XBiome TALL_BIRCH_HILLS = std(OLD_GROWTH_BIRCH_FOREST, "TALL_BIRCH_HILLS", "MUTATED_BIRCH_FOREST_HILLS", "MESA_PLATEAU_FOREST_MOUNTAINS");
   public static final XBiome THE_END = std(Environment.THE_END, "THE_END", "SKY");
   public static final XBiome THE_VOID = std("THE_VOID", "VOID");
   public static final XBiome WARM_OCEAN = std("WARM_OCEAN");
   public static final XBiome WOODED_BADLANDS_PLATEAU = std("WOODED_BADLANDS_PLATEAU", "MESA_ROCK", "MESA_PLATEAU_FOREST");
   public static final XBiome WOODED_HILLS = std("WOODED_HILLS", "FOREST_HILLS");
   public static final XBiome WOODED_MOUNTAINS = std("WOODED_MOUNTAINS", "EXTREME_HILLS_WITH_TREES", "EXTREME_HILLS_PLUS");
   public static final XBiome BAMBOO_JUNGLE = std("BAMBOO_JUNGLE");
   public static final XBiome BAMBOO_JUNGLE_HILLS = std("BAMBOO_JUNGLE_HILLS");
   public static final XBiome DRIPSTONE_CAVES = std("DRIPSTONE_CAVES");
   public static final XBiome LUSH_CAVES = std("LUSH_CAVES");
   private static final boolean World_getMaxHeight$SUPPORTED;
   private static final boolean World_getMinHeight$SUPPORTED;
   @Nullable
   private final Environment environment;

   public XBiome(Environment environment, Biome biome, String[] names) {
      super(biome, names);
      this.environment = environment;
   }

   private XBiome(Biome biome, String[] names) {
      this(null, biome, names);
   }

   public Optional<Environment> getEnvironment() {
      return Optional.ofNullable(this.environment);
   }

   @Deprecated
   @Nullable
   public Biome getBiome() {
      return this.get();
   }

   @NotNull
   public CompletableFuture<Void> setBiome(@NotNull Chunk chunk) {
      Biome biome = this.get();
      Objects.requireNonNull(biome, () -> "Unsupported biome: " + this.name());
      Objects.requireNonNull(chunk, "Cannot set biome of null chunk");
      if (!chunk.isLoaded() && !chunk.load(true)) {
         throw new IllegalStateException("Could not load chunk at " + chunk.getX() + ", " + chunk.getZ());
      } else {
         int heightMax = World_getMaxHeight$SUPPORTED ? chunk.getWorld().getMaxHeight() : 1;
         int heightMin = World_getMinHeight$SUPPORTED ? chunk.getWorld().getMinHeight() : 0;
         return CompletableFuture.runAsync(() -> {
            for (int x = 0; x < 16; x++) {
               for (int y = heightMin; y < heightMax; y += 4) {
                  for (int z = 0; z < 16; z++) {
                     Block block = chunk.getBlock(x, y, z);
                     if (block.getBiome() != biome) {
                        block.setBiome(biome);
                     }
                  }
               }
            }
         }).exceptionally(result -> {
            result.printStackTrace();
            return null;
         });
      }
   }

   @NotNull
   public CompletableFuture<Void> setBiome(@NotNull Location start, @NotNull Location end) {
      Biome biome = this.get();
      Objects.requireNonNull(start, "Start location cannot be null");
      Objects.requireNonNull(end, "End location cannot be null");
      Objects.requireNonNull(biome, () -> "Unsupported biome: " + this.name());
      World world = start.getWorld();
      if (!world.getUID().equals(end.getWorld().getUID())) {
         throw new IllegalArgumentException("Location worlds mismatch");
      } else {
         int heightMax = World_getMaxHeight$SUPPORTED ? world.getMaxHeight() : 1;
         int heightMin = World_getMinHeight$SUPPORTED ? world.getMinHeight() : 0;
         return CompletableFuture.runAsync(() -> {
            for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
               for (int y = heightMin; y < heightMax; y += 4) {
                  for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
                     Block block = new Location(world, x, y, z).getBlock();
                     if (block.getBiome() != biome) {
                        block.setBiome(biome);
                     }
                  }
               }
            }
         }).exceptionally(result -> {
            result.printStackTrace();
            return null;
         });
      }
   }

   @NotNull
   public static XBiome of(@NotNull Biome biome) {
      return REGISTRY.getByBukkitForm(biome);
   }

   public static Optional<XBiome> of(@NotNull String biome) {
      return REGISTRY.getByName(biome);
   }

   @Deprecated
   public static XBiome[] values() {
      return REGISTRY.values();
   }

   @NotNull
   public static @Unmodifiable Collection<XBiome> getValues() {
      return REGISTRY.getValues();
   }

   @NotNull
   private static XBiome std(@NotNull Environment environment, @NotNull String... names) {
      return REGISTRY.std(bukkit -> new XBiome(environment, bukkit, names), names);
   }

   @NotNull
   private static XBiome std(@Nullable XBiome newVersion, @NotNull String... names) {
      return REGISTRY.std(bukkit -> new XBiome(null, bukkit, names), newVersion, names);
   }

   @NotNull
   private static XBiome std(@NotNull String... names) {
      return REGISTRY.std(bukkit -> new XBiome(Environment.NORMAL, bukkit, names), names);
   }

   static {
      boolean maxHeight = false;
      boolean minHeight = false;

      try {
         World.class.getMethod("getMaxHeight");
         maxHeight = true;
      } catch (Exception var4) {
      }

      try {
         World.class.getMethod("getMinHeight");
         minHeight = true;
      } catch (Exception var3) {
      }

      World_getMaxHeight$SUPPORTED = maxHeight;
      World_getMinHeight$SUPPORTED = minHeight;
      REGISTRY.discardMetadata();
   }
}
