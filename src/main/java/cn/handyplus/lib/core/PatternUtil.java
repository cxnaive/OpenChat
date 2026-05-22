package cn.handyplus.lib.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jetbrains.annotations.NotNull;

public final class PatternUtil {
   public static final Pattern VERSION_PATTERN = Pattern.compile("\\(MC: (?<version>\\d+\\.\\d+(\\.\\d+)?)\\)");
   public static final Pattern BIG_DECIMAL_NUMERIC = Pattern.compile("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
   public static final Pattern RPG_PATTERN = Pattern.compile("[&§]#[0-9a-fA-F]{3}(?:[0-9a-fA-F]{3})?");
   public static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");
   public static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
   public static final Pattern TEMPLATE_EXPRESSION_REGEX = Pattern.compile("\\$\\{(.*?)}");
   public static final Pattern AT_WORD_PATTERN = Pattern.compile("@(\\w+)");
   public static final Pattern HTTP_CHARSET = Pattern.compile("charset=([\\w\\-]+)", 2);
   public static final Pattern DIGITS_ONLY = Pattern.compile("\\d+");
   public static final Pattern TIME_WITH_UNIT = Pattern.compile("(\\d+)([smhdwMy])");

   private PatternUtil() {
   }

   public static String replaceFirst(String input, String str) {
      return input.replaceFirst("(?i)" + Pattern.quote(str), "").trim();
   }

   public static String replaceAll(String input, String str) {
      return input.replaceAll("(?i)" + Pattern.quote(str), "").trim();
   }

   public static List<String> extractAtTags(String input) {
      List<String> list = new ArrayList<>();
      Matcher matcher = AT_WORD_PATTERN.matcher(input);

      while (matcher.find()) {
         list.add(matcher.group(1));
      }

      return list;
   }

   public static boolean isMatch(@NotNull String regex, String input) {
      if (StrUtil.isEmpty(input)) {
         return false;
      } else {
         try {
            return Pattern.matches(regex, input);
         } catch (PatternSyntaxException var3) {
            return false;
         }
      }
   }
}
