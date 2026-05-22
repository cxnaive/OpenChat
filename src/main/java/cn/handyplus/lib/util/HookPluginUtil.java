package cn.handyplus.lib.util;

import cn.handyplus.lib.constants.HookPluginEnum;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HookPluginUtil {
   public static boolean hook(@NotNull HookPluginEnum hookPluginEnum) {
      return hook(hookPluginEnum, null);
   }

   public static boolean hook(@NotNull HookPluginEnum hookPluginEnum, @Nullable Integer version) {
      return hook(hookPluginEnum, version, true);
   }

   public static boolean hook(@NotNull HookPluginEnum hookPluginEnum, @Nullable Integer version, boolean isMsg) {
      return hookToPlugin(hookPluginEnum, version, isMsg).isPresent();
   }

   public static Optional<Plugin> hookToPlugin(@NotNull HookPluginEnum hookPluginEnum) {
      return hookToPlugin(hookPluginEnum, null, true);
   }

   public static Optional<Plugin> hookToPlugin(@NotNull HookPluginEnum hookPluginEnum, @Nullable Integer version) {
      return hookToPlugin(hookPluginEnum, version, true);
   }

   public static Optional<Plugin> hookToPlugin(@NotNull HookPluginEnum hookPluginEnum, @Nullable Integer version, boolean isMsg) {
      Plugin plugin = Bukkit.getPluginManager().getPlugin(hookPluginEnum.getName());
      Optional<Plugin> pluginOpt = plugin != null && plugin.isEnabled() ? Optional.of(plugin) : Optional.empty();
      if (pluginOpt.isPresent() && version != null) {
         int firstPluginVersion = BaseUtil.getFirstPluginVersion(pluginOpt.get());
         if (firstPluginVersion < version) {
            pluginOpt = Optional.empty();
         }
      }

      if (isMsg) {
         MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg(pluginOpt.isPresent() ? hookPluginEnum.getSuccessMsg() : hookPluginEnum.getFailMsg()));
      }

      return pluginOpt;
   }
}
