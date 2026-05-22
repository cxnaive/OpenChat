package cn.handyplus.lib.internal;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;

class FoliaScheduler {
   private FoliaScheduler() {
   }

   protected static void runTask(Runnable task) {
      Bukkit.getGlobalRegionScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run());
   }

   protected static void runTaskLater(Runnable task, long delay) {
      delay = getOneIfNotPositive(delay);
      Bukkit.getGlobalRegionScheduler().runDelayed(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run(), delay);
   }

   protected static void runTaskLater(HandyRunnable task, long delay) {
      delay = getOneIfNotPositive(delay);
      task.setupTask(Bukkit.getGlobalRegionScheduler().runDelayed(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run(), delay));
   }

   protected static void runTaskTimer(Runnable task, long delay, long period) {
      delay = getOneIfNotPositive(delay);
      Bukkit.getGlobalRegionScheduler().runAtFixedRate(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run(), delay, period);
   }

   protected static void runTaskTimer(HandyRunnable task, long delay, long period) {
      delay = getOneIfNotPositive(delay);
      task.setupTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run(), delay, period));
   }

   protected static void runTaskAsynchronously(Runnable task) {
      Bukkit.getAsyncScheduler().runNow(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run());
   }

   protected static void runTaskLaterAsynchronously(Runnable task, long delay) {
      delay = getOneIfNotPositive(delay);
      Bukkit.getAsyncScheduler().runDelayed(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run(), delay * 50L, TimeUnit.MILLISECONDS);
   }

   protected static void runTaskLaterAsynchronously(HandyRunnable task, long delay) {
      delay = getOneIfNotPositive(delay);
      task.setupTask(Bukkit.getAsyncScheduler().runDelayed(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run(), delay * 50L, TimeUnit.MILLISECONDS));
   }

   protected static void runTaskTimerAsynchronously(Runnable task, long delay, long period) {
      delay = getOneIfNotPositive(delay);
      Bukkit.getAsyncScheduler().runAtFixedRate(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS);
   }

   protected static void runTaskTimerAsynchronously(HandyRunnable task, long delay, long period) {
      delay = getOneIfNotPositive(delay);
      task.setupTask(
         Bukkit.getAsyncScheduler().runAtFixedRate(HandySchedulerUtil.BUKKIT_PLUGIN, a -> task.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS)
      );
   }

   protected static void cancelTask() {
      Bukkit.getGlobalRegionScheduler().cancelTasks(HandySchedulerUtil.BUKKIT_PLUGIN);
      Bukkit.getAsyncScheduler().cancelTasks(HandySchedulerUtil.BUKKIT_PLUGIN);
   }

   private static long getOneIfNotPositive(long delay) {
      return delay <= 0L ? 1L : delay;
   }
}
