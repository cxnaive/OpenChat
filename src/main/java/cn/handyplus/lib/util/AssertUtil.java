package cn.handyplus.lib.util;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import java.math.BigDecimal;
import java.util.Collection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AssertUtil {
   private AssertUtil() {
   }

   public static void isTrue(boolean expression, String message) {
      notTrue(!expression, message);
   }

   public static void notTrue(boolean expression, String message) {
      if (expression) {
         throw new RuntimeException(message);
      }
   }

   public static void notNull(Object object, String message) {
      if (object == null) {
         throw new RuntimeException(message);
      }
   }

   public static void notEmpty(String str, String message) {
      if (StrUtil.isEmpty(str)) {
         throw new RuntimeException(message);
      }
   }

   public static <T> void notNull(Collection<T> collection, String message) {
      if (CollUtil.isEmpty(collection)) {
         throw new RuntimeException(message);
      }
   }

   public static Player notPlayer(CommandSender sender, String message) {
      if (BaseUtil.isNotPlayer(sender)) {
         throw new RuntimeException(message);
      } else {
         return (Player)sender;
      }
   }

   public static Integer isNumericToInt(String str, String message) {
      return NumberUtil.isNumericToBigDecimal(str).orElseThrow(() -> new RuntimeException(message)).intValue();
   }

   public static Long isNumericToLong(String str, String message) {
      return NumberUtil.isNumericToBigDecimal(str).orElseThrow(() -> new RuntimeException(message)).longValue();
   }

   public static BigDecimal isNumericToBigDecimal(String str, String message) {
      return NumberUtil.isNumericToBigDecimal(str).orElseThrow(() -> new RuntimeException(message));
   }

   public static Integer isPositiveToInt(String str, String message) {
      Integer value = isNumericToInt(str, message);
      if (value <= 0) {
         throw new RuntimeException(message);
      } else {
         return value;
      }
   }

   public static Long isPositiveToLong(String str, String message) {
      Long value = isNumericToLong(str, message);
      if (value <= 0L) {
         throw new RuntimeException(message);
      } else {
         return value;
      }
   }

   public static BigDecimal isPositiveToBigDecimal(String str, String message) {
      BigDecimal value = isNumericToBigDecimal(str, message);
      if (value.compareTo(BigDecimal.ZERO) <= 0) {
         throw new RuntimeException(message);
      } else {
         return value;
      }
   }
}
