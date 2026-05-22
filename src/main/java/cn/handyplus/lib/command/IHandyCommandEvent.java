package cn.handyplus.lib.command;

import java.util.Optional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface IHandyCommandEvent {
   String command();

   String permission();

   void onCommand(CommandSender var1, Command var2, String var3, String[] var4);

   default boolean isAsync() {
      return false;
   }

   default void tab(HandyTab tab) {
   }

   default Optional<String> getArg(String[] args, int index) {
      return args.length > index ? Optional.of(args[index]) : Optional.empty();
   }

   default String getArg(String[] args, int index, String exception) {
      return this.getArg(args, index).orElseThrow(() -> new RuntimeException(exception));
   }
}
