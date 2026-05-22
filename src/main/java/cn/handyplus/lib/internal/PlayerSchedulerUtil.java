package cn.handyplus.lib.internal;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PlayerSchedulerUtil {
   private PlayerSchedulerUtil() {
   }

   public static boolean teleport(@NotNull Entity entity, @NotNull Location target) {
      return teleport(entity, target, TeleportCause.PLUGIN);
   }

   public static boolean teleport(@NotNull Entity entity, @NotNull Location target, @NotNull TeleportCause cause) {
      if (HandySchedulerUtil.isFolia()) {
         entity.teleportAsync(target, cause);
         return true;
      } else {
         return entity.teleport(target, cause);
      }
   }

   public static void syncTeleport(@NotNull Entity entity, @NotNull Location target) {
      syncTeleport(entity, target, TeleportCause.PLUGIN);
   }

   public static void syncTeleport(@NotNull Entity entity, @NotNull Location target, @NotNull TeleportCause cause) {
      if (HandySchedulerUtil.isFolia()) {
         entity.teleportAsync(target, cause);
      } else {
         BukkitScheduler.runTask(() -> entity.teleport(target, cause));
      }
   }

   public static void addPotionEffects(@NotNull LivingEntity entity, @NotNull List<PotionEffect> potionEffectList) {
      if (!potionEffectList.isEmpty()) {
         if (HandySchedulerUtil.isFolia()) {
            entity.getScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> entity.addPotionEffects(potionEffectList), () -> {});
         } else {
            BukkitScheduler.runTask(() -> entity.addPotionEffects(potionEffectList));
         }
      }
   }

   public static void addPotionEffects(@NotNull LivingEntity entity, @NotNull PotionEffect potionEffect) {
      addPotionEffects(entity, Collections.singletonList(potionEffect));
   }

   public static void removePotionEffect(@NotNull LivingEntity entity, @NotNull PotionEffectType potionEffect) {
      if (HandySchedulerUtil.isFolia()) {
         entity.getScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> entity.removePotionEffect(potionEffect), () -> {});
      } else {
         BukkitScheduler.runTask(() -> entity.removePotionEffect(potionEffect));
      }
   }

   public static void playSound(@NotNull Player player, @NotNull Sound sound, float volume, float pitch) {
      if (HandySchedulerUtil.isFolia()) {
         player.getScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> player.playSound(player.getLocation(), sound, volume, pitch), () -> {});
      } else {
         BukkitScheduler.runTask(() -> player.playSound(player.getLocation(), sound, volume, pitch));
      }
   }

   public static void playSound(@NotNull Player player, @NotNull String sound, float volume, float pitch) {
      if (HandySchedulerUtil.isFolia()) {
         player.getScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> player.playSound(player.getLocation(), sound, volume, pitch), () -> {});
      } else {
         BukkitScheduler.runTask(() -> player.playSound(player.getLocation(), sound, volume, pitch));
      }
   }

   public static void performCommand(@NotNull Player player, @NotNull String command) {
      performCommand(player, command, true, false);
   }

   public static void syncPerformCommand(@NotNull Player player, @NotNull String command) {
      performCommand(player, command, true, true);
   }

   public static void playerPerformCommand(@NotNull Player player, @NotNull String command) {
      performCommand(player, command, false, false);
   }

   public static void syncPlayerPerformCommand(@NotNull Player player, @NotNull String command) {
      performCommand(player, command, false, true);
   }

   public static void openInventory(@NotNull Player player, @NotNull Inventory inventory) {
      openInventory(player, inventory, false);
   }

   public static void syncOpenInventory(@NotNull Player player, @NotNull Inventory inventory) {
      openInventory(player, inventory, true);
   }

   public static void closeInventory(@NotNull Player player) {
      closeInventory(player, false);
   }

   public static void syncCloseInventory(@NotNull Player player) {
      closeInventory(player, true);
   }

   public static void performOpCommand(@NotNull Player player, @NotNull String command) {
      opPerformCommand(player, command, true, false);
   }

   public static void syncPerformOpCommand(@NotNull Player player, @NotNull String command) {
      opPerformCommand(player, command, true, true);
   }

   public static void playerPerformOpCommand(@NotNull Player player, @NotNull String command) {
      opPerformCommand(player, command, false, false);
   }

   public static void syncPlayerPerformOpCommand(@NotNull Player player, @NotNull String command) {
      opPerformCommand(player, command, false, true);
   }

   public static void dispatchCommand(@NotNull String command) {
      dispatchCommand(command, false);
   }

   public static void syncDispatchCommand(@NotNull String command) {
      dispatchCommand(command, true);
   }

   public static void syncPerformReplaceCommand(@NotNull Player player, @NotNull String command) {
      if (command.contains("[close]")) {
         syncCloseInventory(player);
      } else {
         command = command.replace("${player}", player.getName());
         if (command.contains("[op]")) {
            String newCommand = command.replace("[op]", "");
            syncPerformOpCommand(player, newCommand);
         } else if (command.contains("[console]")) {
            syncDispatchCommand(command.replace("[console]", ""));
         } else {
            syncPerformCommand(player, command);
         }
      }
   }

   public static void dropItem(@NotNull Player player, @NotNull List<ItemStack> dropItemList) {
      if (HandySchedulerUtil.isFolia()) {
         player.getScheduler()
            .run(
               HandySchedulerUtil.BUKKIT_PLUGIN, task -> dropItemList.forEach(dropItem -> player.getWorld().dropItem(player.getLocation(), dropItem)), () -> {}
            );
      } else {
         HandySchedulerUtil.runTask(() -> dropItemList.forEach(item -> player.getWorld().dropItem(player.getLocation(), item)));
      }
   }

   public static void dropItem(@NotNull Player player, @NotNull List<ItemStack> dropItemList, long delay) {
      if (HandySchedulerUtil.isFolia()) {
         player.getScheduler()
            .runDelayed(
               HandySchedulerUtil.BUKKIT_PLUGIN,
               task -> dropItemList.forEach(dropItem -> player.getWorld().dropItem(player.getLocation(), dropItem)),
               () -> {},
               delay
            );
      } else {
         HandySchedulerUtil.runTaskLater(() -> dropItemList.forEach(item -> player.getWorld().dropItem(player.getLocation(), item)), delay);
      }
   }

   private static void performCommand(@NotNull Player player, @NotNull String command, boolean isChat, boolean isSync) {
      if (HandySchedulerUtil.isFolia()) {
         player.getScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> performCommand(player, command, isChat), () -> {});
      } else if (isSync) {
         BukkitScheduler.runTask(() -> performCommand(player, command, isChat));
      } else {
         performCommand(player, command, isChat);
      }
   }

   private static synchronized void opPerformCommand(@NotNull Player player, @NotNull String command, boolean isChat, boolean isSync) {
      boolean op = player.isOp();

      try {
         if (!op) {
            player.setOp(true);
         }

         performCommand(player, command, isChat, isSync);
      } finally {
         player.setOp(op);
      }
   }

   private static void performCommand(@NotNull Player player, @NotNull String command, boolean isChat) {
      if (isChat) {
         player.chat("/" + command.trim());
      } else {
         player.performCommand(command.trim());
      }
   }

   private static void dispatchCommand(@NotNull String command, boolean isSync) {
      if (HandySchedulerUtil.isFolia()) {
         HandySchedulerUtil.runTask(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.trim()));
      } else if (isSync) {
         HandySchedulerUtil.runTask(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.trim()));
      } else {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.trim());
      }
   }

   private static void openInventory(@NotNull Player player, @NotNull Inventory inventory, boolean isSync) {
      if (HandySchedulerUtil.isFolia()) {
         player.getScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> player.openInventory(inventory), () -> {});
      } else if (isSync) {
         BukkitScheduler.runTask(() -> player.openInventory(inventory));
      } else {
         player.openInventory(inventory);
      }
   }

   private static void closeInventory(@NotNull Player player, boolean isSync) {
      if (HandySchedulerUtil.isFolia()) {
         player.getScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> player.closeInventory(), () -> {});
      } else if (isSync) {
         BukkitScheduler.runTask(player::closeInventory);
      } else {
         player.closeInventory();
      }
   }
}
