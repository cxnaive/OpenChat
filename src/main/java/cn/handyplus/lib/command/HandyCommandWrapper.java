package cn.handyplus.lib.command;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.annotation.HandySubCommand;
import cn.handyplus.lib.core.ClassUtil;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.permissions.DefaultPermissions;
import org.jetbrains.annotations.NotNull;

public final class HandyCommandWrapper {
   private HandyCommandWrapper() {
   }

   public static void initCommand(String packageName) {
      try {
         List<Class<?>> commandList = ClassUtil.getInstance().getClassByAnnotation(packageName, HandyCommand.class);
         if (CollUtil.isNotEmpty(commandList)) {
            for (Class<?> aClass : commandList) {
               HandyCommand handyCommand = aClass.getAnnotation(HandyCommand.class);
               PluginCommand pluginCommand = Bukkit.getPluginCommand(handyCommand.name());
               if (pluginCommand != null) {
                  Object instance = aClass.newInstance();
                  if (instance instanceof CommandExecutor) {
                     pluginCommand.setExecutor((CommandExecutor)instance);
                  }

                  if (instance instanceof TabExecutor) {
                     pluginCommand.setTabCompleter((TabExecutor)instance);
                  }

                  if (handyCommand.aliases().length > 0) {
                     pluginCommand.setAliases(Arrays.asList(handyCommand.aliases()));
                  }

                  pluginCommand.setDescription(handyCommand.description());
                  pluginCommand.setUsage(handyCommand.usage());
                  pluginCommand.setPermissionMessage(handyCommand.permissionMessage());
                  if (StrUtil.isNotEmpty(handyCommand.permission())) {
                     pluginCommand.setPermission(handyCommand.permission());
                     DefaultPermissions.registerPermission(handyCommand.permission(), null, handyCommand.PERMISSION_DEFAULT());
                  }
               }
            }
         }

         List<Class<IHandyCommandEvent>> handyCommandEventList = ClassUtil.getInstance().getClassByIsAssignableFrom(packageName, IHandyCommandEvent.class);
         if (CollUtil.isNotEmpty(handyCommandEventList)) {
            List<IHandyCommandEvent> handyCommandEvents = new ArrayList<>();

            for (Class<?> aClassx : handyCommandEventList) {
               handyCommandEvents.add((IHandyCommandEvent)aClassx.newInstance());
            }

            HandyCommandFactory.getInstance().init(handyCommandEvents);
         }

         Map<Class<?>, List<Method>> methodsMap = ClassUtil.getInstance().getMethodByAnnotation(packageName, HandySubCommand.class);
         if (!methodsMap.isEmpty()) {
            List<HandySubCommandParam> subCommandParamList = new ArrayList<>();

            for (Class<?> aClassx : methodsMap.keySet()) {
               for (Method method : methodsMap.get(aClassx)) {
                  subCommandParamList.add(getHandySubCommandParam(aClassx, method));
               }
            }

            Map<String, Map<String, HandySubCommandParam>> subCommandMap = subCommandParamList.stream()
               .collect(
                  Collectors.groupingBy(
                     HandySubCommandParam::getCommand,
                     Collectors.groupingBy(HandySubCommandParam::getSubCommand, Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0)))
                  )
               );
            HandyCommandFactory.getInstance().initSubCommand(subCommandMap);
         }
      } catch (Throwable var9) {
         throw new RuntimeException(var9);
      }
   }

   public static boolean onSubCommand(String command, CommandSender sender, Command cmd, String label, String[] args, String noPermission) {
      return HandyCommandFactory.getInstance().onSubCommand(command, sender, cmd, label, args, noPermission);
   }

   public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, String noPermission) {
      return HandyCommandFactory.getInstance().onCommand(sender, cmd, label, args, noPermission);
   }

   public static List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
      return HandyCommandFactory.getInstance().onTabComplete(sender, cmd, label, args);
   }

   public static void injectCommand(String cmd) {
      injectCommand(Collections.singletonList(cmd));
   }

   public static void injectCommand(List<String> cmdList) {
      try {
         Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         bukkitCommandMap.setAccessible(true);
         CommandMap commandMap = (CommandMap)bukkitCommandMap.get(Bukkit.getServer());

         for (String cmd : cmdList) {
            Command myCommand = new Command(cmd) {
               public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                  return true;
               }
            };
            commandMap.register(InitApi.PLUGIN.getDescription().getName(), myCommand);
         }
      } catch (Throwable var6) {
         throw new RuntimeException(var6);
      }
   }

   private static HandySubCommandParam getHandySubCommandParam(Class<?> aClass, Method method) {
      HandySubCommand handySubCommand = method.getAnnotation(HandySubCommand.class);
      HandySubCommandParam param = new HandySubCommandParam();
      param.setCommand(handySubCommand.mainCommand().toLowerCase().trim());
      param.setSubCommand(handySubCommand.subCommand().toLowerCase().trim());
      param.setPermission(handySubCommand.permission().trim());
      param.setAClass(aClass);
      param.setMethod(method);
      return param;
   }
}
