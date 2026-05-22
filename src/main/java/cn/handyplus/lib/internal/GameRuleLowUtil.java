package cn.handyplus.lib.internal;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

class GameRuleLowUtil {
   protected static void setGameRuleValue(@NotNull World world, @NotNull String ruleName, @NotNull Object value) {
      world.setGameRuleValue(ruleName, String.valueOf(value));
   }
}
