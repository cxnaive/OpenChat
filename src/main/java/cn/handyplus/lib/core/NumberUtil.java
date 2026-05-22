package cn.handyplus.lib.core;

import cn.handyplus.lib.util.AssertUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.regex.Matcher;
import org.jetbrains.annotations.NotNull;

public final class NumberUtil {
   private NumberUtil() {
   }

   public static Integer isNumericToInt(@NotNull String str) {
      return isNumericToInt(str, null);
   }

   public static Integer isNumericToInt(@NotNull String str, Integer def) {
      return isNumericToBigDecimal(str).map(BigDecimal::intValue).orElse(def);
   }

   public static Double isNumericToDouble(@NotNull String str) {
      return isNumericToDouble(str, null);
   }

   public static Double isNumericToDouble(@NotNull String str, Double def) {
      return isNumericToBigDecimal(str).map(BigDecimal::doubleValue).orElse(def);
   }

   public static Long isNumericToLong(@NotNull String str) {
      return isNumericToLong(str, null);
   }

   public static Long isNumericToLong(@NotNull String str, Long def) {
      return isNumericToBigDecimal(str).map(BigDecimal::longValue).orElse(def);
   }

   public static Optional<BigDecimal> isNumericToBigDecimal(@NotNull String str) {
      return Optional.ofNullable(isNumericToBigDecimal(str, null));
   }

   public static BigDecimal isNumericToBigDecimal(@NotNull String str, BigDecimal def) {
      if (StrUtil.isEmpty(str)) {
         return def;
      } else {
         str = str.trim();

         try {
            Matcher isNum = PatternUtil.BIG_DECIMAL_NUMERIC.matcher(str);
            return isNum.matches() ? new BigDecimal(str) : def;
         } catch (NumberFormatException var3) {
            return def;
         }
      }
   }

   public static BigDecimal mul(@NotNull BigDecimal num1, @NotNull BigDecimal num2) {
      BigDecimal rawResult = num1.multiply(num2);
      int scale = rawResult.scale();
      return scale > 2 ? rawResult.setScale(2, RoundingMode.HALF_UP) : rawResult;
   }

   public static BigDecimal div(@NotNull BigDecimal num1, @NotNull BigDecimal num2) {
      return num1.divide(num2, 2, RoundingMode.HALF_UP);
   }

   public static boolean lt(@NotNull BigDecimal num1, @NotNull BigDecimal num2) {
      return num1.compareTo(num2) < 0;
   }

   public static boolean le(@NotNull BigDecimal num1, @NotNull BigDecimal num2) {
      return num1.compareTo(num2) <= 0;
   }

   public static boolean gt(@NotNull BigDecimal num1, @NotNull BigDecimal num2) {
      return num1.compareTo(num2) > 0;
   }

   public static boolean ge(@NotNull BigDecimal num1, @NotNull BigDecimal num2) {
      return num1.compareTo(num2) >= 0;
   }

   public static BigDecimal toBigDecimal(Number number) {
      if (null == number) {
         return BigDecimal.ZERO;
      } else {
         AssertUtil.isTrue(isValidNumber(number), "Number is invalid!");
         if (number instanceof BigDecimal) {
            return (BigDecimal)number;
         } else if (number instanceof Long) {
            return new BigDecimal((Long)number);
         } else if (number instanceof Integer) {
            return new BigDecimal((Integer)number);
         } else {
            return number instanceof BigInteger ? new BigDecimal((BigInteger)number) : new BigDecimal(number.toString());
         }
      }
   }

   public static boolean isValidNumber(Number number) {
      if (null == number) {
         return false;
      } else if (number instanceof Double) {
         return !((Double)number).isInfinite() && !((Double)number).isNaN();
      } else {
         return !(number instanceof Float) ? true : !((Float)number).isInfinite() && !((Float)number).isNaN();
      }
   }

   @NotNull
   public static String format(Number number) {
      if (number == null) {
         return "0";
      } else {
         BigDecimal bigDecimal = toBigDecimal(number);
         bigDecimal = bigDecimal.setScale(2, RoundingMode.DOWN).stripTrailingZeros();
         boolean hasDecimal = bigDecimal.scale() > 0;
         BigInteger integerPart = bigDecimal.toBigInteger();
         String integerStr = formatIntegerWithComma(integerPart.toString());
         if (!hasDecimal) {
            return integerStr;
         } else {
            BigDecimal decimalPart = bigDecimal.subtract(new BigDecimal(integerPart)).abs();
            String decimalStr = decimalPart.toPlainString();
            if (decimalStr.startsWith("0.")) {
               decimalStr = decimalStr.substring(1);
            }

            return integerStr + decimalStr;
         }
      }
   }

   private static String formatIntegerWithComma(String integerStr) {
      boolean negative = integerStr.startsWith("-");
      if (negative) {
         integerStr = integerStr.substring(1);
      }

      StringBuilder sb = new StringBuilder();
      int len = integerStr.length();

      for (int i = 0; i < len; i++) {
         if (i > 0 && (len - i) % 3 == 0) {
            sb.append(',');
         }

         sb.append(integerStr.charAt(i));
      }

      return negative ? "-" + sb : sb.toString();
   }
}
