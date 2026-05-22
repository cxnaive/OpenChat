package cn.handyplus.lib.internal;

import org.bukkit.Bukkit;

class BukkitScheduler {
   private BukkitScheduler() {
   }

   protected static void runTask(Runnable task) {
      Bukkit.getScheduler().runTask(HandySchedulerUtil.BUKKIT_PLUGIN, task);
   }

   protected static void runTaskLater(Runnable task, long delay) {
      Bukkit.getScheduler().runTaskLater(HandySchedulerUtil.BUKKIT_PLUGIN, task, delay);
   }

   protected static void runTaskLater(HandyRunnable task, long delay) {
      task.setupTask(Bukkit.getScheduler().runTaskLater(HandySchedulerUtil.BUKKIT_PLUGIN, task, delay));
   }

   protected static void runTaskTimer(Runnable task, long delay, long period) {
      Bukkit.getScheduler().runTaskTimer(HandySchedulerUtil.BUKKIT_PLUGIN, task, delay, period);
   }

   protected static void runTaskTimer(HandyRunnable task, long delay, long period) {
      task.setupTask(Bukkit.getScheduler().runTaskTimer(HandySchedulerUtil.BUKKIT_PLUGIN, task, delay, period));
   }

   protected static void runTaskAsynchronously(Runnable task) {
      Bukkit.getScheduler().runTaskAsynchronously(HandySchedulerUtil.BUKKIT_PLUGIN, task);
   }

   protected static void runTaskLaterAsynchronously(Runnable task, long delay) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(HandySchedulerUtil.BUKKIT_PLUGIN, task, delay);
   }

   protected static void runTaskLaterAsynchronously(HandyRunnable task, long delay) {
      task.setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(HandySchedulerUtil.BUKKIT_PLUGIN, task, delay));
   }

   protected static void runTaskTimerAsynchronously(Runnable task, long delay, long period) {
      Bukkit.getScheduler().runTaskTimerAsynchronously(HandySchedulerUtil.BUKKIT_PLUGIN, task, delay, period);
   }

   protected static void runTaskTimerAsynchronously(HandyRunnable task, long delay, long period) {
      task.setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(HandySchedulerUtil.BUKKIT_PLUGIN, task, delay, period));
   }

   protected static void cancelTask() {
      Bukkit.getScheduler().cancelTasks(HandySchedulerUtil.BUKKIT_PLUGIN);
   }
}
