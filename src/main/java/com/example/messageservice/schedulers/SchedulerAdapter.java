package com.example.messageservice.schedulers;

import cn.handyplus.chat.PlayerChat;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.example.messageservice.MessageServicePlugin;

import java.util.concurrent.TimeUnit;

/**
 * Folia 调度器适配器
 * 所有调度器调用使用 PlayerChat.INSTANCE 作为真实插件实例
 */
public class SchedulerAdapter {

    private final MessageServicePlugin plugin;

    public SchedulerAdapter(MessageServicePlugin plugin) {
        this.plugin = plugin;
    }

    public void runOnPlayer(Player player, Runnable task) {
        if (player == null || !player.isOnline()) {
            return;
        }
        player.getScheduler().execute(PlayerChat.INSTANCE, task, null, 0);
    }

    public void runOnLocation(Location location, Runnable task) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        Bukkit.getRegionScheduler().execute(PlayerChat.INSTANCE, location, task);
    }

    public void runOnGlobal(Runnable task) {
        Bukkit.getGlobalRegionScheduler().execute(PlayerChat.INSTANCE, task);
    }

    public void runAsync(Runnable task) {
        Bukkit.getAsyncScheduler().runNow(PlayerChat.INSTANCE, scheduledTask -> task.run());
    }

    public ScheduledTask runDelayedOnPlayer(Player player, Runnable task, long delayTicks) {
        if (player == null || !player.isOnline()) {
            return null;
        }
        return player.getScheduler().runDelayed(PlayerChat.INSTANCE, scheduledTask -> task.run(), null, delayTicks);
    }

    public ScheduledTask runDelayedOnLocation(Location location, Runnable task, long delayTicks) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        return Bukkit.getRegionScheduler().runDelayed(PlayerChat.INSTANCE, location, scheduledTask -> task.run(), delayTicks);
    }

    public ScheduledTask runDelayedOnGlobal(Runnable task, long delayTicks) {
        return Bukkit.getGlobalRegionScheduler().runDelayed(PlayerChat.INSTANCE, scheduledTask -> task.run(), delayTicks);
    }

    public ScheduledTask runDelayedAsync(Runnable task, long delayMillis) {
        return Bukkit.getAsyncScheduler().runDelayed(PlayerChat.INSTANCE, scheduledTask -> task.run(), delayMillis, TimeUnit.MILLISECONDS);
    }

    public ScheduledTask runTimerOnPlayer(Player player, Runnable task, long delayTicks, long periodTicks) {
        if (player == null || !player.isOnline()) {
            return null;
        }
        return player.getScheduler().runAtFixedRate(PlayerChat.INSTANCE, scheduledTask -> task.run(), null, delayTicks, periodTicks);
    }

    public ScheduledTask runTimerOnLocation(Location location, Runnable task, long delayTicks, long periodTicks) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        return Bukkit.getRegionScheduler().runAtFixedRate(PlayerChat.INSTANCE, location, scheduledTask -> task.run(), delayTicks, periodTicks);
    }

    public ScheduledTask runTimerOnGlobal(Runnable task, long delayTicks, long periodTicks) {
        return Bukkit.getGlobalRegionScheduler().runAtFixedRate(PlayerChat.INSTANCE, scheduledTask -> task.run(), delayTicks, periodTicks);
    }

    public ScheduledTask runTimerAsync(Runnable task, long delayMillis, long periodMillis) {
        return Bukkit.getAsyncScheduler().runAtFixedRate(PlayerChat.INSTANCE, scheduledTask -> task.run(), delayMillis, periodMillis, TimeUnit.MILLISECONDS);
    }

    public void cancelAllTasks() {
        // Folia 中任务会在插件禁用时自动取消
    }

    public boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
