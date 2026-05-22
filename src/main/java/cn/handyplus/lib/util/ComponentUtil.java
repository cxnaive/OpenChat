package cn.handyplus.lib.util;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.StrUtil;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Nullable;

public class ComponentUtil {
   private static final String DEFAULT_PREFIX = "<!i>";
   private static final String RESET_ITALIC = "<reset><!i>";
   private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().preProcessor(ComponentUtil::legacyToMiniMessage).build();

   public static Component parseColor(String message) {
      return (Component)(StrUtil.isEmpty(message) ? Component.empty() : MINI_MESSAGE.deserialize(message));
   }

   public static List<Component> parseColor(List<String> messageList) {
      return CollUtil.isEmpty(messageList) ? Collections.emptyList() : messageList.stream().map(ComponentUtil::parseColor).collect(Collectors.toList());
   }

   public static String stripColor(@Nullable String input) {
      return StrUtil.isEmpty(input) ? "" : stripColor(parseColor(input));
   }

   public static String stripColor(@Nullable Component input) {
      return input != null && !Component.empty().equals(input) ? PlainTextComponentSerializer.plainText().serialize(input) : "";
   }

   protected static String legacyToMiniMessage(String legacy) {
      legacy = normalizeLegacyPrefixRuns(legacy);
      StringBuilder builder = new StringBuilder("<!i>");
      char[] chars = legacy.toCharArray();

      for (int i = 0; i < chars.length; i++) {
         if (!isColorCode(chars[i])) {
            builder.append(chars[i]);
         } else if (i + 1 >= chars.length) {
            builder.append(chars[i]);
         } else {
            switch (Character.toLowerCase(chars[i + 1])) {
               case '#':
                  if (i + 7 >= chars.length) {
                     builder.append(chars[i]);
                     continue;
                  }

                  appendResetItalic(builder);
                  builder.append("<#").append(chars, i + 2, 6).append(">");
                  i += 6;
                  break;
               case '$':
               case '%':
               case '&':
               case '\'':
               case '(':
               case ')':
               case '*':
               case '+':
               case ',':
               case '-':
               case '.':
               case '/':
               case ':':
               case ';':
               case '<':
               case '=':
               case '>':
               case '?':
               case '@':
               case 'A':
               case 'B':
               case 'C':
               case 'D':
               case 'E':
               case 'F':
               case 'G':
               case 'H':
               case 'I':
               case 'J':
               case 'K':
               case 'L':
               case 'M':
               case 'N':
               case 'O':
               case 'P':
               case 'Q':
               case 'R':
               case 'S':
               case 'T':
               case 'U':
               case 'V':
               case 'W':
               case 'X':
               case 'Y':
               case 'Z':
               case '[':
               case '\\':
               case ']':
               case '^':
               case '_':
               case '`':
               case 'g':
               case 'h':
               case 'i':
               case 'j':
               case 'p':
               case 'q':
               case 's':
               case 't':
               case 'u':
               case 'v':
               case 'w':
               default:
                  builder.append(chars[i]);
                  if (chars[i + 1] == chars[i]) {
                     i++;
                  }
                  continue;
               case '0':
                  appendResetItalic(builder);
                  builder.append("<black>");
                  break;
               case '1':
                  appendResetItalic(builder);
                  builder.append("<dark_blue>");
                  break;
               case '2':
                  appendResetItalic(builder);
                  builder.append("<dark_green>");
                  break;
               case '3':
                  appendResetItalic(builder);
                  builder.append("<dark_aqua>");
                  break;
               case '4':
                  appendResetItalic(builder);
                  builder.append("<dark_red>");
                  break;
               case '5':
                  appendResetItalic(builder);
                  builder.append("<dark_purple>");
                  break;
               case '6':
                  appendResetItalic(builder);
                  builder.append("<gold>");
                  break;
               case '7':
                  appendResetItalic(builder);
                  builder.append("<gray>");
                  break;
               case '8':
                  appendResetItalic(builder);
                  builder.append("<dark_gray>");
                  break;
               case '9':
                  appendResetItalic(builder);
                  builder.append("<blue>");
                  break;
               case 'a':
                  appendResetItalic(builder);
                  builder.append("<green>");
                  break;
               case 'b':
                  appendResetItalic(builder);
                  builder.append("<aqua>");
                  break;
               case 'c':
                  appendResetItalic(builder);
                  builder.append("<red>");
                  break;
               case 'd':
                  appendResetItalic(builder);
                  builder.append("<light_purple>");
                  break;
               case 'e':
                  appendResetItalic(builder);
                  builder.append("<yellow>");
                  break;
               case 'f':
                  appendResetItalic(builder);
                  builder.append("<white>");
                  break;
               case 'k':
                  builder.append("<obf>");
                  break;
               case 'l':
                  builder.append("<b>");
                  break;
               case 'm':
                  builder.append("<st>");
                  break;
               case 'n':
                  builder.append("<u>");
                  break;
               case 'o':
                  builder.append("<i>");
                  break;
               case 'r':
                  appendResetItalic(builder);
                  break;
               case 'x':
                  if (i + 13 >= chars.length
                     || !isColorCode(chars[i + 2])
                     || !isColorCode(chars[i + 4])
                     || !isColorCode(chars[i + 6])
                     || !isColorCode(chars[i + 8])
                     || !isColorCode(chars[i + 10])
                     || !isColorCode(chars[i + 12])) {
                     builder.append(chars[i]);
                     continue;
                  }

                  appendResetItalic(builder);
                  builder.append("<#")
                     .append(chars[i + 3])
                     .append(chars[i + 5])
                     .append(chars[i + 7])
                     .append(chars[i + 9])
                     .append(chars[i + 11])
                     .append(chars[i + 13])
                     .append(">");
                  i += 12;
            }

            i++;
         }
      }

      return builder.toString();
   }

   private static String normalizeLegacyPrefixRuns(String legacy) {
      if (StrUtil.isEmpty(legacy)) {
         return legacy;
      } else {
         char[] chars = legacy.toCharArray();
         StringBuilder builder = new StringBuilder(chars.length);

         for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            if (!isColorCode(current)) {
               builder.append(current);
            } else {
               int j = i + 1;

               while (j < chars.length && isColorCode(chars[j])) {
                  j++;
               }

               int count = j - i;
               if (count >= 2) {
                  builder.append('&');
                  i = j - 1;
               } else {
                  builder.append(chars, i, count);
                  i = j - 1;
               }
            }
         }

         return builder.toString();
      }
   }

   private static void appendResetItalic(StringBuilder builder) {
      if (builder.length() == "<!i>".length() && "<!i>".contentEquals(builder)) {
         builder.setLength(0);
      }

      builder.append("<reset><!i>");
   }

   private static boolean isColorCode(char c) {
      return c == 167 || c == '&';
   }
}
