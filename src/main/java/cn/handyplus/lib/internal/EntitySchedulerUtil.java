package cn.handyplus.lib.internal;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class EntitySchedulerUtil {
   private EntitySchedulerUtil() {
   }

   public static void runSafeOnPlayerScheduler(@NotNull LivingEntity entity, @NotNull Runnable task) {
      runSafeOnPlayerScheduler(entity, task, true);
   }

   public static void runSafeOnPlayerScheduler(@NotNull LivingEntity entity, @NotNull Runnable task, boolean isSync) {
      runSafeOnPlayerScheduler(entity, () -> {
         task.run();
         return null;
      }, isSync);
   }

   public static <T> void runSafeOnPlayerScheduler(@NotNull LivingEntity entity, @NotNull Supplier<T> task, boolean isSync) {
      runSafeOnPlayerScheduler(entity, task, null, isSync);
   }

   public static <T> void runSafeOnPlayerScheduler(@NotNull LivingEntity entity, @NotNull Supplier<T> task, @Nullable Consumer<T> success, boolean isSync) {
      Runnable runner = () -> {
         T result = task.get();
         if (success != null) {
            success.accept(result);
         }
      };
      if (HandySchedulerUtil.isFolia()) {
         entity.getScheduler().run(HandySchedulerUtil.BUKKIT_PLUGIN, a -> runner.run(), () -> {});
      } else if (isSync) {
         BukkitScheduler.runTask(runner);
      } else {
         runner.run();
      }
   }
}
