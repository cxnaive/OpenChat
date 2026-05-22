package cn.handyplus.lib.internal;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class HandySchedulerUtil {
   protected static Plugin BUKKIT_PLUGIN;
   private static ServerTypeEnum SERVER_TYPE;

   private HandySchedulerUtil() {
   }

   public static void init(@NotNull Plugin plugin) {
      BUKKIT_PLUGIN = plugin;
      SERVER_TYPE = ServerTypeEnum.getServerType();
   }

   public static void runTask(@NotNull Runnable task) {
      if (isFolia()) {
         FoliaScheduler.runTask(task);
      } else {
         BukkitScheduler.runTask(task);
      }
   }

   public static void runTaskLater(@NotNull Runnable task, long delay) {
      if (isFolia()) {
         FoliaScheduler.runTaskLater(task, delay);
      } else {
         BukkitScheduler.runTaskLater(task, delay);
      }
   }

   public static void runTaskLater(@NotNull HandyRunnable task, long delay) {
      if (isFolia()) {
         FoliaScheduler.runTaskLater(task, delay);
      } else {
         BukkitScheduler.runTaskLater(task, delay);
      }
   }

   public static void runTaskTimer(@NotNull Runnable task, long delay, long period) {
      if (isFolia()) {
         FoliaScheduler.runTaskTimer(task, delay, period);
      } else {
         BukkitScheduler.runTaskTimer(task, delay, period);
      }
   }

   public static void runTaskTimer(@NotNull HandyRunnable task, long delay, long period) {
      if (isFolia()) {
         FoliaScheduler.runTaskTimer(task, delay, period);
      } else {
         BukkitScheduler.runTaskTimer(task, delay, period);
      }
   }

   public static void runTaskAsynchronously(@NotNull Runnable task) {
      if (isFolia()) {
         FoliaScheduler.runTaskAsynchronously(task);
      } else {
         BukkitScheduler.runTaskAsynchronously(task);
      }
   }

   public static void runTaskLaterAsynchronously(@NotNull Runnable task, long delay) {
      if (isFolia()) {
         FoliaScheduler.runTaskLaterAsynchronously(task, delay);
      } else {
         BukkitScheduler.runTaskLaterAsynchronously(task, delay);
      }
   }

   public static void runTaskLaterAsynchronously(@NotNull HandyRunnable task, long delay) {
      if (isFolia()) {
         FoliaScheduler.runTaskLaterAsynchronously(task, delay);
      } else {
         BukkitScheduler.runTaskLaterAsynchronously(task, delay);
      }
   }

   public static void runTaskTimerAsynchronously(@NotNull Runnable task, long delay, long period) {
      if (isFolia()) {
         FoliaScheduler.runTaskTimerAsynchronously(task, delay, period);
      } else {
         BukkitScheduler.runTaskTimerAsynchronously(task, delay, period);
      }
   }

   public static void runTaskTimerAsynchronously(@NotNull HandyRunnable task, long delay, long period) {
      if (isFolia()) {
         FoliaScheduler.runTaskTimerAsynchronously(task, delay, period);
      } else {
         BukkitScheduler.runTaskTimerAsynchronously(task, delay, period);
      }
   }

   public static void cancelTask() {
      if (isFolia()) {
         FoliaScheduler.cancelTask();
      } else {
         BukkitScheduler.cancelTask();
      }
   }

   public static boolean isFolia() {
      return ServerTypeEnum.FOLIA.equals(SERVER_TYPE);
   }

   public static boolean isPaper() {
      return ServerTypeEnum.PAPER.equals(SERVER_TYPE);
   }
}
