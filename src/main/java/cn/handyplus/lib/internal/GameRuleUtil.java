package cn.handyplus.lib.internal;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class GameRuleUtil {
   public static void setGameRuleValue(@NotNull World world, @NotNull String ruleName, @NotNull Object value) {
      if (CompatCore.IS_1_13) {
         GameRuleHighUtil.setGameRuleValue(world, ruleName, value);
      } else {
         GameRuleLowUtil.setGameRuleValue(world, ruleName, value);
      }
   }
}
