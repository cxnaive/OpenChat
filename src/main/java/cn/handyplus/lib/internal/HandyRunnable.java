package cn.handyplus.lib.internal;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public abstract class HandyRunnable implements Runnable {
   private BukkitTask bukkitTask;
   private ScheduledTask scheduledTask;

   public synchronized void cancel() throws IllegalStateException {
      this.checkScheduled();
      if (!HandySchedulerUtil.isFolia()) {
         Bukkit.getScheduler().cancelTask(this.bukkitTask.getTaskId());
      } else {
         this.scheduledTask.cancel();
      }
   }

   private void checkScheduled() {
      if (HandySchedulerUtil.isFolia() && this.scheduledTask == null) {
         throw new IllegalStateException("Not scheduled yet");
      } else if (!HandySchedulerUtil.isFolia() && this.bukkitTask == null) {
         throw new IllegalStateException("Not scheduled yet");
      }
   }

   @NotNull
   public BukkitTask setupTask(@NotNull BukkitTask task) {
      this.bukkitTask = task;
      return task;
   }

   @NotNull
   public ScheduledTask setupTask(@NotNull ScheduledTask task) {
      this.scheduledTask = task;
      return task;
   }
}
