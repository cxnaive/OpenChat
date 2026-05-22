package cn.handyplus.lib.internal;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

class GameRuleHighUtil {
   protected static void setGameRuleValue(@NotNull World world, @NotNull String ruleName, @NotNull Object value) {
      GameRule gameRule = GameRule.getByName(ruleName);
      if (gameRule == null) {
         Bukkit.getLogger().log(Level.SEVERE, "世界规则: " + ruleName + " 不存在.");
      } else {
         Class<?> ruleType = gameRule.getType();
         if (value instanceof Boolean && ruleType == Boolean.class) {
            world.setGameRule(gameRule, (Boolean)value);
         } else if (value instanceof Integer && ruleType == Integer.class) {
            world.setGameRule(gameRule, (Integer)value);
         } else if (value instanceof String && ruleType == String.class) {
            world.setGameRule(gameRule, (String)value);
         } else {
            Bukkit.getLogger().log(Level.SEVERE, "世界规则: " + ruleName + " 类型 " + value + " 不匹配, 预期类型: " + ruleType.getSimpleName());
         }
      }
   }
}
