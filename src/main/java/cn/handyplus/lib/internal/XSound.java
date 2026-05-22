package cn.handyplus.lib.internal;

import cn.handyplus.lib.internal.base.XModule;
import cn.handyplus.lib.internal.base.XRegistry;
import cn.handyplus.lib.internal.base.annotations.XMerge;
import com.google.common.base.Enums;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class XSound extends XModule<XSound, Sound> {
   public static final XRegistry<XSound, Sound> REGISTRY = new XRegistry<>(
      Sound.class, XSound.class, () -> Registry.SOUNDS, XSound::new, x$0 -> new XSound[x$0]
   );
   public static final XSound AMBIENT_UNDERWATER_LOOP = std("ambient.underwater.loop", "AMBIENT_UNDERWATER_EXIT");
   public static final XSound AMBIENT_UNDERWATER_LOOP_ADDITIONS = std("ambient.underwater.loop.additions", "AMBIENT_UNDERWATER_EXIT");
   public static final XSound AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE = std("ambient.underwater.loop.additions.rare", "AMBIENT_UNDERWATER_EXIT");
   public static final XSound AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE = std("ambient.underwater.loop.additions.ultra_rare", "AMBIENT_UNDERWATER_EXIT");
   public static final XSound BLOCK_ANVIL_BREAK = std("block.anvil.break", "ANVIL_BREAK");
   public static final XSound BLOCK_ANVIL_HIT = std("block.anvil.hit", "BLOCK_ANVIL_FALL");
   public static final XSound BLOCK_ANVIL_LAND = std("block.anvil.land", "ANVIL_LAND");
   public static final XSound BLOCK_ANVIL_PLACE = std("block.anvil.place", "BLOCK_ANVIL_FALL");
   public static final XSound BLOCK_ANVIL_STEP = std("block.anvil.step", "BLOCK_ANVIL_FALL");
   public static final XSound BLOCK_ANVIL_USE = std("block.anvil.use", "ANVIL_USE");
   public static final XSound BLOCK_BEACON_DEACTIVATE = std("block.beacon.deactivate", "BLOCK_BEACON_AMBIENT");
   public static final XSound BLOCK_BEACON_POWER_SELECT = std("block.beacon.power_select", "BLOCK_BEACON_AMBIENT");
   public static final XSound BLOCK_CHEST_CLOSE = std("block.chest.close", "CHEST_CLOSE", "ENTITY_CHEST_CLOSE");
   public static final XSound BLOCK_CHEST_OPEN = std("block.chest.open", "CHEST_OPEN", "ENTITY_CHEST_OPEN");
   public static final XSound BLOCK_FIRE_AMBIENT = std("block.fire.ambient", "FIRE");
   public static final XSound BLOCK_FIRE_EXTINGUISH = std("block.fire.extinguish", "FIZZ");
   public static final XSound BLOCK_GLASS_BREAK = std("block.glass.break", "GLASS");
   public static final XSound BLOCK_GRASS_BREAK = std("block.grass.break", "DIG_GRASS");
   public static final XSound BLOCK_GRASS_STEP = std("block.grass.step", "STEP_GRASS");
   public static final XSound BLOCK_GRAVEL_BREAK = std("block.gravel.break", "DIG_GRAVEL");
   public static final XSound BLOCK_GRAVEL_STEP = std("block.gravel.step", "STEP_GRAVEL");
   public static final XSound BLOCK_LADDER_STEP = std("block.ladder.step", "STEP_LADDER");
   public static final XSound BLOCK_LAVA_AMBIENT = std("block.lava.ambient", "LAVA");
   public static final XSound BLOCK_LAVA_POP = std("block.lava.pop", "LAVA_POP");
   public static final XSound BLOCK_LILY_PAD_PLACE = std("block.lily_pad.place", "BLOCK_WATERLILY_PLACE");
   public static final XSound BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF = std("block.metal_pressure_plate.click_off", "BLOCK_METAL_PRESSUREPLATE_CLICK_OFF");
   public static final XSound BLOCK_METAL_PRESSURE_PLATE_CLICK_ON = std("block.metal_pressure_plate.click_on", "BLOCK_METAL_PRESSUREPLATE_CLICK_ON");
   public static final XSound BLOCK_NOTE_BLOCK_BASEDRUM = std("block.note_block.basedrum", "NOTE_BASS_DRUM", "BLOCK_NOTE_BASEDRUM");
   public static final XSound BLOCK_NOTE_BLOCK_BASS = std("block.note_block.bass", "NOTE_BASS", "BLOCK_NOTE_BASS");
   public static final XSound BLOCK_NOTE_BLOCK_BELL = std("block.note_block.bell", "BLOCK_NOTE_BELL");
   public static final XSound BLOCK_NOTE_BLOCK_CHIME = std("block.note_block.chime", "BLOCK_NOTE_CHIME");
   public static final XSound BLOCK_NOTE_BLOCK_FLUTE = std("block.note_block.flute", "BLOCK_NOTE_FLUTE");
   public static final XSound BLOCK_NOTE_BLOCK_GUITAR = std("block.note_block.guitar", "NOTE_BASS_GUITAR", "BLOCK_NOTE_GUITAR");
   public static final XSound BLOCK_NOTE_BLOCK_HARP = std("block.note_block.harp", "NOTE_PIANO", "BLOCK_NOTE_HARP");
   public static final XSound BLOCK_NOTE_BLOCK_HAT = std("block.note_block.hat", "NOTE_STICKS", "BLOCK_NOTE_HAT");
   public static final XSound BLOCK_NOTE_BLOCK_PLING = std("block.note_block.pling", "NOTE_PLING", "BLOCK_NOTE_PLING");
   public static final XSound BLOCK_NOTE_BLOCK_SNARE = std("block.note_block.snare", "NOTE_SNARE_DRUM", "BLOCK_NOTE_SNARE");
   public static final XSound BLOCK_NOTE_BLOCK_XYLOPHONE = std("block.note_block.xylophone", "BLOCK_NOTE_XYLOPHONE");
   public static final XSound BLOCK_PISTON_CONTRACT = std("block.piston.contract", "PISTON_RETRACT");
   public static final XSound BLOCK_PISTON_EXTEND = std("block.piston.extend", "PISTON_EXTEND");
   public static final XSound BLOCK_PORTAL_AMBIENT = std("block.portal.ambient", "PORTAL");
   public static final XSound BLOCK_PORTAL_TRAVEL = std("block.portal.travel", "PORTAL_TRAVEL");
   public static final XSound BLOCK_PORTAL_TRIGGER = std("block.portal.trigger", "PORTAL_TRIGGER");
   public static final XSound BLOCK_SAND_BREAK = std("block.sand.break", "DIG_SAND");
   public static final XSound BLOCK_SAND_STEP = std("block.sand.step", "STEP_SAND");
   public static final XSound BLOCK_SLIME_BLOCK_BREAK = std("block.slime_block.break", "BLOCK_SLIME_BREAK");
   public static final XSound BLOCK_SLIME_BLOCK_FALL = std("block.slime_block.fall", "BLOCK_SLIME_FALL");
   public static final XSound BLOCK_SLIME_BLOCK_HIT = std("block.slime_block.hit", "BLOCK_SLIME_HIT");
   public static final XSound BLOCK_SLIME_BLOCK_PLACE = std("block.slime_block.place", "BLOCK_SLIME_PLACE");
   public static final XSound BLOCK_SLIME_BLOCK_STEP = std("block.slime_block.step", "BLOCK_SLIME_STEP");
   public static final XSound BLOCK_SNOW_BREAK = std("block.snow.break", "DIG_SNOW");
   public static final XSound BLOCK_SNOW_STEP = std("block.snow.step", "STEP_SNOW");
   public static final XSound BLOCK_STONE_BREAK = std("block.stone.break", "DIG_STONE");
   public static final XSound BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF = std("block.stone_pressure_plate.click_off", "BLOCK_STONE_PRESSUREPLATE_CLICK_OFF");
   public static final XSound BLOCK_STONE_PRESSURE_PLATE_CLICK_ON = std("block.stone_pressure_plate.click_on", "BLOCK_STONE_PRESSUREPLATE_CLICK_ON");
   public static final XSound BLOCK_STONE_STEP = std("block.stone.step", "STEP_STONE");
   public static final XSound BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES = std("block.sweet_berry_bush.pick_berries", "item.sweet_berries.pick_from_bush");
   public static final XSound BLOCK_WATER_AMBIENT = std("block.water.ambient", "WATER");
   public static final XSound BLOCK_WET_GRASS_PLACE = std("block.wet_grass.place", "BLOCK_WET_GRASS_HIT");
   public static final XSound BLOCK_WET_GRASS_STEP = std("block.wet_grass.step", "BLOCK_WET_GRASS_HIT");
   public static final XSound BLOCK_WOODEN_BUTTON_CLICK_OFF = std("block.wooden_button.click_off", "WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_OFF");
   public static final XSound BLOCK_WOODEN_BUTTON_CLICK_ON = std("block.wooden_button.click_on", "WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON");
   public static final XSound BLOCK_WOODEN_DOOR_CLOSE = std("block.wooden_door.close", "DOOR_CLOSE");
   public static final XSound BLOCK_WOODEN_DOOR_OPEN = std("block.wooden_door.open", "DOOR_OPEN");
   public static final XSound BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF = std("block.wooden_pressure_plate.click_off", "BLOCK_WOOD_PRESSUREPLATE_CLICK_OFF");
   public static final XSound BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON = std("block.wooden_pressure_plate.click_on", "BLOCK_WOOD_PRESSUREPLATE_CLICK_ON");
   public static final XSound BLOCK_WOOD_BREAK = std("block.wood.break", "DIG_WOOD");
   public static final XSound BLOCK_WOOD_STEP = std("block.wood.step", "STEP_WOOD");
   public static final XSound BLOCK_WOOL_BREAK = std("block.wool.break", "DIG_WOOL", "BLOCK_CLOTH_BREAK");
   public static final XSound BLOCK_WOOL_HIT = std("block.wool.hit", "BLOCK_CLOTH_HIT");
   public static final XSound BLOCK_WOOL_PLACE = std("block.wool.place", "BLOCK_WOOL_FALL", "BLOCK_CLOTH_PLACE");
   public static final XSound BLOCK_WOOL_STEP = std("block.wool.step", "STEP_WOOL", "BLOCK_CLOTH_STEP");
   public static final XSound ENTITY_ARMOR_STAND_BREAK = std("entity.armor_stand.break", "ENTITY_ARMORSTAND_BREAK");
   public static final XSound ENTITY_ARMOR_STAND_FALL = std("entity.armor_stand.fall", "ENTITY_ARMORSTAND_FALL");
   public static final XSound ENTITY_ARMOR_STAND_HIT = std("entity.armor_stand.hit", "ENTITY_ARMORSTAND_HIT");
   public static final XSound ENTITY_ARMOR_STAND_PLACE = std("entity.armor_stand.place", "ENTITY_ARMORSTAND_PLACE");
   public static final XSound ENTITY_ARROW_HIT = std("entity.arrow.hit", "ARROW_HIT");
   public static final XSound ENTITY_ARROW_HIT_PLAYER = std("entity.arrow.hit_player", "SUCCESSFUL_HIT");
   public static final XSound ENTITY_ARROW_SHOOT = std("entity.arrow.shoot", "SHOOT_ARROW");
   public static final XSound ENTITY_BAT_AMBIENT = std("entity.bat.ambient", "BAT_IDLE");
   public static final XSound ENTITY_BAT_DEATH = std("entity.bat.death", "BAT_DEATH");
   public static final XSound ENTITY_BAT_HURT = std("entity.bat.hurt", "BAT_HURT");
   public static final XSound ENTITY_BAT_LOOP = std("entity.bat.loop", "BAT_LOOP");
   public static final XSound ENTITY_BAT_TAKEOFF = std("entity.bat.takeoff", "BAT_TAKEOFF");
   public static final XSound ENTITY_BLAZE_AMBIENT = std("entity.blaze.ambient", "BLAZE_BREATH");
   public static final XSound ENTITY_BLAZE_DEATH = std("entity.blaze.death", "BLAZE_DEATH");
   public static final XSound ENTITY_BLAZE_HURT = std("entity.blaze.hurt", "BLAZE_HIT");
   public static final XSound ENTITY_CAT_AMBIENT = std("entity.cat.ambient", "CAT_MEOW");
   public static final XSound ENTITY_CAT_EAT = std("entity.cat.eat");
   public static final XSound ENTITY_CAT_HISS = std("entity.cat.hiss", "CAT_HISS");
   public static final XSound ENTITY_CAT_HURT = std("entity.cat.hurt", "CAT_HIT");
   public static final XSound ENTITY_CAT_PURR = std("entity.cat.purr", "CAT_PURR");
   public static final XSound ENTITY_CAT_PURREOW = std("entity.cat.purreow", "CAT_PURREOW");
   public static final XSound ENTITY_CHICKEN_AMBIENT = std("entity.chicken.ambient", "CHICKEN_IDLE");
   public static final XSound ENTITY_CHICKEN_EGG = std("entity.chicken.egg", "CHICKEN_EGG_POP");
   public static final XSound ENTITY_CHICKEN_HURT = std("entity.chicken.hurt", "CHICKEN_HURT");
   public static final XSound ENTITY_CHICKEN_STEP = std("entity.chicken.step", "CHICKEN_WALK");
   public static final XSound ENTITY_COW_AMBIENT = std("entity.cow.ambient", "COW_IDLE");
   public static final XSound ENTITY_COW_HURT = std("entity.cow.hurt", "COW_HURT");
   public static final XSound ENTITY_COW_STEP = std("entity.cow.step", "COW_WALK");
   public static final XSound ENTITY_CREEPER_DEATH = std("entity.creeper.death", "CREEPER_DEATH");
   public static final XSound ENTITY_CREEPER_PRIMED = std("entity.creeper.primed", "CREEPER_HISS");
   public static final XSound ENTITY_DONKEY_AMBIENT = std("entity.donkey.ambient", "DONKEY_IDLE");
   public static final XSound ENTITY_DONKEY_ANGRY = std("entity.donkey.angry", "DONKEY_ANGRY");
   public static final XSound ENTITY_DONKEY_DEATH = std("entity.donkey.death", "DONKEY_DEATH");
   public static final XSound ENTITY_DONKEY_HURT = std("entity.donkey.hurt", "DONKEY_HIT");
   public static final XSound ENTITY_DRAGON_FIREBALL_EXPLODE = std("entity.dragon_fireball.explode", "ENTITY_ENDERDRAGON_FIREBALL_EXPLODE");
   public static final XSound ENTITY_ENDERMAN_AMBIENT = std("entity.enderman.ambient", "ENDERMAN_IDLE", "ENTITY_ENDERMEN_AMBIENT");
   public static final XSound ENTITY_ENDERMAN_DEATH = std("entity.enderman.death", "ENDERMAN_DEATH", "ENTITY_ENDERMEN_DEATH");
   public static final XSound ENTITY_ENDERMAN_HURT = std("entity.enderman.hurt", "ENDERMAN_HIT", "ENTITY_ENDERMEN_HURT");
   public static final XSound ENTITY_ENDERMAN_SCREAM = std("entity.enderman.scream", "ENDERMAN_SCREAM", "ENTITY_ENDERMEN_SCREAM");
   public static final XSound ENTITY_ENDERMAN_STARE = std("entity.enderman.stare", "ENDERMAN_STARE", "ENTITY_ENDERMEN_STARE");
   public static final XSound ENTITY_ENDERMAN_TELEPORT = std("entity.enderman.teleport", "ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT");
   public static final XSound ENTITY_ENDER_DRAGON_AMBIENT = std("entity.ender_dragon.ambient", "ENDERDRAGON_WINGS", "ENTITY_ENDERDRAGON_AMBIENT");
   public static final XSound ENTITY_ENDER_DRAGON_DEATH = std("entity.ender_dragon.death", "ENDERDRAGON_DEATH", "ENTITY_ENDERDRAGON_DEATH");
   public static final XSound ENTITY_ENDER_DRAGON_FLAP = std("entity.ender_dragon.flap", "ENDERDRAGON_WINGS", "ENTITY_ENDERDRAGON_FLAP");
   public static final XSound ENTITY_ENDER_DRAGON_GROWL = std("entity.ender_dragon.growl", "ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL");
   public static final XSound ENTITY_ENDER_DRAGON_HURT = std("entity.ender_dragon.hurt", "ENDERDRAGON_HIT", "ENTITY_ENDERDRAGON_HURT");
   public static final XSound ENTITY_ENDER_DRAGON_SHOOT = std("entity.ender_dragon.shoot", "ENTITY_ENDERDRAGON_SHOOT");
   public static final XSound ENTITY_ENDER_EYE_LAUNCH = std("entity.ender_eye.launch", "ENTITY_ENDEREYE_LAUNCH");
   public static final XSound ENTITY_ENDER_PEARL_THROW = std("entity.ender_pearl.throw", "ENTITY_ENDERPEARL_THROW");
   public static final XSound ENTITY_EVOKER_AMBIENT = std("entity.evoker.ambient", "ENTITY_EVOCATION_ILLAGER_AMBIENT");
   public static final XSound ENTITY_EVOKER_CAST_SPELL = std("entity.evoker.cast_spell", "ENTITY_EVOCATION_ILLAGER_CAST_SPELL");
   public static final XSound ENTITY_EVOKER_DEATH = std("entity.evoker.death", "ENTITY_EVOCATION_ILLAGER_DEATH");
   public static final XSound ENTITY_EVOKER_FANGS_ATTACK = std("entity.evoker_fangs.attack", "ENTITY_EVOCATION_FANGS_ATTACK");
   public static final XSound ENTITY_EVOKER_HURT = std("entity.evoker.hurt", "ENTITY_EVOCATION_ILLAGER_HURT");
   public static final XSound ENTITY_EVOKER_PREPARE_ATTACK = std("entity.evoker.prepare_attack", "ENTITY_EVOCATION_ILLAGER_PREPARE_ATTACK");
   public static final XSound ENTITY_EVOKER_PREPARE_SUMMON = std("entity.evoker.prepare_summon", "ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON");
   public static final XSound ENTITY_EVOKER_PREPARE_WOLOLO = std("entity.evoker.prepare_wololo", "ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO");
   public static final XSound ENTITY_FIREWORK_ROCKET_BLAST = std("entity.firework_rocket.blast", "FIREWORK_BLAST", "ENTITY_FIREWORK_BLAST");
   public static final XSound ENTITY_FIREWORK_ROCKET_BLAST_FAR = std("entity.firework_rocket.blast_far", "FIREWORK_BLAST2", "ENTITY_FIREWORK_BLAST_FAR");
   public static final XSound ENTITY_FIREWORK_ROCKET_LARGE_BLAST = std(
      "entity.firework_rocket.large_blast", "FIREWORK_LARGE_BLAST", "ENTITY_FIREWORK_LARGE_BLAST"
   );
   public static final XSound ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR = std(
      "entity.firework_rocket.large_blast_far", "FIREWORK_LARGE_BLAST2", "ENTITY_FIREWORK_LARGE_BLAST_FAR"
   );
   public static final XSound ENTITY_FIREWORK_ROCKET_LAUNCH = std("entity.firework_rocket.launch", "FIREWORK_LAUNCH", "ENTITY_FIREWORK_LAUNCH");
   public static final XSound ENTITY_FIREWORK_ROCKET_TWINKLE = std("entity.firework_rocket.twinkle", "FIREWORK_TWINKLE", "ENTITY_FIREWORK_TWINKLE");
   public static final XSound ENTITY_FIREWORK_ROCKET_TWINKLE_FAR = std("entity.firework_rocket.twinkle_far", "FIREWORK_TWINKLE2", "ENTITY_FIREWORK_TWINKLE_FAR");
   public static final XSound ENTITY_FISHING_BOBBER_SPLASH = std("entity.fishing_bobber.splash", "SPLASH2", "ENTITY_BOBBER_SPLASH");
   public static final XSound ENTITY_FISHING_BOBBER_THROW = std("entity.fishing_bobber.throw", "ENTITY_BOBBER_THROW");
   public static final XSound ENTITY_GENERIC_BIG_FALL = std("entity.generic.big_fall", "FALL_BIG");
   public static final XSound ENTITY_GENERIC_DRINK = std("entity.generic.drink", "DRINK");
   public static final XSound ENTITY_GENERIC_EAT = std("entity.generic.eat", "EAT");
   public static final XSound ENTITY_GENERIC_EXPLODE = std("entity.generic.explode", "EXPLODE");
   public static final XSound ENTITY_GENERIC_SMALL_FALL = std("entity.generic.small_fall", "FALL_SMALL");
   public static final XSound ENTITY_GENERIC_SPLASH = std("entity.generic.splash", "SPLASH");
   public static final XSound ENTITY_GENERIC_SWIM = std("entity.generic.swim", "SWIM");
   public static final XSound ENTITY_GHAST_AMBIENT = std("entity.ghast.ambient", "GHAST_MOAN");
   public static final XSound ENTITY_GHAST_DEATH = std("entity.ghast.death", "GHAST_DEATH");
   public static final XSound ENTITY_GHAST_HURT = std("entity.ghast.hurt", "GHAST_SCREAM2");
   public static final XSound ENTITY_GHAST_SCREAM = std("entity.ghast.scream", "GHAST_SCREAM");
   public static final XSound ENTITY_GHAST_SHOOT = std("entity.ghast.shoot", "GHAST_FIREBALL");
   public static final XSound ENTITY_GHAST_WARN = std("entity.ghast.warn", "GHAST_CHARGE");
   public static final XSound ENTITY_HORSE_AMBIENT = std("entity.horse.ambient", "HORSE_IDLE");
   public static final XSound ENTITY_HORSE_ANGRY = std("entity.horse.angry", "HORSE_ANGRY");
   public static final XSound ENTITY_HORSE_ARMOR = std("entity.horse.armor", "HORSE_ARMOR");
   public static final XSound ENTITY_HORSE_BREATHE = std("entity.horse.breathe", "HORSE_BREATHE");
   public static final XSound ENTITY_HORSE_DEATH = std("entity.horse.death", "HORSE_DEATH");
   public static final XSound ENTITY_HORSE_EAT = std("entity.horse.eat");
   public static final XSound ENTITY_HORSE_GALLOP = std("entity.horse.gallop", "HORSE_GALLOP");
   public static final XSound ENTITY_HORSE_HURT = std("entity.horse.hurt", "HORSE_HIT");
   public static final XSound ENTITY_HORSE_JUMP = std("entity.horse.jump", "HORSE_JUMP");
   public static final XSound ENTITY_HORSE_LAND = std("entity.horse.land", "HORSE_LAND");
   public static final XSound ENTITY_HORSE_SADDLE = std("entity.horse.saddle", "HORSE_SADDLE");
   public static final XSound ENTITY_HORSE_STEP = std("entity.horse.step", "HORSE_SOFT");
   public static final XSound ENTITY_HORSE_STEP_WOOD = std("entity.horse.step_wood", "HORSE_WOOD");
   public static final XSound ENTITY_HOSTILE_BIG_FALL = std("entity.hostile.big_fall", "FALL_BIG");
   public static final XSound ENTITY_HOSTILE_SMALL_FALL = std("entity.hostile.small_fall", "FALL_SMALL");
   public static final XSound ENTITY_HOSTILE_SPLASH = std("entity.hostile.splash", "SPLASH");
   public static final XSound ENTITY_HOSTILE_SWIM = std("entity.hostile.swim", "SWIM");
   public static final XSound ENTITY_ILLUSIONER_AMBIENT = std("entity.illusioner.ambient", "ENTITY_ILLUSION_ILLAGER_AMBIENT");
   public static final XSound ENTITY_ILLUSIONER_CAST_SPELL = std("entity.illusioner.cast_spell", "ENTITY_ILLUSION_ILLAGER_CAST_SPELL");
   public static final XSound ENTITY_ILLUSIONER_DEATH = std("entity.illusioner.death", "ENTITY_ILLUSIONER_CAST_DEATH", "ENTITY_ILLUSION_ILLAGER_DEATH");
   public static final XSound ENTITY_ILLUSIONER_HURT = std("entity.illusioner.hurt", "ENTITY_ILLUSION_ILLAGER_HURT");
   public static final XSound ENTITY_ILLUSIONER_MIRROR_MOVE = std("entity.illusioner.mirror_move", "ENTITY_ILLUSION_ILLAGER_MIRROR_MOVE");
   public static final XSound ENTITY_ILLUSIONER_PREPARE_BLINDNESS = std("entity.illusioner.prepare_blindness", "ENTITY_ILLUSION_ILLAGER_PREPARE_BLINDNESS");
   public static final XSound ENTITY_ILLUSIONER_PREPARE_MIRROR = std("entity.illusioner.prepare_mirror", "ENTITY_ILLUSION_ILLAGER_PREPARE_MIRROR");
   public static final XSound ENTITY_IRON_GOLEM_ATTACK = std("entity.iron_golem.attack", "IRONGOLEM_THROW", "ENTITY_IRONGOLEM_ATTACK");
   public static final XSound ENTITY_IRON_GOLEM_DEATH = std("entity.iron_golem.death", "IRONGOLEM_DEATH", "ENTITY_IRONGOLEM_DEATH");
   public static final XSound ENTITY_IRON_GOLEM_HURT = std("entity.iron_golem.hurt", "IRONGOLEM_HIT", "ENTITY_IRONGOLEM_HURT");
   public static final XSound ENTITY_IRON_GOLEM_STEP = std("entity.iron_golem.step", "IRONGOLEM_WALK", "ENTITY_IRONGOLEM_STEP");
   public static final XSound ENTITY_ITEM_BREAK = std("entity.item.break", "ITEM_BREAK");
   public static final XSound ENTITY_ITEM_FRAME_ADD_ITEM = std("entity.item_frame.add_item", "ENTITY_ITEMFRAME_ADD_ITEM");
   public static final XSound ENTITY_ITEM_FRAME_BREAK = std("entity.item_frame.break", "ENTITY_ITEMFRAME_BREAK");
   public static final XSound ENTITY_ITEM_FRAME_PLACE = std("entity.item_frame.place", "ENTITY_ITEMFRAME_PLACE");
   public static final XSound ENTITY_ITEM_FRAME_REMOVE_ITEM = std("entity.item_frame.remove_item", "ENTITY_ITEMFRAME_REMOVE_ITEM");
   public static final XSound ENTITY_ITEM_FRAME_ROTATE_ITEM = std("entity.item_frame.rotate_item", "ENTITY_ITEMFRAME_ROTATE_ITEM");
   public static final XSound ENTITY_ITEM_PICKUP = std("entity.item.pickup", "ITEM_PICKUP");
   public static final XSound ENTITY_LEASH_KNOT_BREAK = std("entity.leash_knot.break", "ENTITY_LEASHKNOT_BREAK");
   public static final XSound ENTITY_LEASH_KNOT_PLACE = std("entity.leash_knot.place", "ENTITY_LEASHKNOT_PLACE");
   public static final XSound ENTITY_LIGHTNING_BOLT_IMPACT = std("entity.lightning_bolt.impact", "ENTITY_LIGHTNING_IMPACT", "AMBIENCE_THUNDER");
   public static final XSound ENTITY_LIGHTNING_BOLT_THUNDER = std("entity.lightning_bolt.thunder", "ENTITY_LIGHTNING_THUNDER", "AMBIENCE_THUNDER");
   public static final XSound ENTITY_LINGERING_POTION_THROW = std("entity.lingering_potion.throw", "ENTITY_LINGERINGPOTION_THROW");
   public static final XSound ENTITY_MAGMA_CUBE_DEATH = std("entity.magma_cube.death", "ENTITY_MAGMACUBE_DEATH");
   public static final XSound ENTITY_MAGMA_CUBE_DEATH_SMALL = std("entity.magma_cube.death_small", "ENTITY_SMALL_MAGMACUBE_DEATH");
   public static final XSound ENTITY_MAGMA_CUBE_HURT = std("entity.magma_cube.hurt", "ENTITY_MAGMACUBE_HURT");
   public static final XSound ENTITY_MAGMA_CUBE_HURT_SMALL = std("entity.magma_cube.hurt_small", "ENTITY_SMALL_MAGMACUBE_HURT");
   public static final XSound ENTITY_MAGMA_CUBE_JUMP = std("entity.magma_cube.jump", "MAGMACUBE_JUMP", "ENTITY_MAGMACUBE_JUMP");
   public static final XSound ENTITY_MAGMA_CUBE_SQUISH = std("entity.magma_cube.squish", "MAGMACUBE_WALK", "ENTITY_MAGMACUBE_SQUISH");
   public static final XSound ENTITY_MAGMA_CUBE_SQUISH_SMALL = std("entity.magma_cube.squish_small", "MAGMACUBE_WALK2", "ENTITY_SMALL_MAGMACUBE_SQUISH");
   public static final XSound ENTITY_MINECART_INSIDE = std("entity.minecart.inside", "MINECART_INSIDE");
   public static final XSound ENTITY_MINECART_RIDING = std("entity.minecart.riding", "MINECART_BASE");
   public static final XSound ENTITY_MULE_CHEST = std("entity.mule.chest", "ENTITY_MULE_AMBIENT");
   public static final XSound ENTITY_MULE_DEATH = std("entity.mule.death", "ENTITY_MULE_AMBIENT");
   public static final XSound ENTITY_MULE_HURT = std("entity.mule.hurt", "ENTITY_MULE_AMBIENT");
   public static final XSound ENTITY_PIG_AMBIENT = std("entity.pig.ambient", "PIG_IDLE");
   public static final XSound ENTITY_PIG_DEATH = std("entity.pig.death", "PIG_DEATH");
   public static final XSound ENTITY_PIG_SADDLE = std("entity.pig.saddle", "ENTITY_PIG_HURT");
   public static final XSound ENTITY_PIG_STEP = std("entity.pig.step", "PIG_WALK");
   public static final XSound ENTITY_PLAYER_ATTACK_STRONG = std("entity.player.attack.strong", "SUCCESSFUL_HIT");
   public static final XSound ENTITY_PLAYER_BIG_FALL = std("entity.player.big_fall", "FALL_BIG");
   public static final XSound ENTITY_PLAYER_BURP = std("entity.player.burp", "BURP");
   public static final XSound ENTITY_PLAYER_HURT = std("entity.player.hurt", "HURT_FLESH");
   public static final XSound ENTITY_PLAYER_LEVELUP = std("entity.player.levelup", "LEVEL_UP");
   public static final XSound ENTITY_PLAYER_SMALL_FALL = std("entity.player.small_fall", "FALL_SMALL");
   public static final XSound ENTITY_PLAYER_SPLASH = std("entity.player.splash", "SLASH");
   public static final XSound ENTITY_PLAYER_SPLASH_HIGH_SPEED = std("entity.player.splash.high_speed", "SPLASH");
   public static final XSound ENTITY_PLAYER_SWIM = std("entity.player.swim", "SWIM");
   public static final XSound ENTITY_POLAR_BEAR_AMBIENT_BABY = std("entity.polar_bear.ambient_baby", "ENTITY_POLAR_BEAR_BABY_AMBIENT");
   public static final XSound ENTITY_SALMON_HURT = std("entity.salmon.hurt", "ENTITY_SALMON_FLOP");
   public static final XSound ENTITY_SHEEP_AMBIENT = std("entity.sheep.ambient", "SHEEP_IDLE");
   public static final XSound ENTITY_SHEEP_SHEAR = std("entity.sheep.shear", "SHEEP_SHEAR");
   public static final XSound ENTITY_SHEEP_STEP = std("entity.sheep.step", "SHEEP_WALK");
   public static final XSound ENTITY_SILVERFISH_AMBIENT = std("entity.silverfish.ambient", "SILVERFISH_IDLE");
   public static final XSound ENTITY_SILVERFISH_DEATH = std("entity.silverfish.death", "SILVERFISH_KILL");
   public static final XSound ENTITY_SILVERFISH_HURT = std("entity.silverfish.hurt", "SILVERFISH_HIT");
   public static final XSound ENTITY_SILVERFISH_STEP = std("entity.silverfish.step", "SILVERFISH_WALK");
   public static final XSound ENTITY_SKELETON_AMBIENT = std("entity.skeleton.ambient", "SKELETON_IDLE");
   public static final XSound ENTITY_SKELETON_DEATH = std("entity.skeleton.death", "SKELETON_DEATH");
   public static final XSound ENTITY_SKELETON_HORSE_AMBIENT = std("entity.skeleton_horse.ambient", "HORSE_SKELETON_IDLE");
   public static final XSound ENTITY_SKELETON_HORSE_DEATH = std("entity.skeleton_horse.death", "HORSE_SKELETON_DEATH");
   public static final XSound ENTITY_SKELETON_HORSE_HURT = std("entity.skeleton_horse.hurt", "HORSE_SKELETON_HIT");
   public static final XSound ENTITY_SKELETON_HURT = std("entity.skeleton.hurt", "SKELETON_HURT");
   public static final XSound ENTITY_SKELETON_STEP = std("entity.skeleton.step", "SKELETON_WALK");
   public static final XSound ENTITY_SLIME_ATTACK = std("entity.slime.attack", "SLIME_ATTACK");
   public static final XSound ENTITY_SLIME_HURT_SMALL = std("entity.slime.hurt_small", "ENTITY_SMALL_SLIME_HURT");
   public static final XSound ENTITY_SLIME_JUMP = std("entity.slime.jump", "SLIME_WALK");
   public static final XSound ENTITY_SLIME_JUMP_SMALL = std("entity.slime.jump_small", "SLIME_WALK2", "ENTITY_SMALL_SLIME_JUMP");
   public static final XSound ENTITY_SLIME_SQUISH = std("entity.slime.squish", "SLIME_WALK2");
   public static final XSound ENTITY_SLIME_SQUISH_SMALL = std("entity.slime.squish_small", "ENTITY_SMALL_SLIME_SQUISH");
   public static final XSound ENTITY_SNOW_GOLEM_AMBIENT = std("entity.snow_golem.ambient", "ENTITY_SNOWMAN_AMBIENT");
   public static final XSound ENTITY_SNOW_GOLEM_DEATH = std("entity.snow_golem.death", "ENTITY_SNOWMAN_DEATH");
   public static final XSound ENTITY_SNOW_GOLEM_HURT = std("entity.snow_golem.hurt", "ENTITY_SNOWMAN_HURT");
   public static final XSound ENTITY_SNOW_GOLEM_SHEAR = std("entity.snow_golem.shear");
   public static final XSound ENTITY_SNOW_GOLEM_SHOOT = std("entity.snow_golem.shoot", "ENTITY_SNOWMAN_SHOOT");
   public static final XSound ENTITY_SPIDER_AMBIENT = std("entity.spider.ambient", "SPIDER_IDLE");
   public static final XSound ENTITY_SPIDER_DEATH = std("entity.spider.death", "SPIDER_DEATH");
   public static final XSound ENTITY_SPIDER_STEP = std("entity.spider.step", "SPIDER_WALK");
   public static final XSound ENTITY_TNT_PRIMED = std("entity.tnt.primed", "FUSE");
   public static final XSound ENTITY_TROPICAL_FISH_FLOP = std("entity.tropical_fish.flop", "ENTITY_TROPICAL_FISH_DEATH");
   public static final XSound ENTITY_VILLAGER_AMBIENT = std("entity.villager.ambient", "VILLAGER_IDLE");
   public static final XSound ENTITY_VILLAGER_DEATH = std("entity.villager.death", "VILLAGER_DEATH");
   public static final XSound ENTITY_VILLAGER_HURT = std("entity.villager.hurt", "VILLAGER_HIT");
   public static final XSound ENTITY_VILLAGER_NO = std("entity.villager.no", "VILLAGER_NO");
   public static final XSound ENTITY_VILLAGER_TRADE = std("entity.villager.trade", "VILLAGER_HAGGLE", "ENTITY_VILLAGER_TRADING");
   public static final XSound ENTITY_VILLAGER_YES = std("entity.villager.yes", "VILLAGER_YES");
   public static final XSound ENTITY_VINDICATOR_AMBIENT = std("entity.vindicator.ambient", "ENTITY_VINDICATION_ILLAGER_AMBIENT");
   public static final XSound ENTITY_VINDICATOR_DEATH = std("entity.vindicator.death", "ENTITY_VINDICATION_ILLAGER_DEATH");
   public static final XSound ENTITY_VINDICATOR_HURT = std("entity.vindicator.hurt", "ENTITY_VINDICATION_ILLAGER_HURT");
   public static final XSound ENTITY_WITHER_AMBIENT = std("entity.wither.ambient", "WITHER_IDLE");
   public static final XSound ENTITY_WITHER_DEATH = std("entity.wither.death", "WITHER_DEATH");
   public static final XSound ENTITY_WITHER_HURT = std("entity.wither.hurt", "WITHER_HURT");
   public static final XSound ENTITY_WITHER_SHOOT = std("entity.wither.shoot", "WITHER_SHOOT");
   public static final XSound ENTITY_WITHER_SPAWN = std("entity.wither.spawn", "WITHER_SPAWN");
   public static final XSound ENTITY_WOLF_AMBIENT = std("entity.wolf.ambient", "WOLF_BARK");
   public static final XSound ENTITY_WOLF_DEATH = std("entity.wolf.death", "WOLF_DEATH");
   public static final XSound ENTITY_WOLF_GROWL = std("entity.wolf.growl", "WOLF_GROWL");
   public static final XSound ENTITY_WOLF_HOWL = std("entity.wolf.howl", "WOLF_HOWL");
   public static final XSound ENTITY_WOLF_HURT = std("entity.wolf.hurt", "WOLF_HURT");
   public static final XSound ENTITY_WOLF_PANT = std("entity.wolf.pant", "WOLF_PANT");
   public static final XSound ENTITY_WOLF_SHAKE = std("entity.wolf.shake", "WOLF_SHAKE");
   public static final XSound ENTITY_WOLF_STEP = std("entity.wolf.step", "WOLF_WALK");
   public static final XSound ENTITY_WOLF_WHINE = std("entity.wolf.whine", "WOLF_WHINE");
   public static final XSound ENTITY_ZOMBIE_AMBIENT = std("entity.zombie.ambient", "ZOMBIE_IDLE");
   public static final XSound ENTITY_ZOMBIE_ATTACK_IRON_DOOR = std("entity.zombie.attack_iron_door", "ZOMBIE_METAL");
   public static final XSound ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR = std("entity.zombie.attack_wooden_door", "ZOMBIE_WOOD", "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD");
   public static final XSound ENTITY_ZOMBIE_BREAK_WOODEN_DOOR = std("entity.zombie.break_wooden_door", "ZOMBIE_WOODBREAK", "ENTITY_ZOMBIE_BREAK_DOOR_WOOD");
   public static final XSound ENTITY_ZOMBIE_DEATH = std("entity.zombie.death", "ZOMBIE_DEATH");
   public static final XSound ENTITY_ZOMBIE_HORSE_AMBIENT = std("entity.zombie_horse.ambient", "HORSE_ZOMBIE_IDLE");
   public static final XSound ENTITY_ZOMBIE_HORSE_DEATH = std("entity.zombie_horse.death", "HORSE_ZOMBIE_DEATH");
   public static final XSound ENTITY_ZOMBIE_HORSE_HURT = std("entity.zombie_horse.hurt", "HORSE_ZOMBIE_HIT");
   public static final XSound ENTITY_ZOMBIE_HURT = std("entity.zombie.hurt", "ZOMBIE_HURT");
   public static final XSound ENTITY_ZOMBIE_INFECT = std("entity.zombie.infect", "ZOMBIE_INFECT");
   public static final XSound ENTITY_ZOMBIE_STEP = std("entity.zombie.step", "ZOMBIE_WALK");
   public static final XSound ENTITY_ZOMBIE_VILLAGER_CONVERTED = std("entity.zombie_villager.converted", "ZOMBIE_UNFECT");
   public static final XSound ENTITY_ZOMBIE_VILLAGER_CURE = std("entity.zombie_villager.cure", "ZOMBIE_REMEDY");
   public static final XSound ENTITY_ZOMBIFIED_PIGLIN_AMBIENT = std(
      "entity.zombified_piglin.ambient", "ZOMBIE_PIG_IDLE", "ENTITY_ZOMBIE_PIG_AMBIENT", "ENTITY_ZOMBIE_PIGMAN_AMBIENT"
   );
   public static final XSound ENTITY_ZOMBIFIED_PIGLIN_ANGRY = std(
      "entity.zombified_piglin.angry", "ZOMBIE_PIG_ANGRY", "ENTITY_ZOMBIE_PIG_ANGRY", "ENTITY_ZOMBIE_PIGMAN_ANGRY"
   );
   public static final XSound ENTITY_ZOMBIFIED_PIGLIN_DEATH = std(
      "entity.zombified_piglin.death", "ZOMBIE_PIG_DEATH", "ENTITY_ZOMBIE_PIG_DEATH", "ENTITY_ZOMBIE_PIGMAN_DEATH"
   );
   public static final XSound ENTITY_ZOMBIFIED_PIGLIN_HURT = std(
      "entity.zombified_piglin.hurt", "ZOMBIE_PIG_HURT", "ENTITY_ZOMBIE_PIG_HURT", "ENTITY_ZOMBIE_PIGMAN_HURT"
   );
   public static final XSound ITEM_FLINTANDSTEEL_USE = std("item.flintandsteel.use", "FIRE_IGNITE");
   public static final XSound ITEM_TRIDENT_RIPTIDE_2 = std("item.trident.riptide_2", "ITEM_TRIDENT_RIPTIDE_1");
   public static final XSound ITEM_TRIDENT_RIPTIDE_3 = std("item.trident.riptide_3", "ITEM_TRIDENT_RIPTIDE_1");
   public static final XSound MUSIC_DISC_11 = std("music_disc.11", "RECORD_11");
   public static final XSound MUSIC_DISC_13 = std("music_disc.13", "RECORD_13");
   public static final XSound MUSIC_DISC_BLOCKS = std("music_disc.blocks", "RECORD_BLOCKS");
   public static final XSound MUSIC_DISC_CAT = std("music_disc.cat", "RECORD_CAT");
   public static final XSound MUSIC_DISC_CHIRP = std("music_disc.chirp", "RECORD_CHIRP");
   public static final XSound MUSIC_DISC_FAR = std("music_disc.far", "RECORD_FAR");
   public static final XSound MUSIC_DISC_MALL = std("music_disc.mall", "RECORD_MALL");
   public static final XSound MUSIC_DISC_MELLOHI = std("music_disc.mellohi", "RECORD_MELLOHI");
   public static final XSound MUSIC_DISC_STAL = std("music_disc.stal", "RECORD_STAL");
   public static final XSound MUSIC_DISC_STRAD = std("music_disc.strad", "RECORD_STRAD");
   public static final XSound MUSIC_DISC_WAIT = std("music_disc.wait", "RECORD_WAIT");
   public static final XSound MUSIC_DISC_WARD = std("music_disc.ward", "RECORD_WARD");
   public static final XSound MUSIC_NETHER_BASALT_DELTAS = std("music.nether.basalt_deltas", "MUSIC_NETHER");
   public static final XSound UI_BUTTON_CLICK = std("ui.button.click", "CLICK");
   public static final XSound WEATHER_RAIN = std("weather.rain", "AMBIENCE_RAIN");
   public static final XSound AMBIENT_CAVE = std("ambient.cave", "AMBIENCE_CAVE");
   @XMerge(since = "1.9", version = "1.12?", name = "ENTITY_EXPERIENCE_ORB_TOUCH")
   public static final XSound ENTITY_EXPERIENCE_ORB_PICKUP = std("entity.experience_orb.pickup", "ORB_PICKUP");
   public static final XSound AMBIENT_BASALT_DELTAS_ADDITIONS = std("ambient.basalt_deltas.additions");
   public static final XSound AMBIENT_BASALT_DELTAS_LOOP = std("ambient.basalt_deltas.loop");
   public static final XSound AMBIENT_BASALT_DELTAS_MOOD = std("ambient.basalt_deltas.mood");
   public static final XSound AMBIENT_CRIMSON_FOREST_ADDITIONS = std("ambient.crimson_forest.additions");
   public static final XSound AMBIENT_CRIMSON_FOREST_LOOP = std("ambient.crimson_forest.loop");
   public static final XSound AMBIENT_CRIMSON_FOREST_MOOD = std("ambient.crimson_forest.mood");
   public static final XSound AMBIENT_NETHER_WASTES_ADDITIONS = std("ambient.nether_wastes.additions");
   public static final XSound AMBIENT_NETHER_WASTES_LOOP = std("ambient.nether_wastes.loop");
   public static final XSound AMBIENT_NETHER_WASTES_MOOD = std("ambient.nether_wastes.mood");
   public static final XSound AMBIENT_SOUL_SAND_VALLEY_ADDITIONS = std("ambient.soul_sand_valley.additions");
   public static final XSound AMBIENT_SOUL_SAND_VALLEY_LOOP = std("ambient.soul_sand_valley.loop");
   public static final XSound AMBIENT_SOUL_SAND_VALLEY_MOOD = std("ambient.soul_sand_valley.mood");
   public static final XSound AMBIENT_UNDERWATER_ENTER = std("ambient.underwater.enter");
   public static final XSound AMBIENT_UNDERWATER_EXIT = std("ambient.underwater.exit");
   public static final XSound AMBIENT_WARPED_FOREST_ADDITIONS = std("ambient.warped_forest.additions");
   public static final XSound AMBIENT_WARPED_FOREST_LOOP = std("ambient.warped_forest.loop");
   public static final XSound AMBIENT_WARPED_FOREST_MOOD = std("ambient.warped_forest.mood");
   public static final XSound BLOCK_AMETHYST_BLOCK_BREAK = std("block.amethyst_block.break");
   public static final XSound BLOCK_AMETHYST_BLOCK_CHIME = std("block.amethyst_block.chime");
   public static final XSound BLOCK_AMETHYST_BLOCK_FALL = std("block.amethyst_block.fall");
   public static final XSound BLOCK_AMETHYST_BLOCK_HIT = std("block.amethyst_block.hit");
   public static final XSound BLOCK_AMETHYST_BLOCK_PLACE = std("block.amethyst_block.place");
   public static final XSound BLOCK_AMETHYST_BLOCK_RESONATE = std("block.amethyst_block.resonate");
   public static final XSound BLOCK_AMETHYST_BLOCK_STEP = std("block.amethyst_block.step");
   public static final XSound BLOCK_AMETHYST_CLUSTER_BREAK = std("block.amethyst_cluster.break");
   public static final XSound BLOCK_AMETHYST_CLUSTER_FALL = std("block.amethyst_cluster.fall");
   public static final XSound BLOCK_AMETHYST_CLUSTER_HIT = std("block.amethyst_cluster.hit");
   public static final XSound BLOCK_AMETHYST_CLUSTER_PLACE = std("block.amethyst_cluster.place");
   public static final XSound BLOCK_AMETHYST_CLUSTER_STEP = std("block.amethyst_cluster.step");
   public static final XSound BLOCK_ANCIENT_DEBRIS_BREAK = std("block.ancient_debris.break");
   public static final XSound BLOCK_ANCIENT_DEBRIS_FALL = std("block.ancient_debris.fall");
   public static final XSound BLOCK_ANCIENT_DEBRIS_HIT = std("block.ancient_debris.hit");
   public static final XSound BLOCK_ANCIENT_DEBRIS_PLACE = std("block.ancient_debris.place");
   public static final XSound BLOCK_ANCIENT_DEBRIS_STEP = std("block.ancient_debris.step");
   public static final XSound BLOCK_ANVIL_DESTROY = std("block.anvil.destroy");
   public static final XSound BLOCK_ANVIL_FALL = std("block.anvil.fall");
   public static final XSound BLOCK_AZALEA_BREAK = std("block.azalea.break");
   public static final XSound BLOCK_AZALEA_FALL = std("block.azalea.fall");
   public static final XSound BLOCK_AZALEA_HIT = std("block.azalea.hit");
   public static final XSound BLOCK_AZALEA_LEAVES_BREAK = std("block.azalea_leaves.break");
   public static final XSound BLOCK_AZALEA_LEAVES_FALL = std("block.azalea_leaves.fall");
   public static final XSound BLOCK_AZALEA_LEAVES_HIT = std("block.azalea_leaves.hit");
   public static final XSound BLOCK_AZALEA_LEAVES_PLACE = std("block.azalea_leaves.place");
   public static final XSound BLOCK_AZALEA_LEAVES_STEP = std("block.azalea_leaves.step");
   public static final XSound BLOCK_AZALEA_PLACE = std("block.azalea.place");
   public static final XSound BLOCK_AZALEA_STEP = std("block.azalea.step");
   public static final XSound BLOCK_BAMBOO_BREAK = std("block.bamboo.break");
   public static final XSound BLOCK_BAMBOO_FALL = std("block.bamboo.fall");
   public static final XSound BLOCK_BAMBOO_HIT = std("block.bamboo.hit");
   public static final XSound BLOCK_BAMBOO_PLACE = std("block.bamboo.place");
   public static final XSound BLOCK_BAMBOO_SAPLING_BREAK = std("block.bamboo_sapling.break");
   public static final XSound BLOCK_BAMBOO_SAPLING_HIT = std("block.bamboo_sapling.hit");
   public static final XSound BLOCK_BAMBOO_SAPLING_PLACE = std("block.bamboo_sapling.place");
   public static final XSound BLOCK_BAMBOO_STEP = std("block.bamboo.step");
   public static final XSound BLOCK_BAMBOO_WOOD_BREAK = std("block.bamboo_wood.break");
   public static final XSound BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF = std("block.bamboo_wood_button.click_off");
   public static final XSound BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON = std("block.bamboo_wood_button.click_on");
   public static final XSound BLOCK_BAMBOO_WOOD_DOOR_CLOSE = std("block.bamboo_wood_door.close");
   public static final XSound BLOCK_BAMBOO_WOOD_DOOR_OPEN = std("block.bamboo_wood_door.open");
   public static final XSound BLOCK_BAMBOO_WOOD_FALL = std("block.bamboo_wood.fall");
   public static final XSound BLOCK_BAMBOO_WOOD_FENCE_GATE_CLOSE = std("block.bamboo_wood_fence_gate.close");
   public static final XSound BLOCK_BAMBOO_WOOD_FENCE_GATE_OPEN = std("block.bamboo_wood_fence_gate.open");
   public static final XSound BLOCK_BAMBOO_WOOD_HANGING_SIGN_BREAK = std("block.bamboo_wood_hanging_sign.break");
   public static final XSound BLOCK_BAMBOO_WOOD_HANGING_SIGN_FALL = std("block.bamboo_wood_hanging_sign.fall");
   public static final XSound BLOCK_BAMBOO_WOOD_HANGING_SIGN_HIT = std("block.bamboo_wood_hanging_sign.hit");
   public static final XSound BLOCK_BAMBOO_WOOD_HANGING_SIGN_PLACE = std("block.bamboo_wood_hanging_sign.place");
   public static final XSound BLOCK_BAMBOO_WOOD_HANGING_SIGN_STEP = std("block.bamboo_wood_hanging_sign.step");
   public static final XSound BLOCK_BAMBOO_WOOD_HIT = std("block.bamboo_wood.hit");
   public static final XSound BLOCK_BAMBOO_WOOD_PLACE = std("block.bamboo_wood.place");
   public static final XSound BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF = std("block.bamboo_wood_pressure_plate.click_off");
   public static final XSound BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON = std("block.bamboo_wood_pressure_plate.click_on");
   public static final XSound BLOCK_BAMBOO_WOOD_STEP = std("block.bamboo_wood.step");
   public static final XSound BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE = std("block.bamboo_wood_trapdoor.close");
   public static final XSound BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN = std("block.bamboo_wood_trapdoor.open");
   public static final XSound BLOCK_BARREL_CLOSE = std("block.barrel.close");
   public static final XSound BLOCK_BARREL_OPEN = std("block.barrel.open");
   public static final XSound BLOCK_BASALT_BREAK = std("block.basalt.break");
   public static final XSound BLOCK_BASALT_FALL = std("block.basalt.fall");
   public static final XSound BLOCK_BASALT_HIT = std("block.basalt.hit");
   public static final XSound BLOCK_BASALT_PLACE = std("block.basalt.place");
   public static final XSound BLOCK_BASALT_STEP = std("block.basalt.step");
   public static final XSound BLOCK_BEACON_ACTIVATE = std("block.beacon.activate");
   public static final XSound BLOCK_BEACON_AMBIENT = std("block.beacon.ambient");
   public static final XSound BLOCK_BEEHIVE_DRIP = std("block.beehive.drip");
   public static final XSound BLOCK_BEEHIVE_ENTER = std("block.beehive.enter");
   public static final XSound BLOCK_BEEHIVE_EXIT = std("block.beehive.exit");
   public static final XSound BLOCK_BEEHIVE_SHEAR = std("block.beehive.shear");
   public static final XSound BLOCK_BEEHIVE_WORK = std("block.beehive.work");
   public static final XSound BLOCK_BELL_RESONATE = std("block.bell.resonate");
   public static final XSound BLOCK_BELL_USE = std("block.bell.use");
   public static final XSound BLOCK_BIG_DRIPLEAF_BREAK = std("block.big_dripleaf.break");
   public static final XSound BLOCK_BIG_DRIPLEAF_FALL = std("block.big_dripleaf.fall");
   public static final XSound BLOCK_BIG_DRIPLEAF_HIT = std("block.big_dripleaf.hit");
   public static final XSound BLOCK_BIG_DRIPLEAF_PLACE = std("block.big_dripleaf.place");
   public static final XSound BLOCK_BIG_DRIPLEAF_STEP = std("block.big_dripleaf.step");
   public static final XSound BLOCK_BIG_DRIPLEAF_TILT_DOWN = std("block.big_dripleaf.tilt_down");
   public static final XSound BLOCK_BIG_DRIPLEAF_TILT_UP = std("block.big_dripleaf.tilt_up");
   public static final XSound BLOCK_BLASTFURNACE_FIRE_CRACKLE = std("block.blastfurnace.fire_crackle");
   public static final XSound BLOCK_BONE_BLOCK_BREAK = std("block.bone_block.break");
   public static final XSound BLOCK_BONE_BLOCK_FALL = std("block.bone_block.fall");
   public static final XSound BLOCK_BONE_BLOCK_HIT = std("block.bone_block.hit");
   public static final XSound BLOCK_BONE_BLOCK_PLACE = std("block.bone_block.place");
   public static final XSound BLOCK_BONE_BLOCK_STEP = std("block.bone_block.step");
   public static final XSound BLOCK_BREWING_STAND_BREW = std("block.brewing_stand.brew");
   public static final XSound BLOCK_BUBBLE_COLUMN_BUBBLE_POP = std("block.bubble_column.bubble_pop");
   public static final XSound BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT = std("block.bubble_column.upwards_ambient");
   public static final XSound BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE = std("block.bubble_column.upwards_inside");
   public static final XSound BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT = std("block.bubble_column.whirlpool_ambient");
   public static final XSound BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE = std("block.bubble_column.whirlpool_inside");
   public static final XSound BLOCK_CAKE_ADD_CANDLE = std("block.cake.add_candle");
   public static final XSound BLOCK_CALCITE_BREAK = std("block.calcite.break");
   public static final XSound BLOCK_CALCITE_FALL = std("block.calcite.fall");
   public static final XSound BLOCK_CALCITE_HIT = std("block.calcite.hit");
   public static final XSound BLOCK_CALCITE_PLACE = std("block.calcite.place");
   public static final XSound BLOCK_CALCITE_STEP = std("block.calcite.step");
   public static final XSound BLOCK_CAMPFIRE_CRACKLE = std("block.campfire.crackle");
   public static final XSound BLOCK_CANDLE_AMBIENT = std("block.candle.ambient");
   public static final XSound BLOCK_CANDLE_BREAK = std("block.candle.break");
   public static final XSound BLOCK_CANDLE_EXTINGUISH = std("block.candle.extinguish");
   public static final XSound BLOCK_CANDLE_FALL = std("block.candle.fall");
   public static final XSound BLOCK_CANDLE_HIT = std("block.candle.hit");
   public static final XSound BLOCK_CANDLE_PLACE = std("block.candle.place");
   public static final XSound BLOCK_CANDLE_STEP = std("block.candle.step");
   public static final XSound BLOCK_CAVE_VINES_BREAK = std("block.cave_vines.break");
   public static final XSound BLOCK_CAVE_VINES_FALL = std("block.cave_vines.fall");
   public static final XSound BLOCK_CAVE_VINES_HIT = std("block.cave_vines.hit");
   public static final XSound BLOCK_CAVE_VINES_PICK_BERRIES = std("block.cave_vines.pick_berries");
   public static final XSound BLOCK_CAVE_VINES_PLACE = std("block.cave_vines.place");
   public static final XSound BLOCK_CAVE_VINES_STEP = std("block.cave_vines.step");
   public static final XSound BLOCK_CHAIN_BREAK = std("block.chain.break");
   public static final XSound BLOCK_CHAIN_FALL = std("block.chain.fall");
   public static final XSound BLOCK_CHAIN_HIT = std("block.chain.hit");
   public static final XSound BLOCK_CHAIN_PLACE = std("block.chain.place");
   public static final XSound BLOCK_CHAIN_STEP = std("block.chain.step");
   public static final XSound BLOCK_CHERRY_LEAVES_BREAK = std("block.cherry_leaves.break");
   public static final XSound BLOCK_CHERRY_LEAVES_FALL = std("block.cherry_leaves.fall");
   public static final XSound BLOCK_CHERRY_LEAVES_HIT = std("block.cherry_leaves.hit");
   public static final XSound BLOCK_CHERRY_LEAVES_PLACE = std("block.cherry_leaves.place");
   public static final XSound BLOCK_CHERRY_LEAVES_STEP = std("block.cherry_leaves.step");
   public static final XSound BLOCK_CHERRY_SAPLING_BREAK = std("block.cherry_sapling.break");
   public static final XSound BLOCK_CHERRY_SAPLING_FALL = std("block.cherry_sapling.fall");
   public static final XSound BLOCK_CHERRY_SAPLING_HIT = std("block.cherry_sapling.hit");
   public static final XSound BLOCK_CHERRY_SAPLING_PLACE = std("block.cherry_sapling.place");
   public static final XSound BLOCK_CHERRY_SAPLING_STEP = std("block.cherry_sapling.step");
   public static final XSound BLOCK_CHERRY_WOOD_BREAK = std("block.cherry_wood.break");
   public static final XSound BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF = std("block.cherry_wood_button.click_off");
   public static final XSound BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON = std("block.cherry_wood_button.click_on");
   public static final XSound BLOCK_CHERRY_WOOD_DOOR_CLOSE = std("block.cherry_wood_door.close");
   public static final XSound BLOCK_CHERRY_WOOD_DOOR_OPEN = std("block.cherry_wood_door.open");
   public static final XSound BLOCK_CHERRY_WOOD_FALL = std("block.cherry_wood.fall");
   public static final XSound BLOCK_CHERRY_WOOD_FENCE_GATE_CLOSE = std("block.cherry_wood_fence_gate.close");
   public static final XSound BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN = std("block.cherry_wood_fence_gate.open");
   public static final XSound BLOCK_CHERRY_WOOD_HANGING_SIGN_BREAK = std("block.cherry_wood_hanging_sign.break");
   public static final XSound BLOCK_CHERRY_WOOD_HANGING_SIGN_FALL = std("block.cherry_wood_hanging_sign.fall");
   public static final XSound BLOCK_CHERRY_WOOD_HANGING_SIGN_HIT = std("block.cherry_wood_hanging_sign.hit");
   public static final XSound BLOCK_CHERRY_WOOD_HANGING_SIGN_PLACE = std("block.cherry_wood_hanging_sign.place");
   public static final XSound BLOCK_CHERRY_WOOD_HANGING_SIGN_STEP = std("block.cherry_wood_hanging_sign.step");
   public static final XSound BLOCK_CHERRY_WOOD_HIT = std("block.cherry_wood.hit");
   public static final XSound BLOCK_CHERRY_WOOD_PLACE = std("block.cherry_wood.place");
   public static final XSound BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF = std("block.cherry_wood_pressure_plate.click_off");
   public static final XSound BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON = std("block.cherry_wood_pressure_plate.click_on");
   public static final XSound BLOCK_CHERRY_WOOD_STEP = std("block.cherry_wood.step");
   public static final XSound BLOCK_CHERRY_WOOD_TRAPDOOR_CLOSE = std("block.cherry_wood_trapdoor.close");
   public static final XSound BLOCK_CHERRY_WOOD_TRAPDOOR_OPEN = std("block.cherry_wood_trapdoor.open");
   public static final XSound BLOCK_CHEST_LOCKED = std("block.chest.locked");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_BREAK = std("block.chiseled_bookshelf.break");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_FALL = std("block.chiseled_bookshelf.fall");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_HIT = std("block.chiseled_bookshelf.hit");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_INSERT = std("block.chiseled_bookshelf.insert");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED = std("block.chiseled_bookshelf.insert.enchanted");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_PICKUP = std("block.chiseled_bookshelf.pickup");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED = std("block.chiseled_bookshelf.pickup.enchanted");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_PLACE = std("block.chiseled_bookshelf.place");
   public static final XSound BLOCK_CHISELED_BOOKSHELF_STEP = std("block.chiseled_bookshelf.step");
   public static final XSound BLOCK_CHORUS_FLOWER_DEATH = std("block.chorus_flower.death");
   public static final XSound BLOCK_CHORUS_FLOWER_GROW = std("block.chorus_flower.grow");
   public static final XSound BLOCK_COBWEB_BREAK = std("block.cobweb.break");
   public static final XSound BLOCK_COBWEB_FALL = std("block.cobweb.fall");
   public static final XSound BLOCK_COBWEB_HIT = std("block.cobweb.hit");
   public static final XSound BLOCK_COBWEB_PLACE = std("block.cobweb.place");
   public static final XSound BLOCK_COBWEB_STEP = std("block.cobweb.step");
   public static final XSound BLOCK_COMPARATOR_CLICK = std("block.comparator.click");
   public static final XSound BLOCK_COMPOSTER_EMPTY = std("block.composter.empty");
   public static final XSound BLOCK_COMPOSTER_FILL = std("block.composter.fill");
   public static final XSound BLOCK_COMPOSTER_FILL_SUCCESS = std("block.composter.fill_success");
   public static final XSound BLOCK_COMPOSTER_READY = std("block.composter.ready");
   public static final XSound BLOCK_CONDUIT_ACTIVATE = std("block.conduit.activate");
   public static final XSound BLOCK_CONDUIT_AMBIENT = std("block.conduit.ambient");
   public static final XSound BLOCK_CONDUIT_AMBIENT_SHORT = std("block.conduit.ambient.short");
   public static final XSound BLOCK_CONDUIT_ATTACK_TARGET = std("block.conduit.attack.target");
   public static final XSound BLOCK_CONDUIT_DEACTIVATE = std("block.conduit.deactivate");
   public static final XSound BLOCK_COPPER_BREAK = std("block.copper.break");
   public static final XSound BLOCK_COPPER_BULB_BREAK = std("block.copper_bulb.break");
   public static final XSound BLOCK_COPPER_BULB_FALL = std("block.copper_bulb.fall");
   public static final XSound BLOCK_COPPER_BULB_HIT = std("block.copper_bulb.hit");
   public static final XSound BLOCK_COPPER_BULB_PLACE = std("block.copper_bulb.place");
   public static final XSound BLOCK_COPPER_BULB_STEP = std("block.copper_bulb.step");
   public static final XSound BLOCK_COPPER_BULB_TURN_OFF = std("block.copper_bulb.turn_off");
   public static final XSound BLOCK_COPPER_BULB_TURN_ON = std("block.copper_bulb.turn_on");
   public static final XSound BLOCK_COPPER_DOOR_CLOSE = std("block.copper_door.close");
   public static final XSound BLOCK_COPPER_DOOR_OPEN = std("block.copper_door.open");
   public static final XSound BLOCK_COPPER_FALL = std("block.copper.fall");
   public static final XSound BLOCK_COPPER_GRATE_BREAK = std("block.copper_grate.break");
   public static final XSound BLOCK_COPPER_GRATE_FALL = std("block.copper_grate.fall");
   public static final XSound BLOCK_COPPER_GRATE_HIT = std("block.copper_grate.hit");
   public static final XSound BLOCK_COPPER_GRATE_PLACE = std("block.copper_grate.place");
   public static final XSound BLOCK_COPPER_GRATE_STEP = std("block.copper_grate.step");
   public static final XSound BLOCK_COPPER_HIT = std("block.copper.hit");
   public static final XSound BLOCK_COPPER_PLACE = std("block.copper.place");
   public static final XSound BLOCK_COPPER_STEP = std("block.copper.step");
   public static final XSound BLOCK_COPPER_TRAPDOOR_CLOSE = std("block.copper_trapdoor.close");
   public static final XSound BLOCK_COPPER_TRAPDOOR_OPEN = std("block.copper_trapdoor.open");
   public static final XSound BLOCK_CORAL_BLOCK_BREAK = std("block.coral_block.break");
   public static final XSound BLOCK_CORAL_BLOCK_FALL = std("block.coral_block.fall");
   public static final XSound BLOCK_CORAL_BLOCK_HIT = std("block.coral_block.hit");
   public static final XSound BLOCK_CORAL_BLOCK_PLACE = std("block.coral_block.place");
   public static final XSound BLOCK_CORAL_BLOCK_STEP = std("block.coral_block.step");
   public static final XSound BLOCK_CRAFTER_CRAFT = std("block.crafter.craft");
   public static final XSound BLOCK_CRAFTER_FAIL = std("block.crafter.fail");
   public static final XSound BLOCK_CREAKING_HEART_BREAK = std("block.creaking_heart.break");
   public static final XSound BLOCK_CREAKING_HEART_FALL = std("block.creaking_heart.fall");
   public static final XSound BLOCK_CREAKING_HEART_HIT = std("block.creaking_heart.hit");
   public static final XSound BLOCK_CREAKING_HEART_HURT = std("block.creaking_heart.hurt");
   public static final XSound BLOCK_CREAKING_HEART_IDLE = std("block.creaking_heart.idle");
   public static final XSound BLOCK_CREAKING_HEART_PLACE = std("block.creaking_heart.place");
   public static final XSound BLOCK_CREAKING_HEART_SPAWN = std("block.creaking_heart.spawn");
   public static final XSound BLOCK_CREAKING_HEART_STEP = std("block.creaking_heart.step");
   public static final XSound BLOCK_CROP_BREAK = std("block.crop.break");
   public static final XSound BLOCK_DECORATED_POT_BREAK = std("block.decorated_pot.break");
   public static final XSound BLOCK_DECORATED_POT_FALL = std("block.decorated_pot.fall");
   public static final XSound BLOCK_DECORATED_POT_HIT = std("block.decorated_pot.hit");
   public static final XSound BLOCK_DECORATED_POT_INSERT = std("block.decorated_pot.insert");
   public static final XSound BLOCK_DECORATED_POT_INSERT_FAIL = std("block.decorated_pot.insert_fail");
   public static final XSound BLOCK_DECORATED_POT_PLACE = std("block.decorated_pot.place");
   public static final XSound BLOCK_DECORATED_POT_SHATTER = std("block.decorated_pot.shatter");
   public static final XSound BLOCK_DECORATED_POT_STEP = std("block.decorated_pot.step");
   public static final XSound BLOCK_DEEPSLATE_BREAK = std("block.deepslate.break");
   public static final XSound BLOCK_DEEPSLATE_BRICKS_BREAK = std("block.deepslate_bricks.break");
   public static final XSound BLOCK_DEEPSLATE_BRICKS_FALL = std("block.deepslate_bricks.fall");
   public static final XSound BLOCK_DEEPSLATE_BRICKS_HIT = std("block.deepslate_bricks.hit");
   public static final XSound BLOCK_DEEPSLATE_BRICKS_PLACE = std("block.deepslate_bricks.place");
   public static final XSound BLOCK_DEEPSLATE_BRICKS_STEP = std("block.deepslate_bricks.step");
   public static final XSound BLOCK_DEEPSLATE_FALL = std("block.deepslate.fall");
   public static final XSound BLOCK_DEEPSLATE_HIT = std("block.deepslate.hit");
   public static final XSound BLOCK_DEEPSLATE_PLACE = std("block.deepslate.place");
   public static final XSound BLOCK_DEEPSLATE_STEP = std("block.deepslate.step");
   public static final XSound BLOCK_DEEPSLATE_TILES_BREAK = std("block.deepslate_tiles.break");
   public static final XSound BLOCK_DEEPSLATE_TILES_FALL = std("block.deepslate_tiles.fall");
   public static final XSound BLOCK_DEEPSLATE_TILES_HIT = std("block.deepslate_tiles.hit");
   public static final XSound BLOCK_DEEPSLATE_TILES_PLACE = std("block.deepslate_tiles.place");
   public static final XSound BLOCK_DEEPSLATE_TILES_STEP = std("block.deepslate_tiles.step");
   public static final XSound BLOCK_DISPENSER_DISPENSE = std("block.dispenser.dispense");
   public static final XSound BLOCK_DISPENSER_FAIL = std("block.dispenser.fail");
   public static final XSound BLOCK_DISPENSER_LAUNCH = std("block.dispenser.launch");
   public static final XSound BLOCK_DRIPSTONE_BLOCK_BREAK = std("block.dripstone_block.break");
   public static final XSound BLOCK_DRIPSTONE_BLOCK_FALL = std("block.dripstone_block.fall");
   public static final XSound BLOCK_DRIPSTONE_BLOCK_HIT = std("block.dripstone_block.hit");
   public static final XSound BLOCK_DRIPSTONE_BLOCK_PLACE = std("block.dripstone_block.place");
   public static final XSound BLOCK_DRIPSTONE_BLOCK_STEP = std("block.dripstone_block.step");
   public static final XSound BLOCK_ENCHANTMENT_TABLE_USE = std("block.enchantment_table.use");
   public static final XSound BLOCK_ENDER_CHEST_CLOSE = std("block.ender_chest.close", "BLOCK_ENDERCHEST_CLOSE");
   public static final XSound BLOCK_ENDER_CHEST_OPEN = std("block.ender_chest.open", "BLOCK_ENDERCHEST_OPEN");
   public static final XSound BLOCK_END_GATEWAY_SPAWN = std("block.end_gateway.spawn");
   public static final XSound BLOCK_END_PORTAL_FRAME_FILL = std("block.end_portal_frame.fill");
   public static final XSound BLOCK_END_PORTAL_SPAWN = std("block.end_portal.spawn");
   public static final XSound BLOCK_FENCE_GATE_CLOSE = std("block.fence_gate.close");
   public static final XSound BLOCK_FENCE_GATE_OPEN = std("block.fence_gate.open");
   public static final XSound BLOCK_FLOWERING_AZALEA_BREAK = std("block.flowering_azalea.break");
   public static final XSound BLOCK_FLOWERING_AZALEA_FALL = std("block.flowering_azalea.fall");
   public static final XSound BLOCK_FLOWERING_AZALEA_HIT = std("block.flowering_azalea.hit");
   public static final XSound BLOCK_FLOWERING_AZALEA_PLACE = std("block.flowering_azalea.place");
   public static final XSound BLOCK_FLOWERING_AZALEA_STEP = std("block.flowering_azalea.step");
   public static final XSound BLOCK_FROGLIGHT_BREAK = std("block.froglight.break");
   public static final XSound BLOCK_FROGLIGHT_FALL = std("block.froglight.fall");
   public static final XSound BLOCK_FROGLIGHT_HIT = std("block.froglight.hit");
   public static final XSound BLOCK_FROGLIGHT_PLACE = std("block.froglight.place");
   public static final XSound BLOCK_FROGLIGHT_STEP = std("block.froglight.step");
   public static final XSound BLOCK_FROGSPAWN_BREAK = std("block.frogspawn.break");
   public static final XSound BLOCK_FROGSPAWN_FALL = std("block.frogspawn.fall");
   public static final XSound BLOCK_FROGSPAWN_HATCH = std("block.frogspawn.hatch");
   public static final XSound BLOCK_FROGSPAWN_HIT = std("block.frogspawn.hit");
   public static final XSound BLOCK_FROGSPAWN_PLACE = std("block.frogspawn.place");
   public static final XSound BLOCK_FROGSPAWN_STEP = std("block.frogspawn.step");
   public static final XSound BLOCK_FUNGUS_BREAK = std("block.fungus.break");
   public static final XSound BLOCK_FUNGUS_FALL = std("block.fungus.fall");
   public static final XSound BLOCK_FUNGUS_HIT = std("block.fungus.hit");
   public static final XSound BLOCK_FUNGUS_PLACE = std("block.fungus.place");
   public static final XSound BLOCK_FUNGUS_STEP = std("block.fungus.step");
   public static final XSound BLOCK_FURNACE_FIRE_CRACKLE = std("block.furnace.fire_crackle");
   public static final XSound BLOCK_GILDED_BLACKSTONE_BREAK = std("block.gilded_blackstone.break");
   public static final XSound BLOCK_GILDED_BLACKSTONE_FALL = std("block.gilded_blackstone.fall");
   public static final XSound BLOCK_GILDED_BLACKSTONE_HIT = std("block.gilded_blackstone.hit");
   public static final XSound BLOCK_GILDED_BLACKSTONE_PLACE = std("block.gilded_blackstone.place");
   public static final XSound BLOCK_GILDED_BLACKSTONE_STEP = std("block.gilded_blackstone.step");
   public static final XSound BLOCK_GLASS_FALL = std("block.glass.fall");
   public static final XSound BLOCK_GLASS_HIT = std("block.glass.hit");
   public static final XSound BLOCK_GLASS_PLACE = std("block.glass.place");
   public static final XSound BLOCK_GLASS_STEP = std("block.glass.step");
   public static final XSound BLOCK_GRASS_FALL = std("block.grass.fall");
   public static final XSound BLOCK_GRASS_HIT = std("block.grass.hit");
   public static final XSound BLOCK_GRASS_PLACE = std("block.grass.place");
   public static final XSound BLOCK_GRAVEL_FALL = std("block.gravel.fall");
   public static final XSound BLOCK_GRAVEL_HIT = std("block.gravel.hit");
   public static final XSound BLOCK_GRAVEL_PLACE = std("block.gravel.place");
   public static final XSound BLOCK_GRINDSTONE_USE = std("block.grindstone.use");
   public static final XSound BLOCK_GROWING_PLANT_CROP = std("block.growing_plant.crop");
   public static final XSound BLOCK_HANGING_ROOTS_BREAK = std("block.hanging_roots.break");
   public static final XSound BLOCK_HANGING_ROOTS_FALL = std("block.hanging_roots.fall");
   public static final XSound BLOCK_HANGING_ROOTS_HIT = std("block.hanging_roots.hit");
   public static final XSound BLOCK_HANGING_ROOTS_PLACE = std("block.hanging_roots.place");
   public static final XSound BLOCK_HANGING_ROOTS_STEP = std("block.hanging_roots.step");
   public static final XSound BLOCK_HANGING_SIGN_BREAK = std("block.hanging_sign.break");
   public static final XSound BLOCK_HANGING_SIGN_FALL = std("block.hanging_sign.fall");
   public static final XSound BLOCK_HANGING_SIGN_HIT = std("block.hanging_sign.hit");
   public static final XSound BLOCK_HANGING_SIGN_PLACE = std("block.hanging_sign.place");
   public static final XSound BLOCK_HANGING_SIGN_STEP = std("block.hanging_sign.step");
   public static final XSound BLOCK_HANGING_SIGN_WAXED_INTERACT_FAIL = std("block.hanging_sign.waxed_interact_fail");
   public static final XSound BLOCK_HEAVY_CORE_BREAK = std("block.heavy_core.break");
   public static final XSound BLOCK_HEAVY_CORE_FALL = std("block.heavy_core.fall");
   public static final XSound BLOCK_HEAVY_CORE_HIT = std("block.heavy_core.hit");
   public static final XSound BLOCK_HEAVY_CORE_PLACE = std("block.heavy_core.place");
   public static final XSound BLOCK_HEAVY_CORE_STEP = std("block.heavy_core.step");
   public static final XSound BLOCK_HONEY_BLOCK_BREAK = std("block.honey_block.break");
   public static final XSound BLOCK_HONEY_BLOCK_FALL = std("block.honey_block.fall");
   public static final XSound BLOCK_HONEY_BLOCK_HIT = std("block.honey_block.hit");
   public static final XSound BLOCK_HONEY_BLOCK_PLACE = std("block.honey_block.place");
   public static final XSound BLOCK_HONEY_BLOCK_SLIDE = std("block.honey_block.slide");
   public static final XSound BLOCK_HONEY_BLOCK_STEP = std("block.honey_block.step");
   public static final XSound BLOCK_IRON_DOOR_CLOSE = std("block.iron_door.close");
   public static final XSound BLOCK_IRON_DOOR_OPEN = std("block.iron_door.open");
   public static final XSound BLOCK_IRON_TRAPDOOR_CLOSE = std("block.iron_trapdoor.close");
   public static final XSound BLOCK_IRON_TRAPDOOR_OPEN = std("block.iron_trapdoor.open");
   public static final XSound BLOCK_LADDER_BREAK = std("block.ladder.break");
   public static final XSound BLOCK_LADDER_FALL = std("block.ladder.fall");
   public static final XSound BLOCK_LADDER_HIT = std("block.ladder.hit");
   public static final XSound BLOCK_LADDER_PLACE = std("block.ladder.place");
   public static final XSound BLOCK_LANTERN_BREAK = std("block.lantern.break");
   public static final XSound BLOCK_LANTERN_FALL = std("block.lantern.fall");
   public static final XSound BLOCK_LANTERN_HIT = std("block.lantern.hit");
   public static final XSound BLOCK_LANTERN_PLACE = std("block.lantern.place");
   public static final XSound BLOCK_LANTERN_STEP = std("block.lantern.step");
   public static final XSound BLOCK_LARGE_AMETHYST_BUD_BREAK = std("block.large_amethyst_bud.break");
   public static final XSound BLOCK_LARGE_AMETHYST_BUD_PLACE = std("block.large_amethyst_bud.place");
   public static final XSound BLOCK_LAVA_EXTINGUISH = std("block.lava.extinguish");
   public static final XSound BLOCK_LEVER_CLICK = std("block.lever.click");
   public static final XSound BLOCK_LODESTONE_BREAK = std("block.lodestone.break");
   public static final XSound BLOCK_LODESTONE_FALL = std("block.lodestone.fall");
   public static final XSound BLOCK_LODESTONE_HIT = std("block.lodestone.hit");
   public static final XSound BLOCK_LODESTONE_PLACE = std("block.lodestone.place");
   public static final XSound BLOCK_LODESTONE_STEP = std("block.lodestone.step");
   public static final XSound BLOCK_MANGROVE_ROOTS_BREAK = std("block.mangrove_roots.break");
   public static final XSound BLOCK_MANGROVE_ROOTS_FALL = std("block.mangrove_roots.fall");
   public static final XSound BLOCK_MANGROVE_ROOTS_HIT = std("block.mangrove_roots.hit");
   public static final XSound BLOCK_MANGROVE_ROOTS_PLACE = std("block.mangrove_roots.place");
   public static final XSound BLOCK_MANGROVE_ROOTS_STEP = std("block.mangrove_roots.step");
   public static final XSound BLOCK_MEDIUM_AMETHYST_BUD_BREAK = std("block.medium_amethyst_bud.break");
   public static final XSound BLOCK_MEDIUM_AMETHYST_BUD_PLACE = std("block.medium_amethyst_bud.place");
   public static final XSound BLOCK_METAL_BREAK = std("block.metal.break");
   public static final XSound BLOCK_METAL_FALL = std("block.metal.fall");
   public static final XSound BLOCK_METAL_HIT = std("block.metal.hit");
   public static final XSound BLOCK_METAL_PLACE = std("block.metal.place");
   public static final XSound BLOCK_METAL_STEP = std("block.metal.step");
   public static final XSound BLOCK_MOSS_BREAK = std("block.moss.break");
   public static final XSound BLOCK_MOSS_CARPET_BREAK = std("block.moss_carpet.break");
   public static final XSound BLOCK_MOSS_CARPET_FALL = std("block.moss_carpet.fall");
   public static final XSound BLOCK_MOSS_CARPET_HIT = std("block.moss_carpet.hit");
   public static final XSound BLOCK_MOSS_CARPET_PLACE = std("block.moss_carpet.place");
   public static final XSound BLOCK_MOSS_CARPET_STEP = std("block.moss_carpet.step");
   public static final XSound BLOCK_MOSS_FALL = std("block.moss.fall");
   public static final XSound BLOCK_MOSS_HIT = std("block.moss.hit");
   public static final XSound BLOCK_MOSS_PLACE = std("block.moss.place");
   public static final XSound BLOCK_MOSS_STEP = std("block.moss.step");
   public static final XSound BLOCK_MUDDY_MANGROVE_ROOTS_BREAK = std("block.muddy_mangrove_roots.break");
   public static final XSound BLOCK_MUDDY_MANGROVE_ROOTS_FALL = std("block.muddy_mangrove_roots.fall");
   public static final XSound BLOCK_MUDDY_MANGROVE_ROOTS_HIT = std("block.muddy_mangrove_roots.hit");
   public static final XSound BLOCK_MUDDY_MANGROVE_ROOTS_PLACE = std("block.muddy_mangrove_roots.place");
   public static final XSound BLOCK_MUDDY_MANGROVE_ROOTS_STEP = std("block.muddy_mangrove_roots.step");
   public static final XSound BLOCK_MUD_BREAK = std("block.mud.break");
   public static final XSound BLOCK_MUD_BRICKS_BREAK = std("block.mud_bricks.break");
   public static final XSound BLOCK_MUD_BRICKS_FALL = std("block.mud_bricks.fall");
   public static final XSound BLOCK_MUD_BRICKS_HIT = std("block.mud_bricks.hit");
   public static final XSound BLOCK_MUD_BRICKS_PLACE = std("block.mud_bricks.place");
   public static final XSound BLOCK_MUD_BRICKS_STEP = std("block.mud_bricks.step");
   public static final XSound BLOCK_MUD_FALL = std("block.mud.fall");
   public static final XSound BLOCK_MUD_HIT = std("block.mud.hit");
   public static final XSound BLOCK_MUD_PLACE = std("block.mud.place");
   public static final XSound BLOCK_MUD_STEP = std("block.mud.step");
   public static final XSound BLOCK_NETHERITE_BLOCK_BREAK = std("block.netherite_block.break");
   public static final XSound BLOCK_NETHERITE_BLOCK_FALL = std("block.netherite_block.fall");
   public static final XSound BLOCK_NETHERITE_BLOCK_HIT = std("block.netherite_block.hit");
   public static final XSound BLOCK_NETHERITE_BLOCK_PLACE = std("block.netherite_block.place");
   public static final XSound BLOCK_NETHERITE_BLOCK_STEP = std("block.netherite_block.step");
   public static final XSound BLOCK_NETHERRACK_BREAK = std("block.netherrack.break");
   public static final XSound BLOCK_NETHERRACK_FALL = std("block.netherrack.fall");
   public static final XSound BLOCK_NETHERRACK_HIT = std("block.netherrack.hit");
   public static final XSound BLOCK_NETHERRACK_PLACE = std("block.netherrack.place");
   public static final XSound BLOCK_NETHERRACK_STEP = std("block.netherrack.step");
   public static final XSound BLOCK_NETHER_BRICKS_BREAK = std("block.nether_bricks.break");
   public static final XSound BLOCK_NETHER_BRICKS_FALL = std("block.nether_bricks.fall");
   public static final XSound BLOCK_NETHER_BRICKS_HIT = std("block.nether_bricks.hit");
   public static final XSound BLOCK_NETHER_BRICKS_PLACE = std("block.nether_bricks.place");
   public static final XSound BLOCK_NETHER_BRICKS_STEP = std("block.nether_bricks.step");
   public static final XSound BLOCK_NETHER_GOLD_ORE_BREAK = std("block.nether_gold_ore.break");
   public static final XSound BLOCK_NETHER_GOLD_ORE_FALL = std("block.nether_gold_ore.fall");
   public static final XSound BLOCK_NETHER_GOLD_ORE_HIT = std("block.nether_gold_ore.hit");
   public static final XSound BLOCK_NETHER_GOLD_ORE_PLACE = std("block.nether_gold_ore.place");
   public static final XSound BLOCK_NETHER_GOLD_ORE_STEP = std("block.nether_gold_ore.step");
   public static final XSound BLOCK_NETHER_ORE_BREAK = std("block.nether_ore.break");
   public static final XSound BLOCK_NETHER_ORE_FALL = std("block.nether_ore.fall");
   public static final XSound BLOCK_NETHER_ORE_HIT = std("block.nether_ore.hit");
   public static final XSound BLOCK_NETHER_ORE_PLACE = std("block.nether_ore.place");
   public static final XSound BLOCK_NETHER_ORE_STEP = std("block.nether_ore.step");
   public static final XSound BLOCK_NETHER_SPROUTS_BREAK = std("block.nether_sprouts.break");
   public static final XSound BLOCK_NETHER_SPROUTS_FALL = std("block.nether_sprouts.fall");
   public static final XSound BLOCK_NETHER_SPROUTS_HIT = std("block.nether_sprouts.hit");
   public static final XSound BLOCK_NETHER_SPROUTS_PLACE = std("block.nether_sprouts.place");
   public static final XSound BLOCK_NETHER_SPROUTS_STEP = std("block.nether_sprouts.step");
   public static final XSound BLOCK_NETHER_WART_BREAK = std("block.nether_wart.break");
   public static final XSound BLOCK_NETHER_WOOD_BREAK = std("block.nether_wood.break");
   public static final XSound BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF = std("block.nether_wood_button.click_off");
   public static final XSound BLOCK_NETHER_WOOD_BUTTON_CLICK_ON = std("block.nether_wood_button.click_on");
   public static final XSound BLOCK_NETHER_WOOD_DOOR_CLOSE = std("block.nether_wood_door.close");
   public static final XSound BLOCK_NETHER_WOOD_DOOR_OPEN = std("block.nether_wood_door.open");
   public static final XSound BLOCK_NETHER_WOOD_FALL = std("block.nether_wood.fall");
   public static final XSound BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE = std("block.nether_wood_fence_gate.close");
   public static final XSound BLOCK_NETHER_WOOD_FENCE_GATE_OPEN = std("block.nether_wood_fence_gate.open");
   public static final XSound BLOCK_NETHER_WOOD_HANGING_SIGN_BREAK = std("block.nether_wood_hanging_sign.break");
   public static final XSound BLOCK_NETHER_WOOD_HANGING_SIGN_FALL = std("block.nether_wood_hanging_sign.fall");
   public static final XSound BLOCK_NETHER_WOOD_HANGING_SIGN_HIT = std("block.nether_wood_hanging_sign.hit");
   public static final XSound BLOCK_NETHER_WOOD_HANGING_SIGN_PLACE = std("block.nether_wood_hanging_sign.place");
   public static final XSound BLOCK_NETHER_WOOD_HANGING_SIGN_STEP = std("block.nether_wood_hanging_sign.step");
   public static final XSound BLOCK_NETHER_WOOD_HIT = std("block.nether_wood.hit");
   public static final XSound BLOCK_NETHER_WOOD_PLACE = std("block.nether_wood.place");
   public static final XSound BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF = std("block.nether_wood_pressure_plate.click_off");
   public static final XSound BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON = std("block.nether_wood_pressure_plate.click_on");
   public static final XSound BLOCK_NETHER_WOOD_STEP = std("block.nether_wood.step");
   public static final XSound BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE = std("block.nether_wood_trapdoor.close");
   public static final XSound BLOCK_NETHER_WOOD_TRAPDOOR_OPEN = std("block.nether_wood_trapdoor.open");
   public static final XSound BLOCK_NOTE_BLOCK_BANJO = std("block.note_block.banjo");
   public static final XSound BLOCK_NOTE_BLOCK_BIT = std("block.note_block.bit");
   public static final XSound BLOCK_NOTE_BLOCK_COW_BELL = std("block.note_block.cow_bell");
   public static final XSound BLOCK_NOTE_BLOCK_DIDGERIDOO = std("block.note_block.didgeridoo");
   public static final XSound BLOCK_NOTE_BLOCK_IMITATE_CREEPER = std("block.note_block.imitate.creeper");
   public static final XSound BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON = std("block.note_block.imitate.ender_dragon");
   public static final XSound BLOCK_NOTE_BLOCK_IMITATE_PIGLIN = std("block.note_block.imitate.piglin");
   public static final XSound BLOCK_NOTE_BLOCK_IMITATE_SKELETON = std("block.note_block.imitate.skeleton");
   public static final XSound BLOCK_NOTE_BLOCK_IMITATE_WITHER_SKELETON = std("block.note_block.imitate.wither_skeleton");
   public static final XSound BLOCK_NOTE_BLOCK_IMITATE_ZOMBIE = std("block.note_block.imitate.zombie");
   public static final XSound BLOCK_NOTE_BLOCK_IRON_XYLOPHONE = std("block.note_block.iron_xylophone");
   public static final XSound BLOCK_NYLIUM_BREAK = std("block.nylium.break");
   public static final XSound BLOCK_NYLIUM_FALL = std("block.nylium.fall");
   public static final XSound BLOCK_NYLIUM_HIT = std("block.nylium.hit");
   public static final XSound BLOCK_NYLIUM_PLACE = std("block.nylium.place");
   public static final XSound BLOCK_NYLIUM_STEP = std("block.nylium.step");
   public static final XSound BLOCK_PACKED_MUD_BREAK = std("block.packed_mud.break");
   public static final XSound BLOCK_PACKED_MUD_FALL = std("block.packed_mud.fall");
   public static final XSound BLOCK_PACKED_MUD_HIT = std("block.packed_mud.hit");
   public static final XSound BLOCK_PACKED_MUD_PLACE = std("block.packed_mud.place");
   public static final XSound BLOCK_PACKED_MUD_STEP = std("block.packed_mud.step");
   public static final XSound BLOCK_PALE_HANGING_MOSS_IDLE = std("block.pale_hanging_moss.idle");
   public static final XSound BLOCK_PINK_PETALS_BREAK = std("block.pink_petals.break");
   public static final XSound BLOCK_PINK_PETALS_FALL = std("block.pink_petals.fall");
   public static final XSound BLOCK_PINK_PETALS_HIT = std("block.pink_petals.hit");
   public static final XSound BLOCK_PINK_PETALS_PLACE = std("block.pink_petals.place");
   public static final XSound BLOCK_PINK_PETALS_STEP = std("block.pink_petals.step");
   public static final XSound BLOCK_POINTED_DRIPSTONE_BREAK = std("block.pointed_dripstone.break");
   public static final XSound BLOCK_POINTED_DRIPSTONE_DRIP_LAVA = std("block.pointed_dripstone.drip_lava");
   public static final XSound BLOCK_POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON = std("block.pointed_dripstone.drip_lava_into_cauldron");
   public static final XSound BLOCK_POINTED_DRIPSTONE_DRIP_WATER = std("block.pointed_dripstone.drip_water");
   public static final XSound BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON = std("block.pointed_dripstone.drip_water_into_cauldron");
   public static final XSound BLOCK_POINTED_DRIPSTONE_FALL = std("block.pointed_dripstone.fall");
   public static final XSound BLOCK_POINTED_DRIPSTONE_HIT = std("block.pointed_dripstone.hit");
   public static final XSound BLOCK_POINTED_DRIPSTONE_LAND = std("block.pointed_dripstone.land");
   public static final XSound BLOCK_POINTED_DRIPSTONE_PLACE = std("block.pointed_dripstone.place");
   public static final XSound BLOCK_POINTED_DRIPSTONE_STEP = std("block.pointed_dripstone.step");
   public static final XSound BLOCK_POLISHED_DEEPSLATE_BREAK = std("block.polished_deepslate.break");
   public static final XSound BLOCK_POLISHED_DEEPSLATE_FALL = std("block.polished_deepslate.fall");
   public static final XSound BLOCK_POLISHED_DEEPSLATE_HIT = std("block.polished_deepslate.hit");
   public static final XSound BLOCK_POLISHED_DEEPSLATE_PLACE = std("block.polished_deepslate.place");
   public static final XSound BLOCK_POLISHED_DEEPSLATE_STEP = std("block.polished_deepslate.step");
   public static final XSound BLOCK_POLISHED_TUFF_BREAK = std("block.polished_tuff.break");
   public static final XSound BLOCK_POLISHED_TUFF_FALL = std("block.polished_tuff.fall");
   public static final XSound BLOCK_POLISHED_TUFF_HIT = std("block.polished_tuff.hit");
   public static final XSound BLOCK_POLISHED_TUFF_PLACE = std("block.polished_tuff.place");
   public static final XSound BLOCK_POLISHED_TUFF_STEP = std("block.polished_tuff.step");
   public static final XSound BLOCK_POWDER_SNOW_BREAK = std("block.powder_snow.break");
   public static final XSound BLOCK_POWDER_SNOW_FALL = std("block.powder_snow.fall");
   public static final XSound BLOCK_POWDER_SNOW_HIT = std("block.powder_snow.hit");
   public static final XSound BLOCK_POWDER_SNOW_PLACE = std("block.powder_snow.place");
   public static final XSound BLOCK_POWDER_SNOW_STEP = std("block.powder_snow.step");
   public static final XSound BLOCK_PUMPKIN_CARVE = std("block.pumpkin.carve");
   public static final XSound BLOCK_REDSTONE_TORCH_BURNOUT = std("block.redstone_torch.burnout");
   public static final XSound BLOCK_RESPAWN_ANCHOR_AMBIENT = std("block.respawn_anchor.ambient");
   public static final XSound BLOCK_RESPAWN_ANCHOR_CHARGE = std("block.respawn_anchor.charge");
   public static final XSound BLOCK_RESPAWN_ANCHOR_DEPLETE = std("block.respawn_anchor.deplete");
   public static final XSound BLOCK_RESPAWN_ANCHOR_SET_SPAWN = std("block.respawn_anchor.set_spawn");
   public static final XSound BLOCK_ROOTED_DIRT_BREAK = std("block.rooted_dirt.break");
   public static final XSound BLOCK_ROOTED_DIRT_FALL = std("block.rooted_dirt.fall");
   public static final XSound BLOCK_ROOTED_DIRT_HIT = std("block.rooted_dirt.hit");
   public static final XSound BLOCK_ROOTED_DIRT_PLACE = std("block.rooted_dirt.place");
   public static final XSound BLOCK_ROOTED_DIRT_STEP = std("block.rooted_dirt.step");
   public static final XSound BLOCK_ROOTS_BREAK = std("block.roots.break");
   public static final XSound BLOCK_ROOTS_FALL = std("block.roots.fall");
   public static final XSound BLOCK_ROOTS_HIT = std("block.roots.hit");
   public static final XSound BLOCK_ROOTS_PLACE = std("block.roots.place");
   public static final XSound BLOCK_ROOTS_STEP = std("block.roots.step");
   public static final XSound BLOCK_SAND_FALL = std("block.sand.fall");
   public static final XSound BLOCK_SAND_HIT = std("block.sand.hit");
   public static final XSound BLOCK_SAND_PLACE = std("block.sand.place");
   public static final XSound BLOCK_SCAFFOLDING_BREAK = std("block.scaffolding.break");
   public static final XSound BLOCK_SCAFFOLDING_FALL = std("block.scaffolding.fall");
   public static final XSound BLOCK_SCAFFOLDING_HIT = std("block.scaffolding.hit");
   public static final XSound BLOCK_SCAFFOLDING_PLACE = std("block.scaffolding.place");
   public static final XSound BLOCK_SCAFFOLDING_STEP = std("block.scaffolding.step");
   public static final XSound BLOCK_SCULK_BREAK = std("block.sculk.break");
   public static final XSound BLOCK_SCULK_CATALYST_BLOOM = std("block.sculk_catalyst.bloom");
   public static final XSound BLOCK_SCULK_CATALYST_BREAK = std("block.sculk_catalyst.break");
   public static final XSound BLOCK_SCULK_CATALYST_FALL = std("block.sculk_catalyst.fall");
   public static final XSound BLOCK_SCULK_CATALYST_HIT = std("block.sculk_catalyst.hit");
   public static final XSound BLOCK_SCULK_CATALYST_PLACE = std("block.sculk_catalyst.place");
   public static final XSound BLOCK_SCULK_CATALYST_STEP = std("block.sculk_catalyst.step");
   public static final XSound BLOCK_SCULK_CHARGE = std("block.sculk.charge");
   public static final XSound BLOCK_SCULK_FALL = std("block.sculk.fall");
   public static final XSound BLOCK_SCULK_HIT = std("block.sculk.hit");
   public static final XSound BLOCK_SCULK_PLACE = std("block.sculk.place");
   public static final XSound BLOCK_SCULK_SENSOR_BREAK = std("block.sculk_sensor.break");
   public static final XSound BLOCK_SCULK_SENSOR_CLICKING = std("block.sculk_sensor.clicking");
   public static final XSound BLOCK_SCULK_SENSOR_CLICKING_STOP = std("block.sculk_sensor.clicking_stop");
   public static final XSound BLOCK_SCULK_SENSOR_FALL = std("block.sculk_sensor.fall");
   public static final XSound BLOCK_SCULK_SENSOR_HIT = std("block.sculk_sensor.hit");
   public static final XSound BLOCK_SCULK_SENSOR_PLACE = std("block.sculk_sensor.place");
   public static final XSound BLOCK_SCULK_SENSOR_STEP = std("block.sculk_sensor.step");
   public static final XSound BLOCK_SCULK_SHRIEKER_BREAK = std("block.sculk_shrieker.break");
   public static final XSound BLOCK_SCULK_SHRIEKER_FALL = std("block.sculk_shrieker.fall");
   public static final XSound BLOCK_SCULK_SHRIEKER_HIT = std("block.sculk_shrieker.hit");
   public static final XSound BLOCK_SCULK_SHRIEKER_PLACE = std("block.sculk_shrieker.place");
   public static final XSound BLOCK_SCULK_SHRIEKER_SHRIEK = std("block.sculk_shrieker.shriek");
   public static final XSound BLOCK_SCULK_SHRIEKER_STEP = std("block.sculk_shrieker.step");
   public static final XSound BLOCK_SCULK_SPREAD = std("block.sculk.spread");
   public static final XSound BLOCK_SCULK_STEP = std("block.sculk.step");
   public static final XSound BLOCK_SCULK_VEIN_BREAK = std("block.sculk_vein.break");
   public static final XSound BLOCK_SCULK_VEIN_FALL = std("block.sculk_vein.fall");
   public static final XSound BLOCK_SCULK_VEIN_HIT = std("block.sculk_vein.hit");
   public static final XSound BLOCK_SCULK_VEIN_PLACE = std("block.sculk_vein.place");
   public static final XSound BLOCK_SCULK_VEIN_STEP = std("block.sculk_vein.step");
   public static final XSound BLOCK_SHROOMLIGHT_BREAK = std("block.shroomlight.break");
   public static final XSound BLOCK_SHROOMLIGHT_FALL = std("block.shroomlight.fall");
   public static final XSound BLOCK_SHROOMLIGHT_HIT = std("block.shroomlight.hit");
   public static final XSound BLOCK_SHROOMLIGHT_PLACE = std("block.shroomlight.place");
   public static final XSound BLOCK_SHROOMLIGHT_STEP = std("block.shroomlight.step");
   public static final XSound BLOCK_SHULKER_BOX_CLOSE = std("block.shulker_box.close");
   public static final XSound BLOCK_SHULKER_BOX_OPEN = std("block.shulker_box.open");
   public static final XSound BLOCK_SIGN_WAXED_INTERACT_FAIL = std("block.sign.waxed_interact_fail");
   public static final XSound BLOCK_SMALL_AMETHYST_BUD_BREAK = std("block.small_amethyst_bud.break");
   public static final XSound BLOCK_SMALL_AMETHYST_BUD_PLACE = std("block.small_amethyst_bud.place");
   public static final XSound BLOCK_SMALL_DRIPLEAF_BREAK = std("block.small_dripleaf.break");
   public static final XSound BLOCK_SMALL_DRIPLEAF_FALL = std("block.small_dripleaf.fall");
   public static final XSound BLOCK_SMALL_DRIPLEAF_HIT = std("block.small_dripleaf.hit");
   public static final XSound BLOCK_SMALL_DRIPLEAF_PLACE = std("block.small_dripleaf.place");
   public static final XSound BLOCK_SMALL_DRIPLEAF_STEP = std("block.small_dripleaf.step");
   public static final XSound BLOCK_SMITHING_TABLE_USE = std("block.smithing_table.use");
   public static final XSound BLOCK_SMOKER_SMOKE = std("block.smoker.smoke");
   public static final XSound BLOCK_SNIFFER_EGG_CRACK = std("block.sniffer_egg.crack");
   public static final XSound BLOCK_SNIFFER_EGG_HATCH = std("block.sniffer_egg.hatch");
   public static final XSound BLOCK_SNIFFER_EGG_PLOP = std("block.sniffer_egg.plop");
   public static final XSound BLOCK_SNOW_FALL = std("block.snow.fall");
   public static final XSound BLOCK_SNOW_HIT = std("block.snow.hit");
   public static final XSound BLOCK_SNOW_PLACE = std("block.snow.place");
   public static final XSound BLOCK_SOUL_SAND_BREAK = std("block.soul_sand.break");
   public static final XSound BLOCK_SOUL_SAND_FALL = std("block.soul_sand.fall");
   public static final XSound BLOCK_SOUL_SAND_HIT = std("block.soul_sand.hit");
   public static final XSound BLOCK_SOUL_SAND_PLACE = std("block.soul_sand.place");
   public static final XSound BLOCK_SOUL_SAND_STEP = std("block.soul_sand.step");
   public static final XSound BLOCK_SOUL_SOIL_BREAK = std("block.soul_soil.break");
   public static final XSound BLOCK_SOUL_SOIL_FALL = std("block.soul_soil.fall");
   public static final XSound BLOCK_SOUL_SOIL_HIT = std("block.soul_soil.hit");
   public static final XSound BLOCK_SOUL_SOIL_PLACE = std("block.soul_soil.place");
   public static final XSound BLOCK_SOUL_SOIL_STEP = std("block.soul_soil.step");
   public static final XSound BLOCK_SPAWNER_BREAK = std("block.spawner.break");
   public static final XSound BLOCK_SPAWNER_FALL = std("block.spawner.fall");
   public static final XSound BLOCK_SPAWNER_HIT = std("block.spawner.hit");
   public static final XSound BLOCK_SPAWNER_PLACE = std("block.spawner.place");
   public static final XSound BLOCK_SPAWNER_STEP = std("block.spawner.step");
   public static final XSound BLOCK_SPONGE_ABSORB = std("block.sponge.absorb");
   public static final XSound BLOCK_SPONGE_BREAK = std("block.sponge.break");
   public static final XSound BLOCK_SPONGE_FALL = std("block.sponge.fall");
   public static final XSound BLOCK_SPONGE_HIT = std("block.sponge.hit");
   public static final XSound BLOCK_SPONGE_PLACE = std("block.sponge.place");
   public static final XSound BLOCK_SPONGE_STEP = std("block.sponge.step");
   public static final XSound BLOCK_SPORE_BLOSSOM_BREAK = std("block.spore_blossom.break");
   public static final XSound BLOCK_SPORE_BLOSSOM_FALL = std("block.spore_blossom.fall");
   public static final XSound BLOCK_SPORE_BLOSSOM_HIT = std("block.spore_blossom.hit");
   public static final XSound BLOCK_SPORE_BLOSSOM_PLACE = std("block.spore_blossom.place");
   public static final XSound BLOCK_SPORE_BLOSSOM_STEP = std("block.spore_blossom.step");
   public static final XSound BLOCK_STEM_BREAK = std("block.stem.break");
   public static final XSound BLOCK_STEM_FALL = std("block.stem.fall");
   public static final XSound BLOCK_STEM_HIT = std("block.stem.hit");
   public static final XSound BLOCK_STEM_PLACE = std("block.stem.place");
   public static final XSound BLOCK_STEM_STEP = std("block.stem.step");
   public static final XSound BLOCK_STONE_BUTTON_CLICK_OFF = std("block.stone_button.click_off");
   public static final XSound BLOCK_STONE_BUTTON_CLICK_ON = std("block.stone_button.click_on");
   public static final XSound BLOCK_STONE_FALL = std("block.stone.fall");
   public static final XSound BLOCK_STONE_HIT = std("block.stone.hit");
   public static final XSound BLOCK_STONE_PLACE = std("block.stone.place");
   public static final XSound BLOCK_SUSPICIOUS_GRAVEL_BREAK = std("block.suspicious_gravel.break");
   public static final XSound BLOCK_SUSPICIOUS_GRAVEL_FALL = std("block.suspicious_gravel.fall");
   public static final XSound BLOCK_SUSPICIOUS_GRAVEL_HIT = std("block.suspicious_gravel.hit");
   public static final XSound BLOCK_SUSPICIOUS_GRAVEL_PLACE = std("block.suspicious_gravel.place");
   public static final XSound BLOCK_SUSPICIOUS_GRAVEL_STEP = std("block.suspicious_gravel.step");
   public static final XSound BLOCK_SUSPICIOUS_SAND_BREAK = std("block.suspicious_sand.break");
   public static final XSound BLOCK_SUSPICIOUS_SAND_FALL = std("block.suspicious_sand.fall");
   public static final XSound BLOCK_SUSPICIOUS_SAND_HIT = std("block.suspicious_sand.hit");
   public static final XSound BLOCK_SUSPICIOUS_SAND_PLACE = std("block.suspicious_sand.place");
   public static final XSound BLOCK_SUSPICIOUS_SAND_STEP = std("block.suspicious_sand.step");
   public static final XSound BLOCK_SWEET_BERRY_BUSH_BREAK = std("block.sweet_berry_bush.break");
   public static final XSound BLOCK_SWEET_BERRY_BUSH_PLACE = std("block.sweet_berry_bush.place");
   public static final XSound BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM = std("block.trial_spawner.about_to_spawn_item");
   public static final XSound BLOCK_TRIAL_SPAWNER_AMBIENT = std("block.trial_spawner.ambient");
   public static final XSound BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS = std("block.trial_spawner.ambient_ominous");
   public static final XSound BLOCK_TRIAL_SPAWNER_BREAK = std("block.trial_spawner.break");
   public static final XSound BLOCK_TRIAL_SPAWNER_CLOSE_SHUTTER = std("block.trial_spawner.close_shutter");
   public static final XSound BLOCK_TRIAL_SPAWNER_DETECT_PLAYER = std("block.trial_spawner.detect_player");
   public static final XSound BLOCK_TRIAL_SPAWNER_EJECT_ITEM = std("block.trial_spawner.eject_item");
   public static final XSound BLOCK_TRIAL_SPAWNER_FALL = std("block.trial_spawner.fall");
   public static final XSound BLOCK_TRIAL_SPAWNER_HIT = std("block.trial_spawner.hit");
   public static final XSound BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE = std("block.trial_spawner.ominous_activate");
   public static final XSound BLOCK_TRIAL_SPAWNER_OPEN_SHUTTER = std("block.trial_spawner.open_shutter");
   public static final XSound BLOCK_TRIAL_SPAWNER_PLACE = std("block.trial_spawner.place");
   public static final XSound BLOCK_TRIAL_SPAWNER_SPAWN_ITEM = std("block.trial_spawner.spawn_item");
   public static final XSound BLOCK_TRIAL_SPAWNER_SPAWN_ITEM_BEGIN = std("block.trial_spawner.spawn_item_begin");
   public static final XSound BLOCK_TRIAL_SPAWNER_SPAWN_MOB = std("block.trial_spawner.spawn_mob");
   public static final XSound BLOCK_TRIAL_SPAWNER_STEP = std("block.trial_spawner.step");
   public static final XSound BLOCK_TRIPWIRE_ATTACH = std("block.tripwire.attach");
   public static final XSound BLOCK_TRIPWIRE_CLICK_OFF = std("block.tripwire.click_off");
   public static final XSound BLOCK_TRIPWIRE_CLICK_ON = std("block.tripwire.click_on");
   public static final XSound BLOCK_TRIPWIRE_DETACH = std("block.tripwire.detach");
   public static final XSound BLOCK_TUFF_BREAK = std("block.tuff.break");
   public static final XSound BLOCK_TUFF_BRICKS_BREAK = std("block.tuff_bricks.break");
   public static final XSound BLOCK_TUFF_BRICKS_FALL = std("block.tuff_bricks.fall");
   public static final XSound BLOCK_TUFF_BRICKS_HIT = std("block.tuff_bricks.hit");
   public static final XSound BLOCK_TUFF_BRICKS_PLACE = std("block.tuff_bricks.place");
   public static final XSound BLOCK_TUFF_BRICKS_STEP = std("block.tuff_bricks.step");
   public static final XSound BLOCK_TUFF_FALL = std("block.tuff.fall");
   public static final XSound BLOCK_TUFF_HIT = std("block.tuff.hit");
   public static final XSound BLOCK_TUFF_PLACE = std("block.tuff.place");
   public static final XSound BLOCK_TUFF_STEP = std("block.tuff.step");
   public static final XSound BLOCK_VAULT_ACTIVATE = std("block.vault.activate");
   public static final XSound BLOCK_VAULT_AMBIENT = std("block.vault.ambient");
   public static final XSound BLOCK_VAULT_BREAK = std("block.vault.break");
   public static final XSound BLOCK_VAULT_CLOSE_SHUTTER = std("block.vault.close_shutter");
   public static final XSound BLOCK_VAULT_DEACTIVATE = std("block.vault.deactivate");
   public static final XSound BLOCK_VAULT_EJECT_ITEM = std("block.vault.eject_item");
   public static final XSound BLOCK_VAULT_FALL = std("block.vault.fall");
   public static final XSound BLOCK_VAULT_HIT = std("block.vault.hit");
   public static final XSound BLOCK_VAULT_INSERT_ITEM = std("block.vault.insert_item");
   public static final XSound BLOCK_VAULT_INSERT_ITEM_FAIL = std("block.vault.insert_item_fail");
   public static final XSound BLOCK_VAULT_OPEN_SHUTTER = std("block.vault.open_shutter");
   public static final XSound BLOCK_VAULT_PLACE = std("block.vault.place");
   public static final XSound BLOCK_VAULT_REJECT_REWARDED_PLAYER = std("block.vault.reject_rewarded_player");
   public static final XSound BLOCK_VAULT_STEP = std("block.vault.step");
   public static final XSound BLOCK_VINE_BREAK = std("block.vine.break");
   public static final XSound BLOCK_VINE_FALL = std("block.vine.fall");
   public static final XSound BLOCK_VINE_HIT = std("block.vine.hit");
   public static final XSound BLOCK_VINE_PLACE = std("block.vine.place");
   public static final XSound BLOCK_VINE_STEP = std("block.vine.step");
   public static final XSound BLOCK_WART_BLOCK_BREAK = std("block.wart_block.break");
   public static final XSound BLOCK_WART_BLOCK_FALL = std("block.wart_block.fall");
   public static final XSound BLOCK_WART_BLOCK_HIT = std("block.wart_block.hit");
   public static final XSound BLOCK_WART_BLOCK_PLACE = std("block.wart_block.place");
   public static final XSound BLOCK_WART_BLOCK_STEP = std("block.wart_block.step");
   public static final XSound BLOCK_WEEPING_VINES_BREAK = std("block.weeping_vines.break");
   public static final XSound BLOCK_WEEPING_VINES_FALL = std("block.weeping_vines.fall");
   public static final XSound BLOCK_WEEPING_VINES_HIT = std("block.weeping_vines.hit");
   public static final XSound BLOCK_WEEPING_VINES_PLACE = std("block.weeping_vines.place");
   public static final XSound BLOCK_WEEPING_VINES_STEP = std("block.weeping_vines.step");
   public static final XSound BLOCK_WET_GRASS_BREAK = std("block.wet_grass.break");
   public static final XSound BLOCK_WET_GRASS_FALL = std("block.wet_grass.fall");
   public static final XSound BLOCK_WET_GRASS_HIT = std("block.wet_grass.hit");
   public static final XSound BLOCK_WET_SPONGE_BREAK = std("block.wet_sponge.break");
   public static final XSound BLOCK_WET_SPONGE_DRIES = std("block.wet_sponge.dries");
   public static final XSound BLOCK_WET_SPONGE_FALL = std("block.wet_sponge.fall");
   public static final XSound BLOCK_WET_SPONGE_HIT = std("block.wet_sponge.hit");
   public static final XSound BLOCK_WET_SPONGE_PLACE = std("block.wet_sponge.place");
   public static final XSound BLOCK_WET_SPONGE_STEP = std("block.wet_sponge.step");
   public static final XSound BLOCK_WOODEN_TRAPDOOR_CLOSE = std("block.wooden_trapdoor.close");
   public static final XSound BLOCK_WOODEN_TRAPDOOR_OPEN = std("block.wooden_trapdoor.open");
   public static final XSound BLOCK_WOOD_FALL = std("block.wood.fall");
   public static final XSound BLOCK_WOOD_HIT = std("block.wood.hit");
   public static final XSound BLOCK_WOOD_PLACE = std("block.wood.place");
   public static final XSound BLOCK_WOOL_FALL = std("block.wool.fall", "BLOCK_WOOL_FALL", "BLOCK_CLOTH_FALL");
   public static final XSound ENCHANT_THORNS_HIT = std("enchant.thorns.hit");
   public static final XSound ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM = std("entity.allay.ambient_without_item");
   public static final XSound ENTITY_ALLAY_AMBIENT_WITH_ITEM = std("entity.allay.ambient_with_item");
   public static final XSound ENTITY_ALLAY_DEATH = std("entity.allay.death");
   public static final XSound ENTITY_ALLAY_HURT = std("entity.allay.hurt");
   public static final XSound ENTITY_ALLAY_ITEM_GIVEN = std("entity.allay.item_given");
   public static final XSound ENTITY_ALLAY_ITEM_TAKEN = std("entity.allay.item_taken");
   public static final XSound ENTITY_ALLAY_ITEM_THROWN = std("entity.allay.item_thrown");
   public static final XSound ENTITY_ARMADILLO_AMBIENT = std("entity.armadillo.ambient");
   public static final XSound ENTITY_ARMADILLO_BRUSH = std("entity.armadillo.brush");
   public static final XSound ENTITY_ARMADILLO_DEATH = std("entity.armadillo.death");
   public static final XSound ENTITY_ARMADILLO_EAT = std("entity.armadillo.eat");
   public static final XSound ENTITY_ARMADILLO_HURT = std("entity.armadillo.hurt");
   public static final XSound ENTITY_ARMADILLO_HURT_REDUCED = std("entity.armadillo.hurt_reduced");
   public static final XSound ENTITY_ARMADILLO_LAND = std("entity.armadillo.land");
   public static final XSound ENTITY_ARMADILLO_PEEK = std("entity.armadillo.peek");
   public static final XSound ENTITY_ARMADILLO_ROLL = std("entity.armadillo.roll");
   public static final XSound ENTITY_ARMADILLO_SCUTE_DROP = std("entity.armadillo.scute_drop");
   public static final XSound ENTITY_ARMADILLO_STEP = std("entity.armadillo.step");
   public static final XSound ENTITY_ARMADILLO_UNROLL_FINISH = std("entity.armadillo.unroll_finish");
   public static final XSound ENTITY_ARMADILLO_UNROLL_START = std("entity.armadillo.unroll_start");
   public static final XSound ENTITY_AXOLOTL_ATTACK = std("entity.axolotl.attack");
   public static final XSound ENTITY_AXOLOTL_DEATH = std("entity.axolotl.death");
   public static final XSound ENTITY_AXOLOTL_HURT = std("entity.axolotl.hurt");
   public static final XSound ENTITY_AXOLOTL_IDLE_AIR = std("entity.axolotl.idle_air");
   public static final XSound ENTITY_AXOLOTL_IDLE_WATER = std("entity.axolotl.idle_water");
   public static final XSound ENTITY_AXOLOTL_SPLASH = std("entity.axolotl.splash");
   public static final XSound ENTITY_AXOLOTL_SWIM = std("entity.axolotl.swim");
   public static final XSound ENTITY_BEE_DEATH = std("entity.bee.death");
   public static final XSound ENTITY_BEE_HURT = std("entity.bee.hurt");
   public static final XSound ENTITY_BEE_LOOP = std("entity.bee.loop");
   public static final XSound ENTITY_BEE_LOOP_AGGRESSIVE = std("entity.bee.loop_aggressive");
   public static final XSound ENTITY_BEE_POLLINATE = std("entity.bee.pollinate");
   public static final XSound ENTITY_BEE_STING = std("entity.bee.sting");
   public static final XSound ENTITY_BLAZE_BURN = std("entity.blaze.burn");
   public static final XSound ENTITY_BLAZE_SHOOT = std("entity.blaze.shoot");
   public static final XSound ENTITY_BOAT_PADDLE_LAND = std("entity.boat.paddle_land");
   public static final XSound ENTITY_BOAT_PADDLE_WATER = std("entity.boat.paddle_water");
   public static final XSound ENTITY_BOGGED_AMBIENT = std("entity.bogged.ambient");
   public static final XSound ENTITY_BOGGED_DEATH = std("entity.bogged.death");
   public static final XSound ENTITY_BOGGED_HURT = std("entity.bogged.hurt");
   public static final XSound ENTITY_BOGGED_SHEAR = std("entity.bogged.shear");
   public static final XSound ENTITY_BOGGED_STEP = std("entity.bogged.step");
   public static final XSound ENTITY_BREEZE_CHARGE = std("entity.breeze.charge");
   public static final XSound ENTITY_BREEZE_DEATH = std("entity.breeze.death");
   public static final XSound ENTITY_BREEZE_DEFLECT = std("entity.breeze.deflect");
   public static final XSound ENTITY_BREEZE_HURT = std("entity.breeze.hurt");
   public static final XSound ENTITY_BREEZE_IDLE_AIR = std("entity.breeze.idle_air");
   public static final XSound ENTITY_BREEZE_IDLE_GROUND = std("entity.breeze.idle_ground");
   public static final XSound ENTITY_BREEZE_INHALE = std("entity.breeze.inhale");
   public static final XSound ENTITY_BREEZE_JUMP = std("entity.breeze.jump");
   public static final XSound ENTITY_BREEZE_LAND = std("entity.breeze.land");
   public static final XSound ENTITY_BREEZE_SHOOT = std("entity.breeze.shoot");
   public static final XSound ENTITY_BREEZE_SLIDE = std("entity.breeze.slide");
   public static final XSound ENTITY_BREEZE_WHIRL = std("entity.breeze.whirl");
   public static final XSound ENTITY_BREEZE_WIND_BURST = std("entity.breeze.wind_burst");
   public static final XSound ENTITY_CAMEL_AMBIENT = std("entity.camel.ambient");
   public static final XSound ENTITY_CAMEL_DASH = std("entity.camel.dash");
   public static final XSound ENTITY_CAMEL_DASH_READY = std("entity.camel.dash_ready");
   public static final XSound ENTITY_CAMEL_DEATH = std("entity.camel.death");
   public static final XSound ENTITY_CAMEL_EAT = std("entity.camel.eat");
   public static final XSound ENTITY_CAMEL_HURT = std("entity.camel.hurt");
   public static final XSound ENTITY_CAMEL_SADDLE = std("entity.camel.saddle");
   public static final XSound ENTITY_CAMEL_SIT = std("entity.camel.sit");
   public static final XSound ENTITY_CAMEL_STAND = std("entity.camel.stand");
   public static final XSound ENTITY_CAMEL_STEP = std("entity.camel.step");
   public static final XSound ENTITY_CAMEL_STEP_SAND = std("entity.camel.step_sand");
   public static final XSound ENTITY_CAT_BEG_FOR_FOOD = std("entity.cat.beg_for_food");
   public static final XSound ENTITY_CAT_DEATH = std("entity.cat.death");
   public static final XSound ENTITY_CAT_STRAY_AMBIENT = std("entity.cat.stray_ambient");
   public static final XSound ENTITY_CHICKEN_DEATH = std("entity.chicken.death");
   public static final XSound ENTITY_COD_AMBIENT = std("entity.cod.ambient");
   public static final XSound ENTITY_COD_DEATH = std("entity.cod.death");
   public static final XSound ENTITY_COD_FLOP = std("entity.cod.flop");
   public static final XSound ENTITY_COD_HURT = std("entity.cod.hurt");
   public static final XSound ENTITY_COW_DEATH = std("entity.cow.death");
   public static final XSound ENTITY_COW_MILK = std("entity.cow.milk");
   public static final XSound ENTITY_CREAKING_ACTIVATE = std("entity.creaking.activate");
   public static final XSound ENTITY_CREAKING_AMBIENT = std("entity.creaking.ambient");
   public static final XSound ENTITY_CREAKING_ATTACK = std("entity.creaking.attack");
   public static final XSound ENTITY_CREAKING_DEACTIVATE = std("entity.creaking.deactivate");
   public static final XSound ENTITY_CREAKING_DEATH = std("entity.creaking.death");
   public static final XSound ENTITY_CREAKING_FREEZE = std("entity.creaking.freeze");
   public static final XSound ENTITY_CREAKING_SPAWN = std("entity.creaking.spawn");
   public static final XSound ENTITY_CREAKING_STEP = std("entity.creaking.step");
   public static final XSound ENTITY_CREAKING_SWAY = std("entity.creaking.sway");
   public static final XSound ENTITY_CREAKING_UNFREEZE = std("entity.creaking.unfreeze");
   public static final XSound ENTITY_CREEPER_HURT = std("entity.creeper.hurt");
   public static final XSound ENTITY_DOLPHIN_AMBIENT = std("entity.dolphin.ambient");
   public static final XSound ENTITY_DOLPHIN_AMBIENT_WATER = std("entity.dolphin.ambient_water");
   public static final XSound ENTITY_DOLPHIN_ATTACK = std("entity.dolphin.attack");
   public static final XSound ENTITY_DOLPHIN_DEATH = std("entity.dolphin.death");
   public static final XSound ENTITY_DOLPHIN_EAT = std("entity.dolphin.eat");
   public static final XSound ENTITY_DOLPHIN_HURT = std("entity.dolphin.hurt");
   public static final XSound ENTITY_DOLPHIN_JUMP = std("entity.dolphin.jump");
   public static final XSound ENTITY_DOLPHIN_PLAY = std("entity.dolphin.play");
   public static final XSound ENTITY_DOLPHIN_SPLASH = std("entity.dolphin.splash");
   public static final XSound ENTITY_DOLPHIN_SWIM = std("entity.dolphin.swim");
   public static final XSound ENTITY_DONKEY_CHEST = std("entity.donkey.chest");
   public static final XSound ENTITY_DONKEY_EAT = std("entity.donkey.eat");
   public static final XSound ENTITY_DONKEY_JUMP = std("entity.donkey.jump");
   public static final XSound ENTITY_DROWNED_AMBIENT = std("entity.drowned.ambient");
   public static final XSound ENTITY_DROWNED_AMBIENT_WATER = std("entity.drowned.ambient_water");
   public static final XSound ENTITY_DROWNED_DEATH = std("entity.drowned.death");
   public static final XSound ENTITY_DROWNED_DEATH_WATER = std("entity.drowned.death_water");
   public static final XSound ENTITY_DROWNED_HURT = std("entity.drowned.hurt");
   public static final XSound ENTITY_DROWNED_HURT_WATER = std("entity.drowned.hurt_water");
   public static final XSound ENTITY_DROWNED_SHOOT = std("entity.drowned.shoot");
   public static final XSound ENTITY_DROWNED_STEP = std("entity.drowned.step");
   public static final XSound ENTITY_DROWNED_SWIM = std("entity.drowned.swim");
   public static final XSound ENTITY_EGG_THROW = std("entity.egg.throw");
   public static final XSound ENTITY_ELDER_GUARDIAN_AMBIENT = std("entity.elder_guardian.ambient");
   public static final XSound ENTITY_ELDER_GUARDIAN_AMBIENT_LAND = std("entity.elder_guardian.ambient_land");
   public static final XSound ENTITY_ELDER_GUARDIAN_CURSE = std("entity.elder_guardian.curse");
   public static final XSound ENTITY_ELDER_GUARDIAN_DEATH = std("entity.elder_guardian.death");
   public static final XSound ENTITY_ELDER_GUARDIAN_DEATH_LAND = std("entity.elder_guardian.death_land");
   public static final XSound ENTITY_ELDER_GUARDIAN_FLOP = std("entity.elder_guardian.flop");
   public static final XSound ENTITY_ELDER_GUARDIAN_HURT = std("entity.elder_guardian.hurt");
   public static final XSound ENTITY_ELDER_GUARDIAN_HURT_LAND = std("entity.elder_guardian.hurt_land");
   public static final XSound ENTITY_ENDERMITE_AMBIENT = std("entity.endermite.ambient");
   public static final XSound ENTITY_ENDERMITE_DEATH = std("entity.endermite.death");
   public static final XSound ENTITY_ENDERMITE_HURT = std("entity.endermite.hurt");
   public static final XSound ENTITY_ENDERMITE_STEP = std("entity.endermite.step");
   public static final XSound ENTITY_ENDER_EYE_DEATH = std("entity.ender_eye.death", "ENTITY_ENDER_EYE_DEATH", "ENTITY_ENDEREYE_DEATH");
   public static final XSound ENTITY_EVOKER_CELEBRATE = std("entity.evoker.celebrate");
   public static final XSound ENTITY_EXPERIENCE_BOTTLE_THROW = std("entity.experience_bottle.throw");
   public static final XSound ENTITY_FIREWORK_ROCKET_SHOOT = std("entity.firework_rocket.shoot", "ENTITY_FIREWORK_SHOOT");
   public static final XSound ENTITY_FISHING_BOBBER_RETRIEVE = std("entity.fishing_bobber.retrieve", "ENTITY_BOBBER_RETRIEVE");
   public static final XSound ENTITY_FISH_SWIM = std("entity.fish.swim");
   public static final XSound ENTITY_FOX_AGGRO = std("entity.fox.aggro");
   public static final XSound ENTITY_FOX_AMBIENT = std("entity.fox.ambient");
   public static final XSound ENTITY_FOX_BITE = std("entity.fox.bite");
   public static final XSound ENTITY_FOX_DEATH = std("entity.fox.death");
   public static final XSound ENTITY_FOX_EAT = std("entity.fox.eat");
   public static final XSound ENTITY_FOX_HURT = std("entity.fox.hurt");
   public static final XSound ENTITY_FOX_SCREECH = std("entity.fox.screech");
   public static final XSound ENTITY_FOX_SLEEP = std("entity.fox.sleep");
   public static final XSound ENTITY_FOX_SNIFF = std("entity.fox.sniff");
   public static final XSound ENTITY_FOX_SPIT = std("entity.fox.spit");
   public static final XSound ENTITY_FOX_TELEPORT = std("entity.fox.teleport");
   public static final XSound ENTITY_FROG_AMBIENT = std("entity.frog.ambient");
   public static final XSound ENTITY_FROG_DEATH = std("entity.frog.death");
   public static final XSound ENTITY_FROG_EAT = std("entity.frog.eat");
   public static final XSound ENTITY_FROG_HURT = std("entity.frog.hurt");
   public static final XSound ENTITY_FROG_LAY_SPAWN = std("entity.frog.lay_spawn");
   public static final XSound ENTITY_FROG_LONG_JUMP = std("entity.frog.long_jump");
   public static final XSound ENTITY_FROG_STEP = std("entity.frog.step");
   public static final XSound ENTITY_FROG_TONGUE = std("entity.frog.tongue");
   public static final XSound ENTITY_GENERIC_BURN = std("entity.generic.burn");
   public static final XSound ENTITY_GENERIC_DEATH = std("entity.generic.death");
   public static final XSound ENTITY_GENERIC_EXTINGUISH_FIRE = std("entity.generic.extinguish_fire");
   public static final XSound ENTITY_GENERIC_HURT = std("entity.generic.hurt");
   public static final XSound ENTITY_GLOW_ITEM_FRAME_ADD_ITEM = std("entity.glow_item_frame.add_item");
   public static final XSound ENTITY_GLOW_ITEM_FRAME_BREAK = std("entity.glow_item_frame.break");
   public static final XSound ENTITY_GLOW_ITEM_FRAME_PLACE = std("entity.glow_item_frame.place");
   public static final XSound ENTITY_GLOW_ITEM_FRAME_REMOVE_ITEM = std("entity.glow_item_frame.remove_item");
   public static final XSound ENTITY_GLOW_ITEM_FRAME_ROTATE_ITEM = std("entity.glow_item_frame.rotate_item");
   public static final XSound ENTITY_GLOW_SQUID_AMBIENT = std("entity.glow_squid.ambient");
   public static final XSound ENTITY_GLOW_SQUID_DEATH = std("entity.glow_squid.death");
   public static final XSound ENTITY_GLOW_SQUID_HURT = std("entity.glow_squid.hurt");
   public static final XSound ENTITY_GLOW_SQUID_SQUIRT = std("entity.glow_squid.squirt");
   public static final XSound ENTITY_GOAT_AMBIENT = std("entity.goat.ambient");
   public static final XSound ENTITY_GOAT_DEATH = std("entity.goat.death");
   public static final XSound ENTITY_GOAT_EAT = std("entity.goat.eat");
   public static final XSound ENTITY_GOAT_HORN_BREAK = std("entity.goat.horn_break");
   public static final XSound ENTITY_GOAT_HURT = std("entity.goat.hurt");
   public static final XSound ENTITY_GOAT_LONG_JUMP = std("entity.goat.long_jump");
   public static final XSound ENTITY_GOAT_MILK = std("entity.goat.milk");
   public static final XSound ENTITY_GOAT_PREPARE_RAM = std("entity.goat.prepare_ram");
   public static final XSound ENTITY_GOAT_RAM_IMPACT = std("entity.goat.ram_impact");
   public static final XSound ENTITY_GOAT_SCREAMING_AMBIENT = std("entity.goat.screaming.ambient");
   public static final XSound ENTITY_GOAT_SCREAMING_DEATH = std("entity.goat.screaming.death");
   public static final XSound ENTITY_GOAT_SCREAMING_EAT = std("entity.goat.screaming.eat");
   public static final XSound ENTITY_GOAT_SCREAMING_HURT = std("entity.goat.screaming.hurt");
   public static final XSound ENTITY_GOAT_SCREAMING_LONG_JUMP = std("entity.goat.screaming.long_jump");
   public static final XSound ENTITY_GOAT_SCREAMING_MILK = std("entity.goat.screaming.milk");
   public static final XSound ENTITY_GOAT_SCREAMING_PREPARE_RAM = std("entity.goat.screaming.prepare_ram");
   public static final XSound ENTITY_GOAT_SCREAMING_RAM_IMPACT = std("entity.goat.screaming.ram_impact");
   public static final XSound ENTITY_GOAT_STEP = std("entity.goat.step");
   public static final XSound ENTITY_GUARDIAN_AMBIENT = std("entity.guardian.ambient");
   public static final XSound ENTITY_GUARDIAN_AMBIENT_LAND = std("entity.guardian.ambient_land");
   public static final XSound ENTITY_GUARDIAN_ATTACK = std("entity.guardian.attack");
   public static final XSound ENTITY_GUARDIAN_DEATH = std("entity.guardian.death");
   public static final XSound ENTITY_GUARDIAN_DEATH_LAND = std("entity.guardian.death_land");
   public static final XSound ENTITY_GUARDIAN_FLOP = std("entity.guardian.flop");
   public static final XSound ENTITY_GUARDIAN_HURT = std("entity.guardian.hurt");
   public static final XSound ENTITY_GUARDIAN_HURT_LAND = std("entity.guardian.hurt_land");
   public static final XSound ENTITY_HOGLIN_AMBIENT = std("entity.hoglin.ambient");
   public static final XSound ENTITY_HOGLIN_ANGRY = std("entity.hoglin.angry");
   public static final XSound ENTITY_HOGLIN_ATTACK = std("entity.hoglin.attack");
   public static final XSound ENTITY_HOGLIN_CONVERTED_TO_ZOMBIFIED = std("entity.hoglin.converted_to_zombified");
   public static final XSound ENTITY_HOGLIN_DEATH = std("entity.hoglin.death");
   public static final XSound ENTITY_HOGLIN_HURT = std("entity.hoglin.hurt");
   public static final XSound ENTITY_HOGLIN_RETREAT = std("entity.hoglin.retreat");
   public static final XSound ENTITY_HOGLIN_STEP = std("entity.hoglin.step");
   public static final XSound ENTITY_HOSTILE_DEATH = std("entity.hostile.death");
   public static final XSound ENTITY_HOSTILE_HURT = std("entity.hostile.hurt");
   public static final XSound ENTITY_HUSK_AMBIENT = std("entity.husk.ambient");
   public static final XSound ENTITY_HUSK_CONVERTED_TO_ZOMBIE = std("entity.husk.converted_to_zombie");
   public static final XSound ENTITY_HUSK_DEATH = std("entity.husk.death");
   public static final XSound ENTITY_HUSK_HURT = std("entity.husk.hurt");
   public static final XSound ENTITY_HUSK_STEP = std("entity.husk.step");
   public static final XSound ENTITY_IRON_GOLEM_DAMAGE = std("entity.iron_golem.damage");
   public static final XSound ENTITY_IRON_GOLEM_REPAIR = std("entity.iron_golem.repair");
   public static final XSound ENTITY_LLAMA_AMBIENT = std("entity.llama.ambient");
   public static final XSound ENTITY_LLAMA_ANGRY = std("entity.llama.angry");
   public static final XSound ENTITY_LLAMA_CHEST = std("entity.llama.chest");
   public static final XSound ENTITY_LLAMA_DEATH = std("entity.llama.death");
   public static final XSound ENTITY_LLAMA_EAT = std("entity.llama.eat");
   public static final XSound ENTITY_LLAMA_HURT = std("entity.llama.hurt");
   public static final XSound ENTITY_LLAMA_SPIT = std("entity.llama.spit");
   public static final XSound ENTITY_LLAMA_STEP = std("entity.llama.step");
   public static final XSound ENTITY_LLAMA_SWAG = std("entity.llama.swag");
   public static final XSound ENTITY_MINECART_INSIDE_UNDERWATER = std("entity.minecart.inside.underwater");
   public static final XSound ENTITY_MOOSHROOM_CONVERT = std("entity.mooshroom.convert");
   public static final XSound ENTITY_MOOSHROOM_EAT = std("entity.mooshroom.eat");
   public static final XSound ENTITY_MOOSHROOM_MILK = std("entity.mooshroom.milk");
   public static final XSound ENTITY_MOOSHROOM_SHEAR = std("entity.mooshroom.shear");
   public static final XSound ENTITY_MOOSHROOM_SUSPICIOUS_MILK = std("entity.mooshroom.suspicious_milk");
   public static final XSound ENTITY_MULE_AMBIENT = std("entity.mule.ambient");
   public static final XSound ENTITY_MULE_ANGRY = std("entity.mule.angry");
   public static final XSound ENTITY_MULE_EAT = std("entity.mule.eat");
   public static final XSound ENTITY_MULE_JUMP = std("entity.mule.jump");
   public static final XSound ENTITY_OCELOT_AMBIENT = std("entity.ocelot.ambient");
   public static final XSound ENTITY_OCELOT_DEATH = std("entity.ocelot.death");
   public static final XSound ENTITY_OCELOT_HURT = std("entity.ocelot.hurt");
   public static final XSound ENTITY_PAINTING_BREAK = std("entity.painting.break");
   public static final XSound ENTITY_PAINTING_PLACE = std("entity.painting.place");
   public static final XSound ENTITY_PANDA_AGGRESSIVE_AMBIENT = std("entity.panda.aggressive_ambient");
   public static final XSound ENTITY_PANDA_AMBIENT = std("entity.panda.ambient");
   public static final XSound ENTITY_PANDA_BITE = std("entity.panda.bite");
   public static final XSound ENTITY_PANDA_CANT_BREED = std("entity.panda.cant_breed");
   public static final XSound ENTITY_PANDA_DEATH = std("entity.panda.death");
   public static final XSound ENTITY_PANDA_EAT = std("entity.panda.eat");
   public static final XSound ENTITY_PANDA_HURT = std("entity.panda.hurt");
   public static final XSound ENTITY_PANDA_PRE_SNEEZE = std("entity.panda.pre_sneeze");
   public static final XSound ENTITY_PANDA_SNEEZE = std("entity.panda.sneeze");
   public static final XSound ENTITY_PANDA_STEP = std("entity.panda.step");
   public static final XSound ENTITY_PANDA_WORRIED_AMBIENT = std("entity.panda.worried_ambient");
   public static final XSound ENTITY_PARROT_AMBIENT = std("entity.parrot.ambient");
   public static final XSound ENTITY_PARROT_DEATH = std("entity.parrot.death");
   public static final XSound ENTITY_PARROT_EAT = std("entity.parrot.eat");
   public static final XSound ENTITY_PARROT_FLY = std("entity.parrot.fly");
   public static final XSound ENTITY_PARROT_HURT = std("entity.parrot.hurt");
   public static final XSound ENTITY_PARROT_IMITATE_BLAZE = std("entity.parrot.imitate.blaze");
   public static final XSound ENTITY_PARROT_IMITATE_BOGGED = std("entity.parrot.imitate.bogged");
   public static final XSound ENTITY_PARROT_IMITATE_BREEZE = std("entity.parrot.imitate.breeze");
   public static final XSound ENTITY_PARROT_IMITATE_CREAKING = std("entity.parrot.imitate.creaking");
   public static final XSound ENTITY_PARROT_IMITATE_CREEPER = std("entity.parrot.imitate.creeper");
   public static final XSound ENTITY_PARROT_IMITATE_DROWNED = std("entity.parrot.imitate.drowned");
   public static final XSound ENTITY_PARROT_IMITATE_ELDER_GUARDIAN = std("entity.parrot.imitate.elder_guardian");
   public static final XSound ENTITY_PARROT_IMITATE_ENDERMITE = std("entity.parrot.imitate.endermite");
   public static final XSound ENTITY_PARROT_IMITATE_ENDER_DRAGON = std("entity.parrot.imitate.ender_dragon", "ENTITY_PARROT_IMITATE_ENDERDRAGON");
   public static final XSound ENTITY_PARROT_IMITATE_EVOKER = std("entity.parrot.imitate.evoker", "ENTITY_PARROT_IMITATE_EVOCATION_ILLAGER");
   public static final XSound ENTITY_PARROT_IMITATE_GHAST = std("entity.parrot.imitate.ghast");
   public static final XSound ENTITY_PARROT_IMITATE_GUARDIAN = std("entity.parrot.imitate.guardian");
   public static final XSound ENTITY_PARROT_IMITATE_HOGLIN = std("entity.parrot.imitate.hoglin");
   public static final XSound ENTITY_PARROT_IMITATE_HUSK = std("entity.parrot.imitate.husk");
   public static final XSound ENTITY_PARROT_IMITATE_ILLUSIONER = std("entity.parrot.imitate.illusioner", "ENTITY_PARROT_IMITATE_ILLUSION_ILLAGER");
   public static final XSound ENTITY_PARROT_IMITATE_MAGMA_CUBE = std("entity.parrot.imitate.magma_cube", "ENTITY_PARROT_IMITATE_MAGMACUBE");
   public static final XSound ENTITY_PARROT_IMITATE_PHANTOM = std("entity.parrot.imitate.phantom");
   public static final XSound ENTITY_PARROT_IMITATE_PIGLIN = std("entity.parrot.imitate.piglin", "ENTITY_PARROT_IMITATE_ZOMBIE_PIGMAN");
   public static final XSound ENTITY_PARROT_IMITATE_PIGLIN_BRUTE = std("entity.parrot.imitate.piglin_brute");
   public static final XSound ENTITY_PARROT_IMITATE_PILLAGER = std("entity.parrot.imitate.pillager");
   public static final XSound ENTITY_PARROT_IMITATE_RAVAGER = std("entity.parrot.imitate.ravager");
   public static final XSound ENTITY_PARROT_IMITATE_SHULKER = std("entity.parrot.imitate.shulker");
   public static final XSound ENTITY_PARROT_IMITATE_SILVERFISH = std("entity.parrot.imitate.silverfish");
   public static final XSound ENTITY_PARROT_IMITATE_SKELETON = std("entity.parrot.imitate.skeleton");
   public static final XSound ENTITY_PARROT_IMITATE_SLIME = std("entity.parrot.imitate.slime");
   public static final XSound ENTITY_PARROT_IMITATE_SPIDER = std("entity.parrot.imitate.spider");
   public static final XSound ENTITY_PARROT_IMITATE_STRAY = std("entity.parrot.imitate.stray");
   public static final XSound ENTITY_PARROT_IMITATE_VEX = std("entity.parrot.imitate.vex");
   public static final XSound ENTITY_PARROT_IMITATE_VINDICATOR = std("entity.parrot.imitate.vindicator", "ENTITY_PARROT_IMITATE_VINDICATION_ILLAGER");
   public static final XSound ENTITY_PARROT_IMITATE_WARDEN = std("entity.parrot.imitate.warden");
   public static final XSound ENTITY_PARROT_IMITATE_WITCH = std("entity.parrot.imitate.witch");
   public static final XSound ENTITY_PARROT_IMITATE_WITHER = std("entity.parrot.imitate.wither");
   public static final XSound ENTITY_PARROT_IMITATE_WITHER_SKELETON = std("entity.parrot.imitate.wither_skeleton");
   public static final XSound ENTITY_PARROT_IMITATE_ZOGLIN = std("entity.parrot.imitate.zoglin");
   public static final XSound ENTITY_PARROT_IMITATE_ZOMBIE = std("entity.parrot.imitate.zombie");
   public static final XSound ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER = std("entity.parrot.imitate.zombie_villager");
   public static final XSound ENTITY_PARROT_STEP = std("entity.parrot.step");
   public static final XSound ENTITY_PHANTOM_AMBIENT = std("entity.phantom.ambient");
   public static final XSound ENTITY_PHANTOM_BITE = std("entity.phantom.bite");
   public static final XSound ENTITY_PHANTOM_DEATH = std("entity.phantom.death");
   public static final XSound ENTITY_PHANTOM_FLAP = std("entity.phantom.flap");
   public static final XSound ENTITY_PHANTOM_HURT = std("entity.phantom.hurt");
   public static final XSound ENTITY_PHANTOM_SWOOP = std("entity.phantom.swoop");
   public static final XSound ENTITY_PIGLIN_ADMIRING_ITEM = std("entity.piglin.admiring_item");
   public static final XSound ENTITY_PIGLIN_AMBIENT = std("entity.piglin.ambient");
   public static final XSound ENTITY_PIGLIN_ANGRY = std("entity.piglin.angry");
   public static final XSound ENTITY_PIGLIN_BRUTE_AMBIENT = std("entity.piglin_brute.ambient");
   public static final XSound ENTITY_PIGLIN_BRUTE_ANGRY = std("entity.piglin_brute.angry");
   public static final XSound ENTITY_PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED = std("entity.piglin_brute.converted_to_zombified");
   public static final XSound ENTITY_PIGLIN_BRUTE_DEATH = std("entity.piglin_brute.death");
   public static final XSound ENTITY_PIGLIN_BRUTE_HURT = std("entity.piglin_brute.hurt");
   public static final XSound ENTITY_PIGLIN_BRUTE_STEP = std("entity.piglin_brute.step");
   public static final XSound ENTITY_PIGLIN_CELEBRATE = std("entity.piglin.celebrate");
   public static final XSound ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED = std("entity.piglin.converted_to_zombified");
   public static final XSound ENTITY_PIGLIN_DEATH = std("entity.piglin.death");
   public static final XSound ENTITY_PIGLIN_HURT = std("entity.piglin.hurt");
   public static final XSound ENTITY_PIGLIN_JEALOUS = std("entity.piglin.jealous");
   public static final XSound ENTITY_PIGLIN_RETREAT = std("entity.piglin.retreat");
   public static final XSound ENTITY_PIGLIN_STEP = std("entity.piglin.step");
   public static final XSound ENTITY_PIG_HURT = std("entity.pig.hurt");
   public static final XSound ENTITY_PILLAGER_AMBIENT = std("entity.pillager.ambient");
   public static final XSound ENTITY_PILLAGER_CELEBRATE = std("entity.pillager.celebrate");
   public static final XSound ENTITY_PILLAGER_DEATH = std("entity.pillager.death");
   public static final XSound ENTITY_PILLAGER_HURT = std("entity.pillager.hurt");
   public static final XSound ENTITY_PLAYER_ATTACK_CRIT = std("entity.player.attack.crit");
   public static final XSound ENTITY_PLAYER_ATTACK_KNOCKBACK = std("entity.player.attack.knockback");
   public static final XSound ENTITY_PLAYER_ATTACK_NODAMAGE = std("entity.player.attack.nodamage");
   public static final XSound ENTITY_PLAYER_ATTACK_SWEEP = std("entity.player.attack.sweep");
   public static final XSound ENTITY_PLAYER_ATTACK_WEAK = std("entity.player.attack.weak");
   public static final XSound ENTITY_PLAYER_BREATH = std("entity.player.breath");
   public static final XSound ENTITY_PLAYER_DEATH = std("entity.player.death");
   public static final XSound ENTITY_PLAYER_HURT_DROWN = std("entity.player.hurt_drown");
   public static final XSound ENTITY_PLAYER_HURT_FREEZE = std("entity.player.hurt_freeze");
   public static final XSound ENTITY_PLAYER_HURT_ON_FIRE = std("entity.player.hurt_on_fire");
   public static final XSound ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH = std("entity.player.hurt_sweet_berry_bush");
   public static final XSound ENTITY_PLAYER_TELEPORT = std("entity.player.teleport");
   public static final XSound ENTITY_POLAR_BEAR_AMBIENT = std("entity.polar_bear.ambient");
   public static final XSound ENTITY_POLAR_BEAR_DEATH = std("entity.polar_bear.death");
   public static final XSound ENTITY_POLAR_BEAR_HURT = std("entity.polar_bear.hurt");
   public static final XSound ENTITY_POLAR_BEAR_STEP = std("entity.polar_bear.step");
   public static final XSound ENTITY_POLAR_BEAR_WARNING = std("entity.polar_bear.warning");
   public static final XSound ENTITY_PUFFER_FISH_AMBIENT = std("entity.puffer_fish.ambient");
   public static final XSound ENTITY_PUFFER_FISH_BLOW_OUT = std("entity.puffer_fish.blow_out");
   public static final XSound ENTITY_PUFFER_FISH_BLOW_UP = std("entity.puffer_fish.blow_up");
   public static final XSound ENTITY_PUFFER_FISH_DEATH = std("entity.puffer_fish.death");
   public static final XSound ENTITY_PUFFER_FISH_FLOP = std("entity.puffer_fish.flop");
   public static final XSound ENTITY_PUFFER_FISH_HURT = std("entity.puffer_fish.hurt");
   public static final XSound ENTITY_PUFFER_FISH_STING = std("entity.puffer_fish.sting");
   public static final XSound ENTITY_RABBIT_AMBIENT = std("entity.rabbit.ambient");
   public static final XSound ENTITY_RABBIT_ATTACK = std("entity.rabbit.attack");
   public static final XSound ENTITY_RABBIT_DEATH = std("entity.rabbit.death");
   public static final XSound ENTITY_RABBIT_HURT = std("entity.rabbit.hurt");
   public static final XSound ENTITY_RABBIT_JUMP = std("entity.rabbit.jump");
   public static final XSound ENTITY_RAVAGER_AMBIENT = std("entity.ravager.ambient");
   public static final XSound ENTITY_RAVAGER_ATTACK = std("entity.ravager.attack");
   public static final XSound ENTITY_RAVAGER_CELEBRATE = std("entity.ravager.celebrate");
   public static final XSound ENTITY_RAVAGER_DEATH = std("entity.ravager.death");
   public static final XSound ENTITY_RAVAGER_HURT = std("entity.ravager.hurt");
   public static final XSound ENTITY_RAVAGER_ROAR = std("entity.ravager.roar");
   public static final XSound ENTITY_RAVAGER_STEP = std("entity.ravager.step");
   public static final XSound ENTITY_RAVAGER_STUNNED = std("entity.ravager.stunned");
   public static final XSound ENTITY_SALMON_AMBIENT = std("entity.salmon.ambient");
   public static final XSound ENTITY_SALMON_DEATH = std("entity.salmon.death");
   public static final XSound ENTITY_SALMON_FLOP = std("entity.salmon.flop");
   public static final XSound ENTITY_SHEEP_DEATH = std("entity.sheep.death");
   public static final XSound ENTITY_SHEEP_HURT = std("entity.sheep.hurt");
   public static final XSound ENTITY_SHULKER_AMBIENT = std("entity.shulker.ambient");
   public static final XSound ENTITY_SHULKER_BULLET_HIT = std("entity.shulker_bullet.hit");
   public static final XSound ENTITY_SHULKER_BULLET_HURT = std("entity.shulker_bullet.hurt");
   public static final XSound ENTITY_SHULKER_CLOSE = std("entity.shulker.close");
   public static final XSound ENTITY_SHULKER_DEATH = std("entity.shulker.death");
   public static final XSound ENTITY_SHULKER_HURT = std("entity.shulker.hurt");
   public static final XSound ENTITY_SHULKER_HURT_CLOSED = std("entity.shulker.hurt_closed");
   public static final XSound ENTITY_SHULKER_OPEN = std("entity.shulker.open");
   public static final XSound ENTITY_SHULKER_SHOOT = std("entity.shulker.shoot");
   public static final XSound ENTITY_SHULKER_TELEPORT = std("entity.shulker.teleport");
   public static final XSound ENTITY_SKELETON_CONVERTED_TO_STRAY = std("entity.skeleton.converted_to_stray");
   public static final XSound ENTITY_SKELETON_HORSE_AMBIENT_WATER = std("entity.skeleton_horse.ambient_water");
   public static final XSound ENTITY_SKELETON_HORSE_GALLOP_WATER = std("entity.skeleton_horse.gallop_water");
   public static final XSound ENTITY_SKELETON_HORSE_JUMP_WATER = std("entity.skeleton_horse.jump_water");
   public static final XSound ENTITY_SKELETON_HORSE_STEP_WATER = std("entity.skeleton_horse.step_water");
   public static final XSound ENTITY_SKELETON_HORSE_SWIM = std("entity.skeleton_horse.swim");
   public static final XSound ENTITY_SKELETON_SHOOT = std("entity.skeleton.shoot");
   public static final XSound ENTITY_SLIME_DEATH = std("entity.slime.death", "ENTITY_SMALL_SLIME_DEATH");
   public static final XSound ENTITY_SLIME_DEATH_SMALL = std("entity.slime.death_small", "ENTITY_SMALL_SLIME_DEATH");
   public static final XSound ENTITY_SLIME_HURT = std("entity.slime.hurt");
   public static final XSound ENTITY_SNIFFER_DEATH = std("entity.sniffer.death");
   public static final XSound ENTITY_SNIFFER_DIGGING = std("entity.sniffer.digging");
   public static final XSound ENTITY_SNIFFER_DIGGING_STOP = std("entity.sniffer.digging_stop");
   public static final XSound ENTITY_SNIFFER_DROP_SEED = std("entity.sniffer.drop_seed");
   public static final XSound ENTITY_SNIFFER_EAT = std("entity.sniffer.eat");
   public static final XSound ENTITY_SNIFFER_HAPPY = std("entity.sniffer.happy");
   public static final XSound ENTITY_SNIFFER_HURT = std("entity.sniffer.hurt");
   public static final XSound ENTITY_SNIFFER_IDLE = std("entity.sniffer.idle");
   public static final XSound ENTITY_SNIFFER_SCENTING = std("entity.sniffer.scenting");
   public static final XSound ENTITY_SNIFFER_SEARCHING = std("entity.sniffer.searching");
   public static final XSound ENTITY_SNIFFER_SNIFFING = std("entity.sniffer.sniffing");
   public static final XSound ENTITY_SNIFFER_STEP = std("entity.sniffer.step");
   public static final XSound ENTITY_SNOWBALL_THROW = std("entity.snowball.throw");
   public static final XSound ENTITY_SPIDER_HURT = std("entity.spider.hurt");
   public static final XSound ENTITY_SPLASH_POTION_BREAK = std("entity.splash_potion.break");
   public static final XSound ENTITY_SPLASH_POTION_THROW = std("entity.splash_potion.throw");
   public static final XSound ENTITY_SQUID_AMBIENT = std("entity.squid.ambient");
   public static final XSound ENTITY_SQUID_DEATH = std("entity.squid.death");
   public static final XSound ENTITY_SQUID_HURT = std("entity.squid.hurt");
   public static final XSound ENTITY_SQUID_SQUIRT = std("entity.squid.squirt");
   public static final XSound ENTITY_STRAY_AMBIENT = std("entity.stray.ambient");
   public static final XSound ENTITY_STRAY_DEATH = std("entity.stray.death");
   public static final XSound ENTITY_STRAY_HURT = std("entity.stray.hurt");
   public static final XSound ENTITY_STRAY_STEP = std("entity.stray.step");
   public static final XSound ENTITY_STRIDER_AMBIENT = std("entity.strider.ambient");
   public static final XSound ENTITY_STRIDER_DEATH = std("entity.strider.death");
   public static final XSound ENTITY_STRIDER_EAT = std("entity.strider.eat");
   public static final XSound ENTITY_STRIDER_HAPPY = std("entity.strider.happy");
   public static final XSound ENTITY_STRIDER_HURT = std("entity.strider.hurt");
   public static final XSound ENTITY_STRIDER_RETREAT = std("entity.strider.retreat");
   public static final XSound ENTITY_STRIDER_SADDLE = std("entity.strider.saddle");
   public static final XSound ENTITY_STRIDER_STEP = std("entity.strider.step");
   public static final XSound ENTITY_STRIDER_STEP_LAVA = std("entity.strider.step_lava");
   public static final XSound ENTITY_TADPOLE_DEATH = std("entity.tadpole.death");
   public static final XSound ENTITY_TADPOLE_FLOP = std("entity.tadpole.flop");
   public static final XSound ENTITY_TADPOLE_GROW_UP = std("entity.tadpole.grow_up");
   public static final XSound ENTITY_TADPOLE_HURT = std("entity.tadpole.hurt");
   public static final XSound ENTITY_TROPICAL_FISH_AMBIENT = std("entity.tropical_fish.ambient");
   public static final XSound ENTITY_TROPICAL_FISH_DEATH = std("entity.tropical_fish.death");
   public static final XSound ENTITY_TROPICAL_FISH_HURT = std("entity.tropical_fish.hurt");
   public static final XSound ENTITY_TURTLE_AMBIENT_LAND = std("entity.turtle.ambient_land");
   public static final XSound ENTITY_TURTLE_DEATH = std("entity.turtle.death");
   public static final XSound ENTITY_TURTLE_DEATH_BABY = std("entity.turtle.death_baby");
   public static final XSound ENTITY_TURTLE_EGG_BREAK = std("entity.turtle.egg_break");
   public static final XSound ENTITY_TURTLE_EGG_CRACK = std("entity.turtle.egg_crack");
   public static final XSound ENTITY_TURTLE_EGG_HATCH = std("entity.turtle.egg_hatch");
   public static final XSound ENTITY_TURTLE_HURT = std("entity.turtle.hurt");
   public static final XSound ENTITY_TURTLE_HURT_BABY = std("entity.turtle.hurt_baby");
   public static final XSound ENTITY_TURTLE_LAY_EGG = std("entity.turtle.lay_egg");
   public static final XSound ENTITY_TURTLE_SHAMBLE = std("entity.turtle.shamble");
   public static final XSound ENTITY_TURTLE_SHAMBLE_BABY = std("entity.turtle.shamble_baby");
   public static final XSound ENTITY_TURTLE_SWIM = std("entity.turtle.swim");
   public static final XSound ENTITY_VEX_AMBIENT = std("entity.vex.ambient");
   public static final XSound ENTITY_VEX_CHARGE = std("entity.vex.charge");
   public static final XSound ENTITY_VEX_DEATH = std("entity.vex.death");
   public static final XSound ENTITY_VEX_HURT = std("entity.vex.hurt");
   public static final XSound ENTITY_VILLAGER_CELEBRATE = std("entity.villager.celebrate");
   public static final XSound ENTITY_VILLAGER_WORK_ARMORER = std("entity.villager.work_armorer");
   public static final XSound ENTITY_VILLAGER_WORK_BUTCHER = std("entity.villager.work_butcher");
   public static final XSound ENTITY_VILLAGER_WORK_CARTOGRAPHER = std("entity.villager.work_cartographer");
   public static final XSound ENTITY_VILLAGER_WORK_CLERIC = std("entity.villager.work_cleric");
   public static final XSound ENTITY_VILLAGER_WORK_FARMER = std("entity.villager.work_farmer");
   public static final XSound ENTITY_VILLAGER_WORK_FISHERMAN = std("entity.villager.work_fisherman");
   public static final XSound ENTITY_VILLAGER_WORK_FLETCHER = std("entity.villager.work_fletcher");
   public static final XSound ENTITY_VILLAGER_WORK_LEATHERWORKER = std("entity.villager.work_leatherworker");
   public static final XSound ENTITY_VILLAGER_WORK_LIBRARIAN = std("entity.villager.work_librarian");
   public static final XSound ENTITY_VILLAGER_WORK_MASON = std("entity.villager.work_mason");
   public static final XSound ENTITY_VILLAGER_WORK_SHEPHERD = std("entity.villager.work_shepherd");
   public static final XSound ENTITY_VILLAGER_WORK_TOOLSMITH = std("entity.villager.work_toolsmith");
   public static final XSound ENTITY_VILLAGER_WORK_WEAPONSMITH = std("entity.villager.work_weaponsmith");
   public static final XSound ENTITY_VINDICATOR_CELEBRATE = std("entity.vindicator.celebrate");
   public static final XSound ENTITY_WANDERING_TRADER_AMBIENT = std("entity.wandering_trader.ambient");
   public static final XSound ENTITY_WANDERING_TRADER_DEATH = std("entity.wandering_trader.death");
   public static final XSound ENTITY_WANDERING_TRADER_DISAPPEARED = std("entity.wandering_trader.disappeared");
   public static final XSound ENTITY_WANDERING_TRADER_DRINK_MILK = std("entity.wandering_trader.drink_milk");
   public static final XSound ENTITY_WANDERING_TRADER_DRINK_POTION = std("entity.wandering_trader.drink_potion");
   public static final XSound ENTITY_WANDERING_TRADER_HURT = std("entity.wandering_trader.hurt");
   public static final XSound ENTITY_WANDERING_TRADER_NO = std("entity.wandering_trader.no");
   public static final XSound ENTITY_WANDERING_TRADER_REAPPEARED = std("entity.wandering_trader.reappeared");
   public static final XSound ENTITY_WANDERING_TRADER_TRADE = std("entity.wandering_trader.trade");
   public static final XSound ENTITY_WANDERING_TRADER_YES = std("entity.wandering_trader.yes");
   public static final XSound ENTITY_WARDEN_AGITATED = std("entity.warden.agitated");
   public static final XSound ENTITY_WARDEN_AMBIENT = std("entity.warden.ambient");
   public static final XSound ENTITY_WARDEN_ANGRY = std("entity.warden.angry");
   public static final XSound ENTITY_WARDEN_ATTACK_IMPACT = std("entity.warden.attack_impact");
   public static final XSound ENTITY_WARDEN_DEATH = std("entity.warden.death");
   public static final XSound ENTITY_WARDEN_DIG = std("entity.warden.dig");
   public static final XSound ENTITY_WARDEN_EMERGE = std("entity.warden.emerge");
   public static final XSound ENTITY_WARDEN_HEARTBEAT = std("entity.warden.heartbeat");
   public static final XSound ENTITY_WARDEN_HURT = std("entity.warden.hurt");
   public static final XSound ENTITY_WARDEN_LISTENING = std("entity.warden.listening");
   public static final XSound ENTITY_WARDEN_LISTENING_ANGRY = std("entity.warden.listening_angry");
   public static final XSound ENTITY_WARDEN_NEARBY_CLOSE = std("entity.warden.nearby_close");
   public static final XSound ENTITY_WARDEN_NEARBY_CLOSER = std("entity.warden.nearby_closer");
   public static final XSound ENTITY_WARDEN_NEARBY_CLOSEST = std("entity.warden.nearby_closest");
   public static final XSound ENTITY_WARDEN_ROAR = std("entity.warden.roar");
   public static final XSound ENTITY_WARDEN_SNIFF = std("entity.warden.sniff");
   public static final XSound ENTITY_WARDEN_SONIC_BOOM = std("entity.warden.sonic_boom");
   public static final XSound ENTITY_WARDEN_SONIC_CHARGE = std("entity.warden.sonic_charge");
   public static final XSound ENTITY_WARDEN_STEP = std("entity.warden.step");
   public static final XSound ENTITY_WARDEN_TENDRIL_CLICKS = std("entity.warden.tendril_clicks");
   public static final XSound ENTITY_WIND_CHARGE_THROW = std("entity.wind_charge.throw");
   public static final XSound ENTITY_WITCH_AMBIENT = std("entity.witch.ambient");
   public static final XSound ENTITY_WITCH_CELEBRATE = std("entity.witch.celebrate");
   public static final XSound ENTITY_WITCH_DEATH = std("entity.witch.death");
   public static final XSound ENTITY_WITCH_DRINK = std("entity.witch.drink");
   public static final XSound ENTITY_WITCH_HURT = std("entity.witch.hurt");
   public static final XSound ENTITY_WITCH_THROW = std("entity.witch.throw");
   public static final XSound ENTITY_WITHER_BREAK_BLOCK = std("entity.wither.break_block");
   public static final XSound ENTITY_WITHER_SKELETON_AMBIENT = std("entity.wither_skeleton.ambient");
   public static final XSound ENTITY_WITHER_SKELETON_DEATH = std("entity.wither_skeleton.death");
   public static final XSound ENTITY_WITHER_SKELETON_HURT = std("entity.wither_skeleton.hurt");
   public static final XSound ENTITY_WITHER_SKELETON_STEP = std("entity.wither_skeleton.step");
   public static final XSound ENTITY_ZOGLIN_AMBIENT = std("entity.zoglin.ambient");
   public static final XSound ENTITY_ZOGLIN_ANGRY = std("entity.zoglin.angry");
   public static final XSound ENTITY_ZOGLIN_ATTACK = std("entity.zoglin.attack");
   public static final XSound ENTITY_ZOGLIN_DEATH = std("entity.zoglin.death");
   public static final XSound ENTITY_ZOGLIN_HURT = std("entity.zoglin.hurt");
   public static final XSound ENTITY_ZOGLIN_STEP = std("entity.zoglin.step");
   public static final XSound ENTITY_ZOMBIE_CONVERTED_TO_DROWNED = std("entity.zombie.converted_to_drowned");
   public static final XSound ENTITY_ZOMBIE_DESTROY_EGG = std("entity.zombie.destroy_egg");
   public static final XSound ENTITY_ZOMBIE_VILLAGER_AMBIENT = std("entity.zombie_villager.ambient");
   public static final XSound ENTITY_ZOMBIE_VILLAGER_DEATH = std("entity.zombie_villager.death");
   public static final XSound ENTITY_ZOMBIE_VILLAGER_HURT = std("entity.zombie_villager.hurt");
   public static final XSound ENTITY_ZOMBIE_VILLAGER_STEP = std("entity.zombie_villager.step");
   public static final XSound EVENT_MOB_EFFECT_BAD_OMEN = std("event.mob_effect.bad_omen");
   public static final XSound EVENT_MOB_EFFECT_RAID_OMEN = std("event.mob_effect.raid_omen");
   public static final XSound EVENT_MOB_EFFECT_TRIAL_OMEN = std("event.mob_effect.trial_omen");
   public static final XSound EVENT_RAID_HORN = std("event.raid.horn");
   public static final XSound INTENTIONALLY_EMPTY = std("intentionally_empty");
   public static final XSound ITEM_ARMOR_EQUIP_CHAIN = std("item.armor.equip_chain");
   public static final XSound ITEM_ARMOR_EQUIP_DIAMOND = std("item.armor.equip_diamond");
   public static final XSound ITEM_ARMOR_EQUIP_ELYTRA = std("item.armor.equip_elytra");
   public static final XSound ITEM_ARMOR_EQUIP_GENERIC = std("item.armor.equip_generic");
   public static final XSound ITEM_ARMOR_EQUIP_GOLD = std("item.armor.equip_gold");
   public static final XSound ITEM_ARMOR_EQUIP_IRON = std("item.armor.equip_iron");
   public static final XSound ITEM_ARMOR_EQUIP_LEATHER = std("item.armor.equip_leather");
   public static final XSound ITEM_ARMOR_EQUIP_NETHERITE = std("item.armor.equip_netherite");
   public static final XSound ITEM_ARMOR_EQUIP_TURTLE = std("item.armor.equip_turtle");
   public static final XSound ITEM_ARMOR_EQUIP_WOLF = std("item.armor.equip_wolf");
   public static final XSound ITEM_ARMOR_UNEQUIP_WOLF = std("item.armor.unequip_wolf");
   public static final XSound ITEM_AXE_SCRAPE = std("item.axe.scrape");
   public static final XSound ITEM_AXE_STRIP = std("item.axe.strip");
   public static final XSound ITEM_AXE_WAX_OFF = std("item.axe.wax_off");
   public static final XSound ITEM_BONE_MEAL_USE = std("item.bone_meal.use");
   public static final XSound ITEM_BOOK_PAGE_TURN = std("item.book.page_turn");
   public static final XSound ITEM_BOOK_PUT = std("item.book.put");
   public static final XSound ITEM_BOTTLE_EMPTY = std("item.bottle.empty");
   public static final XSound ITEM_BOTTLE_FILL = std("item.bottle.fill");
   public static final XSound ITEM_BOTTLE_FILL_DRAGONBREATH = std("item.bottle.fill_dragonbreath");
   public static final XSound ITEM_BRUSH_BRUSHING_GENERIC = std("item.brush.brushing.generic");
   public static final XSound ITEM_BRUSH_BRUSHING_GRAVEL = std("item.brush.brushing.gravel");
   public static final XSound ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE = std("item.brush.brushing.gravel.complete");
   public static final XSound ITEM_BRUSH_BRUSHING_SAND = std("item.brush.brushing.sand");
   public static final XSound ITEM_BRUSH_BRUSHING_SAND_COMPLETE = std("item.brush.brushing.sand.complete");
   public static final XSound ITEM_BUCKET_EMPTY = std("item.bucket.empty");
   public static final XSound ITEM_BUCKET_EMPTY_AXOLOTL = std("item.bucket.empty_axolotl");
   public static final XSound ITEM_BUCKET_EMPTY_FISH = std("item.bucket.empty_fish");
   public static final XSound ITEM_BUCKET_EMPTY_LAVA = std("item.bucket.empty_lava");
   public static final XSound ITEM_BUCKET_EMPTY_POWDER_SNOW = std("item.bucket.empty_powder_snow");
   public static final XSound ITEM_BUCKET_EMPTY_TADPOLE = std("item.bucket.empty_tadpole");
   public static final XSound ITEM_BUCKET_FILL = std("item.bucket.fill");
   public static final XSound ITEM_BUCKET_FILL_AXOLOTL = std("item.bucket.fill_axolotl");
   public static final XSound ITEM_BUCKET_FILL_FISH = std("item.bucket.fill_fish");
   public static final XSound ITEM_BUCKET_FILL_LAVA = std("item.bucket.fill_lava");
   public static final XSound ITEM_BUCKET_FILL_POWDER_SNOW = std("item.bucket.fill_powder_snow");
   public static final XSound ITEM_BUCKET_FILL_TADPOLE = std("item.bucket.fill_tadpole");
   public static final XSound ITEM_BUNDLE_DROP_CONTENTS = std("item.bundle.drop_contents");
   public static final XSound ITEM_BUNDLE_INSERT = std("item.bundle.insert");
   public static final XSound ITEM_BUNDLE_INSERT_FAIL = std("item.bundle.insert_fail");
   public static final XSound ITEM_BUNDLE_REMOVE_ONE = std("item.bundle.remove_one");
   public static final XSound ITEM_CHORUS_FRUIT_TELEPORT = std("item.chorus_fruit.teleport");
   public static final XSound ITEM_CROP_PLANT = std("item.crop.plant");
   public static final XSound ITEM_CROSSBOW_HIT = std("item.crossbow.hit");
   public static final XSound ITEM_CROSSBOW_LOADING_END = std("item.crossbow.loading_end");
   public static final XSound ITEM_CROSSBOW_LOADING_MIDDLE = std("item.crossbow.loading_middle");
   public static final XSound ITEM_CROSSBOW_LOADING_START = std("item.crossbow.loading_start");
   public static final XSound ITEM_CROSSBOW_QUICK_CHARGE_1 = std("item.crossbow.quick_charge_1");
   public static final XSound ITEM_CROSSBOW_QUICK_CHARGE_2 = std("item.crossbow.quick_charge_2");
   public static final XSound ITEM_CROSSBOW_QUICK_CHARGE_3 = std("item.crossbow.quick_charge_3");
   public static final XSound ITEM_CROSSBOW_SHOOT = std("item.crossbow.shoot");
   public static final XSound ITEM_DYE_USE = std("item.dye.use");
   public static final XSound ITEM_ELYTRA_FLYING = std("item.elytra.flying");
   public static final XSound ITEM_FIRECHARGE_USE = std("item.firecharge.use");
   public static final XSound ITEM_GLOW_INK_SAC_USE = std("item.glow_ink_sac.use");
   public static final XSound ITEM_GOAT_HORN_SOUND_0 = std("item.goat_horn.sound.0");
   public static final XSound ITEM_GOAT_HORN_SOUND_1 = std("item.goat_horn.sound.1");
   public static final XSound ITEM_GOAT_HORN_SOUND_2 = std("item.goat_horn.sound.2");
   public static final XSound ITEM_GOAT_HORN_SOUND_3 = std("item.goat_horn.sound.3");
   public static final XSound ITEM_GOAT_HORN_SOUND_4 = std("item.goat_horn.sound.4");
   public static final XSound ITEM_GOAT_HORN_SOUND_5 = std("item.goat_horn.sound.5");
   public static final XSound ITEM_GOAT_HORN_SOUND_6 = std("item.goat_horn.sound.6");
   public static final XSound ITEM_GOAT_HORN_SOUND_7 = std("item.goat_horn.sound.7");
   public static final XSound ITEM_HOE_TILL = std("item.hoe.till");
   public static final XSound ITEM_HONEYCOMB_WAX_ON = std("item.honeycomb.wax_on");
   public static final XSound ITEM_HONEY_BOTTLE_DRINK = std("item.honey_bottle.drink");
   public static final XSound ITEM_INK_SAC_USE = std("item.ink_sac.use");
   public static final XSound ITEM_LODESTONE_COMPASS_LOCK = std("item.lodestone_compass.lock");
   public static final XSound ITEM_MACE_SMASH_AIR = std("item.mace.smash_air");
   public static final XSound ITEM_MACE_SMASH_GROUND = std("item.mace.smash_ground");
   public static final XSound ITEM_MACE_SMASH_GROUND_HEAVY = std("item.mace.smash_ground_heavy");
   public static final XSound ITEM_NETHER_WART_PLANT = std("item.nether_wart.plant");
   public static final XSound ITEM_OMINOUS_BOTTLE_DISPOSE = std("item.ominous_bottle.dispose");
   public static final XSound ITEM_SHIELD_BLOCK = std("item.shield.block");
   public static final XSound ITEM_SHIELD_BREAK = std("item.shield.break");
   public static final XSound ITEM_SHOVEL_FLATTEN = std("item.shovel.flatten");
   public static final XSound ITEM_SPYGLASS_STOP_USING = std("item.spyglass.stop_using");
   public static final XSound ITEM_SPYGLASS_USE = std("item.spyglass.use");
   public static final XSound ITEM_TOTEM_USE = std("item.totem.use");
   public static final XSound ITEM_TRIDENT_HIT = std("item.trident.hit");
   public static final XSound ITEM_TRIDENT_HIT_GROUND = std("item.trident.hit_ground");
   public static final XSound ITEM_TRIDENT_RETURN = std("item.trident.return");
   public static final XSound ITEM_TRIDENT_RIPTIDE_1 = std("item.trident.riptide_1");
   public static final XSound ITEM_TRIDENT_THROW = std("item.trident.throw");
   public static final XSound ITEM_TRIDENT_THUNDER = std("item.trident.thunder");
   public static final XSound ITEM_WOLF_ARMOR_BREAK = std("item.wolf_armor.break");
   public static final XSound ITEM_WOLF_ARMOR_CRACK = std("item.wolf_armor.crack");
   public static final XSound ITEM_WOLF_ARMOR_DAMAGE = std("item.wolf_armor.damage");
   public static final XSound ITEM_WOLF_ARMOR_REPAIR = std("item.wolf_armor.repair");
   public static final XSound MUSIC_CREATIVE = std("music.creative");
   public static final XSound MUSIC_CREDITS = std("music.credits");
   public static final XSound MUSIC_DISC_5 = std("music_disc.5");
   public static final XSound MUSIC_DISC_CREATOR = std("music_disc.creator");
   public static final XSound MUSIC_DISC_CREATOR_MUSIC_BOX = std("music_disc.creator_music_box");
   public static final XSound MUSIC_DISC_OTHERSIDE = std("music_disc.otherside");
   public static final XSound MUSIC_DISC_PIGSTEP = std("music_disc.pigstep");
   public static final XSound MUSIC_DISC_PRECIPICE = std("music_disc.precipice");
   public static final XSound MUSIC_DISC_RELIC = std("music_disc.relic");
   public static final XSound MUSIC_DRAGON = std("music.dragon");
   public static final XSound MUSIC_END = std("music.end");
   public static final XSound MUSIC_GAME = std("music.game");
   public static final XSound MUSIC_MENU = std("music.menu");
   public static final XSound MUSIC_NETHER_CRIMSON_FOREST = std("music.nether.crimson_forest");
   public static final XSound MUSIC_NETHER_NETHER_WASTES = std("music.nether.nether_wastes");
   public static final XSound MUSIC_NETHER_SOUL_SAND_VALLEY = std("music.nether.soul_sand_valley");
   public static final XSound MUSIC_NETHER_WARPED_FOREST = std("music.nether.warped_forest");
   public static final XSound MUSIC_OVERWORLD_BADLANDS = std("music.overworld.badlands");
   public static final XSound MUSIC_OVERWORLD_BAMBOO_JUNGLE = std("music.overworld.bamboo_jungle");
   public static final XSound MUSIC_OVERWORLD_CHERRY_GROVE = std("music.overworld.cherry_grove");
   public static final XSound MUSIC_OVERWORLD_DEEP_DARK = std("music.overworld.deep_dark");
   public static final XSound MUSIC_OVERWORLD_DESERT = std("music.overworld.desert");
   public static final XSound MUSIC_OVERWORLD_DRIPSTONE_CAVES = std("music.overworld.dripstone_caves");
   public static final XSound MUSIC_OVERWORLD_FLOWER_FOREST = std("music.overworld.flower_forest");
   public static final XSound MUSIC_OVERWORLD_FOREST = std("music.overworld.forest");
   public static final XSound MUSIC_OVERWORLD_FROZEN_PEAKS = std("music.overworld.frozen_peaks");
   public static final XSound MUSIC_OVERWORLD_GROVE = std("music.overworld.grove");
   public static final XSound MUSIC_OVERWORLD_JAGGED_PEAKS = std("music.overworld.jagged_peaks");
   public static final XSound MUSIC_OVERWORLD_JUNGLE = std("music.overworld.jungle");
   public static final XSound MUSIC_OVERWORLD_LUSH_CAVES = std("music.overworld.lush_caves");
   public static final XSound MUSIC_OVERWORLD_MEADOW = std("music.overworld.meadow");
   public static final XSound MUSIC_OVERWORLD_OLD_GROWTH_TAIGA = std("music.overworld.old_growth_taiga");
   public static final XSound MUSIC_OVERWORLD_SNOWY_SLOPES = std("music.overworld.snowy_slopes");
   public static final XSound MUSIC_OVERWORLD_SPARSE_JUNGLE = std("music.overworld.sparse_jungle");
   public static final XSound MUSIC_OVERWORLD_STONY_PEAKS = std("music.overworld.stony_peaks");
   public static final XSound MUSIC_OVERWORLD_SWAMP = std("music.overworld.swamp");
   public static final XSound MUSIC_UNDER_WATER = std("music.under_water");
   public static final XSound PARTICLE_SOUL_ESCAPE = std("particle.soul_escape");
   public static final XSound UI_CARTOGRAPHY_TABLE_TAKE_RESULT = std("ui.cartography_table.take_result");
   public static final XSound UI_HUD_BUBBLE_POP = std("ui.hud.bubble_pop");
   public static final XSound UI_LOOM_SELECT_PATTERN = std("ui.loom.select_pattern");
   public static final XSound UI_LOOM_TAKE_RESULT = std("ui.loom.take_result");
   public static final XSound UI_STONECUTTER_SELECT_RECIPE = std("ui.stonecutter.select_recipe");
   public static final XSound UI_STONECUTTER_TAKE_RESULT = std("ui.stonecutter.take_result");
   public static final XSound UI_TOAST_CHALLENGE_COMPLETE = std("ui.toast.challenge_complete");
   public static final XSound UI_TOAST_IN = std("ui.toast.in");
   public static final XSound UI_TOAST_OUT = std("ui.toast.out");
   public static final XSound WEATHER_RAIN_ABOVE = std("weather.rain.above");
   public static final XSound BLOCK_EYEBLOSSOM_CLOSE = std("block.eyeblossom.close");
   public static final XSound BLOCK_RESIN_BRICKS_FALL = std("block.resin_bricks.fall");
   public static final XSound BLOCK_RESIN_BRICKS_STEP = std("block.resin_bricks.step");
   public static final XSound BLOCK_RESIN_PLACE = std("block.resin.place");
   public static final XSound ENTITY_CREAKING_TWITCH = std("entity.creaking.twitch");
   public static final XSound BLOCK_EYEBLOSSOM_IDLE = std("block.eyeblossom.idle");
   public static final XSound BLOCK_RESIN_BREAK = std("block.resin.break");
   public static final XSound BLOCK_RESIN_BRICKS_PLACE = std("block.resin_bricks.place");
   public static final XSound BLOCK_RESIN_BRICKS_BREAK = std("block.resin_bricks.break");
   public static final XSound BLOCK_EYEBLOSSOM_CLOSE_LONG = std("block.eyeblossom.close_long");
   public static final XSound BLOCK_RESIN_FALL = std("block.resin.fall");
   public static final XSound BLOCK_RESIN_STEP = std("block.resin.step");
   public static final XSound BLOCK_EYEBLOSSOM_OPEN = std("block.eyeblossom.open");
   public static final XSound BLOCK_RESIN_BRICKS_HIT = std("block.resin_bricks.hit");
   public static final XSound BLOCK_EYEBLOSSOM_OPEN_LONG = std("block.eyeblossom.open_long");
   public static final XSound ENTITY_WIND_CHARGE_WIND_BURST = std("entity.wind_charge.wind_burst", "ENTITY_GENERIC_WIND_BURST");
   @Deprecated
   public static final XSound MUSIC_OVERWORLD_JUNGLE_AND_FOREST = std("music.overworld.jungle_and_forest");
   @Deprecated
   public static final XSound BLOCK_TRIAL_SPAWNER_AMBIENT_CHARGED = std("block.trial_spawner.ambient_charged");
   @Deprecated
   public static final XSound BLOCK_TRIAL_SPAWNER_CHARGE_ACTIVATE = std("block.trial_spawner.charge_activate");
   @Deprecated
   public static final XSound ENTITY_GOAT_SCREAMING_HORN_BREAK = std("entity.goat.screaming.horn_break");
   @Deprecated
   public static final XSound ITEM_BRUSH_BRUSH_SAND_COMPLETED = std("item.brush.brush_sand_completed");
   @Deprecated
   public static final XSound ITEM_GOAT_HORN_PLAY = std("item.goat_horn.play");
   @Deprecated
   public static final XSound ITEM_BRUSH_BRUSHING = std("item.brush.brushing");
   @Deprecated
   public static final XSound ENTITY_PARROT_IMITATE_WOLF = std("ENTITY_PARROT_IMITATE_WOLF");
   @Deprecated
   public static final XSound ENTITY_PARROT_IMITATE_POLAR_BEAR = std("ENTITY_PARROT_IMITATE_POLAR_BEAR");
   @Deprecated
   public static final XSound ENTITY_PARROT_IMITATE_PANDA = std("ENTITY_PARROT_IMITATE_PANDA");
   @Deprecated
   public static final XSound ENTITY_PARROT_IMITATE_ENDERMAN = std("ENTITY_PARROT_IMITATE_ENDERMAN");
   public static final XSound BLOCK_CACTUS_FLOWER_BREAK = std("block.cactus_flower.break");
   public static final XSound BLOCK_CACTUS_FLOWER_PLACE = std("block.cactus_flower.place");
   public static final XSound BLOCK_DEADBUSH_IDLE = std("block.deadbush.idle");
   public static final XSound BLOCK_FIREFLY_BUSH_IDLE = std("block.firefly_bush.idle");
   public static final XSound BLOCK_IRON_BREAK = std("block.iron.break");
   public static final XSound BLOCK_IRON_FALL = std("block.iron.fall");
   public static final XSound BLOCK_IRON_HIT = std("block.iron.hit");
   public static final XSound BLOCK_IRON_PLACE = std("block.iron.place");
   public static final XSound BLOCK_IRON_STEP = std("block.iron.step");
   public static final XSound BLOCK_LEAF_LITTER_BREAK = std("block.leaf_litter.break");
   public static final XSound BLOCK_LEAF_LITTER_STEP = std("block.leaf_litter.step");
   public static final XSound BLOCK_LEAF_LITTER_PLACE = std("block.leaf_litter.place");
   public static final XSound BLOCK_LEAF_LITTER_HIT = std("block.leaf_litter.hit");
   public static final XSound BLOCK_LEAF_LITTER_FALL = std("block.leaf_litter.fall");
   public static final XSound BLOCK_SAND_IDLE = std("block.sand.idle");
   public static final XSound BLOCK_SAND_WIND = std("block.sand.wind");
   public static final XSound ENTITY_WOLF_ANGRY_AMBIENT = std("entity.wolf_angry.ambient");
   public static final XSound ENTITY_WOLF_ANGRY_DEATH = std("entity.wolf_angry.death");
   public static final XSound ENTITY_WOLF_ANGRY_GROWL = std("entity.wolf_angry.growl");
   public static final XSound ENTITY_WOLF_ANGRY_HURT = std("entity.wolf_angry.hurt");
   public static final XSound ENTITY_WOLF_ANGRY_PANT = std("entity.wolf_angry.pant");
   public static final XSound ENTITY_WOLF_ANGRY_WHINE = std("entity.wolf_angry.whine");
   public static final XSound ENTITY_WOLF_BIG_AMBIENT = std("entity.wolf_big.ambient");
   public static final XSound ENTITY_WOLF_BIG_DEATH = std("entity.wolf_big.death");
   public static final XSound ENTITY_WOLF_BIG_GROWL = std("entity.wolf_big.growl");
   public static final XSound ENTITY_WOLF_BIG_HURT = std("entity.wolf_big.hurt");
   public static final XSound ENTITY_WOLF_BIG_PANT = std("entity.wolf_big.pant");
   public static final XSound ENTITY_WOLF_BIG_WHINE = std("entity.wolf_big.whine");
   public static final XSound ENTITY_WOLF_CUTE_AMBIENT = std("entity.wolf_cute.ambient");
   public static final XSound ENTITY_WOLF_CUTE_DEATH = std("entity.wolf_cute.death");
   public static final XSound ENTITY_WOLF_CUTE_GROWL = std("entity.wolf_cute.growl");
   public static final XSound ENTITY_WOLF_CUTE_HURT = std("entity.wolf_cute.hurt");
   public static final XSound ENTITY_WOLF_CUTE_PANT = std("entity.wolf_cute.pant");
   public static final XSound ENTITY_WOLF_CUTE_WHINE = std("entity.wolf_cute.whine");
   public static final XSound ENTITY_WOLF_GRUMPY_AMBIENT = std("entity.wolf_grumpy.ambient");
   public static final XSound ENTITY_WOLF_GRUMPY_DEATH = std("entity.wolf_grumpy.death");
   public static final XSound ENTITY_WOLF_GRUMPY_GROWL = std("entity.wolf_grumpy.growl");
   public static final XSound ENTITY_WOLF_GRUMPY_HURT = std("entity.wolf_grumpy.hurt");
   public static final XSound ENTITY_WOLF_GRUMPY_PANT = std("entity.wolf_grumpy.pant");
   public static final XSound ENTITY_WOLF_GRUMPY_WHINE = std("entity.wolf_grumpy.whine");
   public static final XSound ENTITY_WOLF_PUGLIN_AMBIENT = std("entity.wolf_puglin.ambient");
   public static final XSound ENTITY_WOLF_PUGLIN_DEATH = std("entity.wolf_puglin.death");
   public static final XSound ENTITY_WOLF_PUGLIN_GROWL = std("entity.wolf_puglin.growl");
   public static final XSound ENTITY_WOLF_PUGLIN_HURT = std("entity.wolf_puglin.hurt");
   public static final XSound ENTITY_WOLF_PUGLIN_PANT = std("entity.wolf_puglin.pant");
   public static final XSound ENTITY_WOLF_PUGLIN_WHINE = std("entity.wolf_puglin.whine");
   public static final XSound ENTITY_WOLF_SAD_AMBIENT = std("entity.wolf_sad.ambient");
   public static final XSound ENTITY_WOLF_SAD_DEATH = std("entity.wolf_sad.death");
   public static final XSound ENTITY_WOLF_SAD_GROWL = std("entity.wolf_sad.growl");
   public static final XSound ENTITY_WOLF_SAD_HURT = std("entity.wolf_sad.hurt");
   public static final XSound ENTITY_WOLF_SAD_PANT = std("entity.wolf_sad.pant");
   public static final XSound ENTITY_WOLF_SAD_WHINE = std("entity.wolf_sad.whine");
   public static final XSound BLOCK_DRIED_GHAST_AMBIENT = std("block.dried_ghast.ambient");
   public static final XSound BLOCK_DRIED_GHAST_AMBIENT_WATER = std("block.dried_ghast.ambient_water");
   public static final XSound BLOCK_DRIED_GHAST_BREAK = std("block.dried_ghast.break");
   public static final XSound BLOCK_DRIED_GHAST_FALL = std("block.dried_ghast.fall");
   public static final XSound BLOCK_DRIED_GHAST_PLACE = std("block.dried_ghast.place");
   public static final XSound BLOCK_DRIED_GHAST_PLACE_IN_WATER = std("block.dried_ghast.place_in_water");
   public static final XSound BLOCK_DRIED_GHAST_STEP = std("block.dried_ghast.step");
   public static final XSound BLOCK_DRIED_GHAST_TRANSITION = std("block.dried_ghast.transition");
   public static final XSound BLOCK_DRY_GRASS_AMBIENT = std("block.dry_grass.ambient");
   public static final XSound ENTITY_GHASTLING_AMBIENT = std("entity.ghastling.ambient");
   public static final XSound ENTITY_GHASTLING_DEATH = std("entity.ghastling.death");
   public static final XSound ENTITY_GHASTLING_HURT = std("entity.ghastling.hurt");
   public static final XSound ENTITY_GHASTLING_SPAWN = std("entity.ghastling.spawn");
   public static final XSound ENTITY_HAPPY_GHAST_AMBIENT = std("entity.happy_ghast.ambient");
   public static final XSound ENTITY_HAPPY_GHAST_DEATH = std("entity.happy_ghast.death");
   public static final XSound ENTITY_HAPPY_GHAST_EQUIP = std("entity.happy_ghast.equip");
   public static final XSound ENTITY_HAPPY_GHAST_HARNESS_GOGGLES_DOWN = std("entity.happy_ghast.harness_goggles_down");
   public static final XSound ENTITY_HAPPY_GHAST_HARNESS_GOGGLES_UP = std("entity.happy_ghast.harness_goggles_up");
   public static final XSound ENTITY_HAPPY_GHAST_HURT = std("entity.happy_ghast.hurt");
   public static final XSound ENTITY_HAPPY_GHAST_RIDING = std("entity.happy_ghast.riding");
   public static final XSound ENTITY_HAPPY_GHAST_UNEQUIP = std("entity.happy_ghast.unequip");
   public static final XSound ITEM_HORSE_ARMOR_UNEQUIP = std("item.horse_armor.unequip");
   public static final XSound ITEM_LEAD_BREAK = std("item.lead.break");
   public static final XSound ITEM_LEAD_TIED = std("item.lead.tied");
   public static final XSound ITEM_LEAD_UNTIED = std("item.lead.untied");
   public static final XSound ITEM_LLAMA_CARPET_UNEQUIP = std("item.llama_carpet.unequip");
   public static final XSound ITEM_SADDLE_UNEQUIP = std("item.saddle.unequip");
   public static final XSound ITEM_SHEARS_SNIP = std("item.shears.snip");
   public static final XSound MUSIC_DISC_TEARS = std("music_disc.tears");
   public static final XSound MUSIC_DISC_LAVA_CHICKEN = std("music_disc.lava_chicken");
   public static final XSound BLOCK_COPPER_CHEST_CLOSE = std("block.copper_chest.close");
   public static final XSound BLOCK_COPPER_CHEST_OPEN = std("block.copper_chest.open");
   public static final XSound BLOCK_COPPER_CHEST_OXIDIZED_CLOSE = std("block.copper_chest_oxidized.close");
   public static final XSound BLOCK_COPPER_CHEST_OXIDIZED_OPEN = std("block.copper_chest_oxidized.open");
   public static final XSound BLOCK_COPPER_CHEST_WEATHERED_CLOSE = std("block.copper_chest_weathered.close");
   public static final XSound BLOCK_COPPER_CHEST_WEATHERED_OPEN = std("block.copper_chest_weathered.open");
   public static final XSound BLOCK_COPPER_GOLEM_STATUE_BREAK = std("block.copper_golem_statue.break");
   public static final XSound BLOCK_COPPER_GOLEM_STATUE_FALL = std("block.copper_golem_statue.fall");
   public static final XSound BLOCK_COPPER_GOLEM_STATUE_HIT = std("block.copper_golem_statue.hit");
   public static final XSound BLOCK_COPPER_GOLEM_STATUE_PLACE = std("block.copper_golem_statue.place");
   public static final XSound BLOCK_COPPER_GOLEM_STATUE_STEP = std("block.copper_golem_statue.step");
   public static final XSound BLOCK_SHELF_ACTIVATE = std("block.shelf.activate");
   public static final XSound BLOCK_SHELF_BREAK = std("block.shelf.break");
   public static final XSound BLOCK_SHELF_DEACTIVATE = std("block.shelf.deactivate");
   public static final XSound BLOCK_SHELF_FALL = std("block.shelf.fall");
   public static final XSound BLOCK_SHELF_HIT = std("block.shelf.hit");
   public static final XSound BLOCK_SHELF_MULTI_SWAP = std("block.shelf.multi_swap");
   public static final XSound BLOCK_SHELF_PLACE = std("block.shelf.place");
   public static final XSound BLOCK_SHELF_PLACE_ITEM = std("block.shelf.place_item");
   public static final XSound BLOCK_SHELF_SINGLE_SWAP = std("block.shelf.single_swap");
   public static final XSound BLOCK_SHELF_STEP = std("block.shelf.step");
   public static final XSound BLOCK_SHELF_TAKE_ITEM = std("block.shelf.take_item");
   public static final XSound ENTITY_COPPER_GOLEM_BECOME_STATUE = std("entity.copper_golem_become_statue");
   public static final XSound ENTITY_COPPER_GOLEM_DEATH = std("entity.copper_golem.death");
   public static final XSound ENTITY_COPPER_GOLEM_HURT = std("entity.copper_golem.hurt");
   public static final XSound ENTITY_COPPER_GOLEM_ITEM_DROP = std("entity.copper_golem.item_drop");
   public static final XSound ENTITY_COPPER_GOLEM_ITEM_NO_DROP = std("entity.copper_golem.item_no_drop");
   public static final XSound ENTITY_COPPER_GOLEM_NO_ITEM_GET = std("entity.copper_golem.no_item_get");
   public static final XSound ENTITY_COPPER_GOLEM_NO_ITEM_NO_GET = std("entity.copper_golem.no_item_no_get");
   public static final XSound ENTITY_COPPER_GOLEM_OXIDIZED_DEATH = std("entity.copper_golem_oxidized.death");
   public static final XSound ENTITY_COPPER_GOLEM_OXIDIZED_HURT = std("entity.copper_golem_oxidized.hurt");
   public static final XSound ENTITY_COPPER_GOLEM_OXIDIZED_SPIN = std("entity.copper_golem_oxidized.spin");
   public static final XSound ENTITY_COPPER_GOLEM_OXIDIZED_STEP = std("entity.copper_golem_oxidized.step");
   public static final XSound ENTITY_COPPER_GOLEM_SHEAR = std("entity.copper_golem.shear");
   public static final XSound ENTITY_COPPER_GOLEM_SPAWN = std("entity.copper_golem.spawn");
   public static final XSound ENTITY_COPPER_GOLEM_SPIN = std("entity.copper_golem.spin");
   public static final XSound ENTITY_COPPER_GOLEM_STEP = std("entity.copper_golem.step");
   public static final XSound ENTITY_COPPER_GOLEM_WEATHERED_DEATH = std("entity.copper_golem_weathered.death");
   public static final XSound ENTITY_COPPER_GOLEM_WEATHERED_HURT = std("entity.copper_golem_weathered.hurt");
   public static final XSound ENTITY_COPPER_GOLEM_WEATHERED_SPIN = std("entity.copper_golem_weathered.spin");
   public static final XSound ENTITY_COPPER_GOLEM_WEATHERED_STEP = std("entity.copper_golem_weathered.step");
   public static final XSound ITEM_ARMOR_EQUIP_COPPER = std("item.armor.equip_copper");
   public static final XSound WEATHER_END_FLASH = std("weather.end_flash");
   public static final XSound ENTITY_BABY_NAUTILUS_AMBIENT = std("entity.baby_nautilus.ambient");
   public static final XSound ENTITY_BABY_NAUTILUS_AMBIENT_LAND = std("entity.baby_nautilus.ambient_land");
   public static final XSound ENTITY_BABY_NAUTILUS_DEATH = std("entity.baby_nautilus.death");
   public static final XSound ENTITY_BABY_NAUTILUS_DEATH_LAND = std("entity.baby_nautilus.death_land");
   public static final XSound ENTITY_BABY_NAUTILUS_EAT = std("entity.baby_nautilus.eat");
   public static final XSound ENTITY_BABY_NAUTILUS_HURT = std("entity.baby_nautilus.hurt");
   public static final XSound ENTITY_BABY_NAUTILUS_HURT_LAND = std("entity.baby_nautilus.hurt_land");
   public static final XSound ENTITY_BABY_NAUTILUS_SWIM = std("entity.baby_nautilus.swim");
   public static final XSound ENTITY_CAMEL_HUSK_AMBIENT = std("entity.camel_husk.ambient");
   public static final XSound ENTITY_CAMEL_HUSK_DASH = std("entity.camel_husk.dash");
   public static final XSound ENTITY_CAMEL_HUSK_DASH_READY = std("entity.camel_husk.dash_ready");
   public static final XSound ENTITY_CAMEL_HUSK_DEATH = std("entity.camel_husk.death");
   public static final XSound ENTITY_CAMEL_HUSK_EAT = std("entity.camel_husk.eat");
   public static final XSound ENTITY_CAMEL_HUSK_HURT = std("entity.camel_husk.hurt");
   public static final XSound ENTITY_CAMEL_HUSK_SADDLE = std("entity.camel_husk.saddle");
   public static final XSound ENTITY_CAMEL_HUSK_SIT = std("entity.camel_husk.sit");
   public static final XSound ENTITY_CAMEL_HUSK_STAND = std("entity.camel_husk.stand");
   public static final XSound ENTITY_CAMEL_HUSK_STEP = std("entity.camel_husk.step");
   public static final XSound ENTITY_CAMEL_HUSK_STEP_SAND = std("entity.camel_husk.step_sand");
   public static final XSound ENTITY_NAUTILUS_AMBIENT = std("entity.nautilus.ambient");
   public static final XSound ENTITY_NAUTILUS_AMBIENT_LAND = std("entity.nautilus.ambient_land");
   public static final XSound ENTITY_NAUTILUS_DASH = std("entity.nautilus.dash");
   public static final XSound ENTITY_NAUTILUS_DASH_LAND = std("entity.nautilus.dash_land");
   public static final XSound ENTITY_NAUTILUS_DASH_READY = std("entity.nautilus.dash_ready");
   public static final XSound ENTITY_NAUTILUS_DASH_READY_LAND = std("entity.nautilus.dash_ready_land");
   public static final XSound ENTITY_NAUTILUS_DEATH = std("entity.nautilus.death");
   public static final XSound ENTITY_NAUTILUS_DEATH_LAND = std("entity.nautilus.death_land");
   public static final XSound ENTITY_NAUTILUS_EAT = std("entity.nautilus.eat");
   public static final XSound ENTITY_NAUTILUS_HURT = std("entity.nautilus.hurt");
   public static final XSound ENTITY_NAUTILUS_HURT_LAND = std("entity.nautilus.hurt_land");
   public static final XSound ENTITY_NAUTILUS_RIDING = std("entity.nautilus.riding");
   public static final XSound ENTITY_NAUTILUS_SWIM = std("entity.nautilus.swim");
   public static final XSound ENTITY_PARCHED_AMBIENT = std("entity.parched.ambient");
   public static final XSound ENTITY_PARCHED_DEATH = std("entity.parched.death");
   public static final XSound ENTITY_PARCHED_HURT = std("entity.parched.hurt");
   public static final XSound ENTITY_PARCHED_STEP = std("entity.parched.step");
   public static final XSound ENTITY_PARROT_IMITATE_CAMEL_HUSK = std("entity.parrot.imitate.camel_husk");
   public static final XSound ENTITY_PARROT_IMITATE_PARCHED = std("entity.parrot.imitate.parched");
   public static final XSound ENTITY_PARROT_IMITATE_ZOMBIE_HORSE = std("entity.parrot.imitate.zombie_horse");
   public static final XSound ENTITY_PARROT_IMITATE_ZOMBIE_NAUTILUS = std("entity.parrot.imitate.zombie_nautilus");
   public static final XSound ENTITY_ZOMBIE_HORSE_ANGRY = std("entity.zombie_horse.angry");
   public static final XSound ENTITY_ZOMBIE_HORSE_EAT = std("entity.zombie_horse.eat");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_AMBIENT = std("entity.zombie_nautilus.ambient");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_AMBIENT_LAND = std("entity.zombie_nautilus.ambient_land");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_DASH = std("entity.zombie_nautilus.dash");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_DASH_LAND = std("entity.zombie_nautilus.dash_land");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_DASH_READY = std("entity.zombie_nautilus.dash_ready");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_DASH_READY_LAND = std("entity.zombie_nautilus.dash_ready_land");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_DEATH = std("entity.zombie_nautilus.death");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_DEATH_LAND = std("entity.zombie_nautilus.death_land");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_EAT = std("entity.zombie_nautilus.eat");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_HURT = std("entity.zombie_nautilus.hurt");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_HURT_LAND = std("entity.zombie_nautilus.hurt_land");
   public static final XSound ENTITY_ZOMBIE_NAUTILUS_SWIM = std("entity.zombie_nautilus.swim");
   public static final XSound ITEM_ARMOR_EQUIP_NAUTILUS = std("item.armor.equip_nautilus");
   public static final XSound ITEM_ARMOR_UNEQUIP_NAUTILUS = std("item.armor.unequip_nautilus");
   public static final XSound ITEM_NAUTILUS_SADDLE_EQUIP = std("item.nautilus_saddle_equip");
   public static final XSound ITEM_NAUTILUS_SADDLE_UNDERWATER_EQUIP = std("item.nautilus_saddle_underwater_equip");
   public static final XSound ITEM_SPEAR_ATTACK = std("item.spear.attack");
   public static final XSound ITEM_SPEAR_HIT = std("item.spear.hit");
   public static final XSound ITEM_SPEAR_LUNGE_1 = std("item.spear.lunge_1");
   public static final XSound ITEM_SPEAR_LUNGE_2 = std("item.spear.lunge_2");
   public static final XSound ITEM_SPEAR_LUNGE_3 = std("item.spear.lunge_3");
   public static final XSound ITEM_SPEAR_USE = std("item.spear.use");
   public static final XSound ITEM_SPEAR_WOOD_ATTACK = std("item.spear_wood.attack");
   public static final XSound ITEM_SPEAR_WOOD_HIT = std("item.spear_wood.hit");
   public static final XSound ITEM_SPEAR_WOOD_USE = std("item.spear_wood.use");
   public static final @Unmodifiable Set<XSound> MUSIC = Collections.unmodifiableSet(
      REGISTRY.nameMapping().values().stream().filter(x -> x.name().toUpperCase(Locale.ENGLISH).startsWith("MUSIC")).collect(Collectors.toSet())
   );
   public static final float DEFAULT_VOLUME = 1.0F;
   public static final float DEFAULT_PITCH = 1.0F;
   public static final Pattern NAMESPACED_SOUND_PATTERN = Pattern.compile("(?<namespace>[a-z0-9._-]+):(?<key>[a-z0-9/._-]+)");
   public static final Pattern RECORD_PATTERN = Pattern.compile(
      "\\s*(?<atLocation>~)?\\s*(?:(?<category>[\\w$_]+)@)?(?<sound>[\\w$_]+|"
         + NAMESPACED_SOUND_PATTERN.pattern()
         + ")\\s*(?:,\\s*(?<volume>[+-]?(?:\\d*\\.)?\\d+)\\s*(?:,\\s*(?<pitch>[+-]?(?:\\d*\\.)?\\d+))?)?\\s*"
   );

   private XSound(Sound sound, String[] names) {
      super(sound, names);
   }

   private static XSound std(String... names) {
      return REGISTRY.std(names);
   }

   @Deprecated
   @NotNull
   public static Optional<XSound> matchXSound(@NotNull String sound) {
      return REGISTRY.getByName(sound);
   }

   @Deprecated
   @NotNull
   public static XSound matchXSound(@NotNull Sound sound) {
      return REGISTRY.getByBukkitForm(sound);
   }

   public static XSound of(@NotNull Sound bukkit) {
      return REGISTRY.getByBukkitForm(bukkit);
   }

   public static Optional<XSound> of(@NotNull String bukkit) {
      return REGISTRY.getByName(bukkit);
   }

   @Deprecated
   public static XSound[] values() {
      return REGISTRY.values();
   }

   @NotNull
   public static @Unmodifiable Collection<XSound> getValues() {
      return REGISTRY.getValues();
   }

   private static List<String> split(@NotNull String str, char separatorChar) {
      List<String> list = new ArrayList<>(4);
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
   public static XSound.Record play(@Nullable String sound, Consumer<XSound.SoundPlayer> soundPlayer) {
      XSound.Record record;
      try {
         record = parse(sound);
      } catch (Throwable var4) {
         return null;
      }

      if (record == null) {
         return null;
      } else {
         XSound.SoundPlayer player = record.soundPlayer();
         soundPlayer.accept(player);
         player.play();
         return record;
      }
   }

   @Nullable
   public static XSound.Record parse(@Nullable String sound) {
      if (!Strings.isNullOrEmpty(sound) && !sound.equalsIgnoreCase("none")) {
         List<String> split = split(sound.replace(" ", ""), ',');
         XSound.Record record = new XSound.Record();
         String name = split.get(0);
         if (name.charAt(0) == '~') {
            name = name.substring(1);
            record.publicSound(true);
         } else {
            record.publicSound(false);
         }

         if (name.isEmpty()) {
            throw new IllegalArgumentException("No sound name specified: " + sound);
         } else {
            int atIndex = name.indexOf(64);
            String soundName;
            if (atIndex != -1) {
               String category = name.substring(0, atIndex);
               soundName = name.substring(atIndex + 1);
               XSound.Category soundCategory = (XSound.Category)Enums.getIfPresent(XSound.Category.class, category.toUpperCase(Locale.ENGLISH)).orNull();
               if (soundCategory == null) {
                  throw new IllegalArgumentException("Unknown sound category '" + category + "' in: " + sound);
               }

               record.inCategory(soundCategory);
            } else {
               soundName = name;
            }

            if (soundName.isEmpty()) {
               throw new IllegalArgumentException("No sound name specified: " + name);
            } else {
               Optional<XSound> soundType = of(soundName);
               if (!soundType.isPresent()) {
                  if (soundName.indexOf(58) == -1) {
                     throw new IllegalArgumentException("Unknown sound: " + name + " -> '" + soundName + '\'');
                  }

                  soundName = soundName.toLowerCase(Locale.ENGLISH);
                  if (!NAMESPACED_SOUND_PATTERN.matcher(soundName).matches()) {
                     throw new IllegalArgumentException("Unknown sound '" + soundName + "', invalid namespace characters: " + name);
                  }

                  record.withSound(soundName);
               } else {
                  record.withSound(soundType.get());
               }

               try {
                  if (split.size() > 1) {
                     record.withVolume(Float.parseFloat(split.get(1)));
                  }
               } catch (NumberFormatException var9) {
                  throw new NumberFormatException("Invalid number '" + split.get(1) + "' for sound volume '" + sound + '\'');
               }

               try {
                  if (split.size() > 2) {
                     record.withPitch(Float.parseFloat(split.get(2)));
                  }
               } catch (NumberFormatException var10) {
                  throw new NumberFormatException("Invalid number '" + split.get(2) + "' for sound pitch '" + sound + '\'');
               }

               try {
                  if (split.size() > 3) {
                     record.withSeed(Long.parseLong(split.get(3)));
                  }

                  return record;
               } catch (NumberFormatException var8) {
                  throw new NumberFormatException("Invalid number '" + split.get(3) + "' for sound seed '" + sound + '\'');
               }
            }
         }
      } else {
         return null;
      }
   }

   public static void stopMusic(@NotNull Player player) {
      Objects.requireNonNull(player, "Cannot stop playing musics from null player");

      for (XSound music : MUSIC) {
         Sound sound = music.get();
         if (sound != null) {
            player.stopSound(sound);
         }
      }
   }

   @Deprecated
   @Nullable
   public Sound parseSound() {
      return this.get();
   }

   public void stopSound(@NotNull Player player) {
      Objects.requireNonNull(player, "Cannot stop playing sound from null player");
      Sound sound = this.get();
      if (sound != null) {
         player.stopSound(sound);
      }
   }

   public void play(@NotNull Entity entity) {
      Objects.requireNonNull(entity, "Cannot play sound for null entity");
      XSound.SoundPlayer soundPlayer = this.record().soundPlayer();
      if (entity instanceof Player) {
         soundPlayer.forPlayers((Player)entity);
      } else if (entity instanceof LivingEntity) {
         soundPlayer.atLocation(((LivingEntity)entity).getEyeLocation());
      } else {
         soundPlayer.atLocation(entity.getLocation());
      }

      soundPlayer.play();
   }

   public void play(@NotNull Location location) {
      Objects.requireNonNull(location, "Cannot play sound at null location");
      this.record().soundPlayer().atLocation(location).play();
   }

   public void play(@NotNull Entity entity, float volume, float pitch) {
      if (!(entity instanceof Player)) {
         Location location;
         if (entity instanceof LivingEntity) {
            location = ((LivingEntity)entity).getEyeLocation();
         } else {
            location = entity.getLocation();
         }

         this.play(location, volume, pitch);
      } else {
         this.record().withVolume(volume).withPitch(pitch).soundPlayer().forPlayers((Player)entity).play();
      }
   }

   public void play(@NotNull Location location, float volume, float pitch) {
      this.record().withVolume(volume).withPitch(pitch).soundPlayer().atLocation(location).play();
   }

   public XSound.Record record() {
      return new XSound.Record().withSound(this);
   }

   static {
      REGISTRY.discardMetadata();
   }

   public static enum Category {
      MASTER,
      MUSIC,
      RECORDS,
      WEATHER,
      BLOCKS,
      HOSTILE,
      NEUTRAL,
      PLAYERS,
      AMBIENT,
      VOICE;

      private final Object bukkitObject;

      public boolean isSupported() {
         return this.bukkitObject != null;
      }

      private static <T> T cast(Object any) {
         return (T)any;
      }

      private Category() {
         Object sc = null;

         try {
            sc = Enums.getIfPresent(cast(Class.forName("org.bukkit.SoundCategory")), this.name()).orNull();
         } catch (ClassNotFoundException var5) {
         }

         this.bukkitObject = sc;
      }

      public Object getBukkitObject() {
         return this.bukkitObject;
      }
   }

   public static final class Record {
      private static final Random RANDOM = new Random();
      private Object sound;
      @NotNull
      private XSound.Category category = XSound.Category.MASTER;
      @Nullable
      private Long seed;
      private float volume = 1.0F;
      private float pitch = 1.0F;
      private boolean publicSound;

      @Nullable
      public Long getSeed() {
         return this.seed;
      }

      public Object std() {
         return this.sound;
      }

      @NotNull
      public XSound.Category getCategory() {
         return this.category;
      }

      public float getVolume() {
         return this.volume;
      }

      public float getPitch() {
         return this.pitch;
      }

      public XSound.Record inCategory(XSound.Category category) {
         this.category = Objects.requireNonNull(category, "Sound category cannot be null");
         return this;
      }

      public XSound.SoundPlayer soundPlayer() {
         return new XSound.SoundPlayer(this);
      }

      public XSound.Record withSound(@NotNull XSound sound) {
         Objects.requireNonNull(sound, "Cannot play a null sound");
         this.sound = sound;
         return this;
      }

      public XSound.Record withSound(@NotNull String sound) {
         Objects.requireNonNull(sound, "Cannot play a null sound");
         sound = sound.toLowerCase(Locale.ENGLISH);
         if (sound.indexOf(58) < 0) {
            throw new IllegalArgumentException("Raw sound name doesn't contain both namespace and key: " + sound);
         } else {
            this.sound = sound;
            return this;
         }
      }

      public long generateSeed() {
         return this.seed == null ? RANDOM.nextLong() : this.seed;
      }

      public XSound.Record withVolume(float volume) {
         this.volume = volume;
         return this;
      }

      public XSound.Record publicSound(boolean publicSound) {
         this.publicSound = publicSound;
         return this;
      }

      public XSound.Record withPitch(float pitch) {
         this.pitch = pitch;
         return this;
      }

      public XSound.Record withSeed(Long seed) {
         this.seed = seed;
         return this;
      }

      public String rebuild() {
         String str = "";
         if (this.publicSound) {
            str = str + "~";
         }

         if (this.category != XSound.Category.MASTER) {
            str = str + this.category.name();
         }

         str = str + this.sound + ", " + this.volume + ", " + this.pitch;
         if (this.seed != null) {
            str = str + ", " + this.seed;
         }

         return str;
      }

      @Override
      public String toString() {
         return "Record{sound="
            + this.sound
            + ", category="
            + this.category
            + ", seed="
            + this.seed
            + ", volume="
            + this.volume
            + ", pitch="
            + this.pitch
            + ", publicSound="
            + this.publicSound
            + '}';
      }

      public XSound.Record copy() {
         XSound.Record record = new XSound.Record();
         record.sound = this.sound;
         record.volume = this.volume;
         record.pitch = this.pitch;
         record.publicSound = this.publicSound;
         record.seed = this.seed;
         return record;
      }
   }

   public static final class SoundPlayer {
      private static final byte SUPPORTED_METHOD_LEVEL;
      public XSound.Record record;
      public Set<UUID> players = new HashSet<>(10);
      public Set<UUID> heard = new HashSet<>();
      @Nullable
      public Location location;

      public SoundPlayer(XSound.Record record) {
         this.withRecord(record);
      }

      public XSound.SoundPlayer withRecord(XSound.Record record) {
         this.record = Objects.requireNonNull(record, "Cannot play a null record");
         return this;
      }

      public XSound.SoundPlayer forPlayers(@Nullable Player... players) {
         this.players.clear();
         if (players != null && players.length > 0) {
            this.players.addAll(Arrays.stream(players).map(Entity::getUniqueId).collect(Collectors.toSet()));
         }

         return this;
      }

      public XSound.SoundPlayer atLocation(@Nullable Location location) {
         this.location = location;
         return this;
      }

      public XSound.SoundPlayer forPlayers(@Nullable Collection<Player> players) {
         this.players.clear();
         this.players.addAll(players.stream().map(Entity::getUniqueId).collect(Collectors.toList()));
         return this;
      }

      public Collection<Player> getHearingPlayers() {
         if (!this.record.publicSound && !this.players.isEmpty()) {
            return toOnlinePlayers(this.players, Collectors.toList());
         } else {
            Location loc;
            if (this.location == null) {
               if (this.players.size() != 1) {
                  throw new IllegalStateException("Cannot play public sound when no location is specified: " + this);
               }

               Player player = Bukkit.getPlayer(this.players.iterator().next());
               if (player == null) {
                  return new ArrayList<>();
               }

               loc = player.getEyeLocation();
            } else {
               loc = this.location;
            }

            return getHearingPlayers(loc, this.record.volume);
         }
      }

      @NotNull
      public static Collection<Player> getHearingPlayers(Location location, double volume) {
         volume = volume > 1.0 ? 16.0 * volume : 16.0;
         double powerVolume = volume * volume;
         List<Player> playersInWorld = location.getWorld().getPlayers();
         List<Player> hearing = new ArrayList<>(playersInWorld.size());
         double x = location.getX();
         double y = location.getY();
         double z = location.getZ();

         for (Player player : playersInWorld) {
            Location loc = player.getLocation();
            double deltaX = x - loc.getX();
            double deltaY = y - loc.getY();
            double deltaZ = z - loc.getZ();
            double length = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (length < powerVolume) {
               hearing.add(player);
            }
         }

         return hearing;
      }

      public void play() {
         Location loc;
         if (this.location == null) {
            if (this.players.size() != 1) {
               throw new IllegalStateException("Cannot play sound when there is no location available");
            }

            UUID first = this.players.iterator().next();
            Player player = Bukkit.getPlayer(first);
            if (player == null) {
               return;
            }

            loc = player.getEyeLocation();
         } else {
            loc = this.location;
         }

         this.play(loc);
      }

      public void play(@NotNull Location updatedLocation) {
         Collection<Player> hearing = this.getHearingPlayers();
         this.heard = hearing.stream().<UUID>map(Entity::getUniqueId).collect(Collectors.toSet());
         if (!hearing.isEmpty()) {
            this.play(hearing, updatedLocation);
         }
      }

      private static <A, R> R toOnlinePlayers(Collection<UUID> players, Collector<Player, A, R> collector) {
         return players.stream().<Player>map(Bukkit::getPlayer).filter(Objects::nonNull).collect(collector);
      }

      public void play(Collection<Player> players, @NotNull Location updatedLocation) {
         Objects.requireNonNull(updatedLocation, "Cannot play sound at null location");
         Sound objSound = this.record.sound instanceof XSound ? ((XSound)this.record.sound).get() : null;
         String strSound = this.record.sound instanceof String ? (String)this.record.sound : null;

         for (Player player : players) {
            switch (SUPPORTED_METHOD_LEVEL) {
               case 1:
                  if (objSound != null) {
                     player.playSound(updatedLocation, objSound, this.record.volume, this.record.pitch);
                  } else {
                     player.playSound(updatedLocation, strSound, this.record.volume, this.record.pitch);
                  }
                  break;
               case 2:
                  if (objSound != null) {
                     player.playSound(updatedLocation, objSound, (SoundCategory)this.record.category.getBukkitObject(), this.record.volume, this.record.pitch);
                  } else {
                     player.playSound(updatedLocation, strSound, (SoundCategory)this.record.category.getBukkitObject(), this.record.volume, this.record.pitch);
                  }
                  break;
               case 3:
                  if (objSound != null) {
                     player.playSound(
                        updatedLocation,
                        objSound,
                        (SoundCategory)this.record.category.getBukkitObject(),
                        this.record.volume,
                        this.record.pitch,
                        this.record.generateSeed()
                     );
                  } else {
                     player.playSound(
                        updatedLocation,
                        strSound,
                        (SoundCategory)this.record.category.getBukkitObject(),
                        this.record.volume,
                        this.record.pitch,
                        this.record.generateSeed()
                     );
                  }
                  break;
               default:
                  throw new IllegalStateException("Unknown format: " + SUPPORTED_METHOD_LEVEL);
            }
         }
      }

      public void stopSound() {
         if (this.heard != null && !this.heard.isEmpty()) {
            List<Player> heardOnline = toOnlinePlayers(this.heard, Collectors.toList());
            heardOnline.forEach(x -> {
               if (this.record.sound instanceof XSound) {
                  x.stopSound(((XSound)this.record.sound).get());
               } else {
                  x.stopSound((String)this.record.sound);
               }
            });
         }
      }

      static {
         byte level;
         try {
            Player.class.getDeclaredMethod("playSound", Location.class, String.class, SoundCategory.class, float.class, float.class, long.class);
            level = 3;
         } catch (Throwable var6) {
            try {
               Player.class.getDeclaredMethod("playSound", Location.class, String.class, SoundCategory.class, float.class, float.class);
               level = 2;
            } catch (Throwable var5) {
               try {
                  Player.class.getDeclaredMethod("playSound", Location.class, Sound.class, float.class, float.class);
                  level = 1;
               } catch (Throwable var4) {
                  throw new UnsupportedOperationException("None of sound methods are supported", var4);
               }
            }
         }

         SUPPORTED_METHOD_LEVEL = level;
      }
   }
}
