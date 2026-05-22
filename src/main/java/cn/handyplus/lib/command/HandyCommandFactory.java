package cn.handyplus.lib.command;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.LockUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.internal.HandySchedulerUtil;
import cn.handyplus.lib.util.MessageUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HandyCommandFactory {
   private static final HandyCommandFactory INSTANCE = new HandyCommandFactory();
   private static final Map<String, IHandyCommandEvent> HANDY_COMMAND_EVENT_MAP = new HashMap<>();
   private static HandyTab HANDY_TAB = HandyTab.create();
   private static Map<String, Map<String, HandySubCommandParam>> SUB_COMMAND_MAP = new HashMap<>();

   private HandyCommandFactory() {
   }

   protected static HandyCommandFactory getInstance() {
      return INSTANCE;
   }

   protected void init(List<IHandyCommandEvent> handyCommandEvents) {
      HANDY_COMMAND_EVENT_MAP.clear();
      HANDY_TAB = HandyTab.create();
      if (!CollUtil.isEmpty(handyCommandEvents)) {
         for (IHandyCommandEvent handyCommandEvent : handyCommandEvents) {
            HANDY_COMMAND_EVENT_MAP.put(handyCommandEvent.command().toLowerCase(), handyCommandEvent);
            HANDY_TAB.register(handyCommandEvent);
         }
      }
   }

   protected void initSubCommand(Map<String, Map<String, HandySubCommandParam>> subCommandMap) {
      SUB_COMMAND_MAP = subCommandMap;
   }

   protected boolean onSubCommand(String command, CommandSender sender, Command cmd, String label, String[] args, String noPermission) {
      if (args.length == 0) {
         return false;
      } else {
         Map<String, HandySubCommandParam> subCommandParamMap = SUB_COMMAND_MAP.get(command.toLowerCase());
         if (subCommandParamMap == null) {
            return false;
         } else {
            HandySubCommandParam param = subCommandParamMap.get(args[0].toLowerCase());
            if (param == null) {
               return false;
            } else if (StrUtil.isNotEmpty(param.getPermission()) && !sender.hasPermission(param.getPermission())) {
               MessageUtil.sendMessage(sender, StrUtil.replace(noPermission, "permission", param.getPermission()));
               return true;
            } else if (param.isAsync()) {
               this.onAsyncSubCommandExecution(param, sender, cmd, label, args);
               return true;
            } else {
               this.onSubCommandExecution(param, sender, cmd, label, args);
               return true;
            }
         }
      }
   }

   protected boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, String noPermission) {
      if (args.length == 0) {
         return false;
      } else {
         IHandyCommandEvent handyCommandEvent = HANDY_COMMAND_EVENT_MAP.get(args[0].toLowerCase());
         if (handyCommandEvent == null) {
            return false;
         } else if (StrUtil.isNotEmpty(handyCommandEvent.permission()) && !sender.hasPermission(handyCommandEvent.permission())) {
            MessageUtil.sendMessage(sender, StrUtil.replace(noPermission, "permission", handyCommandEvent.permission()));
            return true;
         } else if (handyCommandEvent.isAsync()) {
            this.onAsyncCommand(handyCommandEvent, sender, cmd, label, args);
            return true;
         } else {
            this.onCommandExecution(handyCommandEvent, sender, cmd, label, args);
            return true;
         }
      }
   }

   protected List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
      return HANDY_TAB.complete(sender, cmd, label, args);
   }

   private void onAsyncSubCommandExecution(HandySubCommandParam param, CommandSender sender, Command cmd, String label, String[] args) {
      HandySchedulerUtil.runTaskAsynchronously(() -> this.onSubCommandExecution(param, sender, cmd, label, args));
   }

   private void onSubCommandExecution(HandySubCommandParam param, CommandSender sender, Command cmd, String label, String[] args) {
      try {
         String name = sender.getName();

         try {
            LockUtil.reentrantLock(name);
            param.getMethod().invoke(param.getAClass().newInstance(), sender, cmd, label, args);
         } catch (RuntimeException var13) {
            MessageUtil.sendMessage(sender, var13.getMessage());
         } catch (Exception var14) {
            InitApi.PLUGIN.getLogger().log(Level.SEVERE, "subCommand exception", (Throwable)var14);
         } finally {
            LockUtil.unReentrantLock(name);
         }
      } catch (Throwable var16) {
         throw new RuntimeException(var16);
      }
   }

   private void onAsyncCommand(IHandyCommandEvent handyCommandEvent, CommandSender sender, Command cmd, String label, String[] args) {
      HandySchedulerUtil.runTaskAsynchronously(() -> this.onCommandExecution(handyCommandEvent, sender, cmd, label, args));
   }

   private void onCommandExecution(IHandyCommandEvent handyCommandEvent, CommandSender sender, Command cmd, String label, String[] args) {
      String name = sender.getName();

      try {
         LockUtil.reentrantLock(name);
         handyCommandEvent.onCommand(sender, cmd, label, args);
      } catch (RuntimeException var12) {
         MessageUtil.sendMessage(sender, var12.getMessage());
      } catch (Exception var13) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "command exception", (Throwable)var13);
      } finally {
         LockUtil.unReentrantLock(name);
      }
   }
}
