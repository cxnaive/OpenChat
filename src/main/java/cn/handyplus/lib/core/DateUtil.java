package cn.handyplus.lib.core;

import cn.handyplus.lib.InitApi;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DateUtil {
   private static final int DATE_FORMAT_CACHE_MAX_SIZE = 64;
   private static final ConcurrentHashMap<String, ThreadLocal<SimpleDateFormat>> DATE_FORMAT_CACHE = new ConcurrentHashMap<>();
   public static final String YYYY = "yyyy-MM-dd";
   public static final String YYYY_HH = "yyyy-MM-dd HH:mm:ss";
   public static final String HH_MM = "HH:mm";
   public static final String ZERO = "00:00";

   private DateUtil() {
   }

   public static Date parse(@NotNull String str, @NotNull String format) {
      SimpleDateFormat sdf = getSimpleDateFormat(format);

      try {
         return sdf.parse(str);
      } catch (ParseException var4) {
         InitApi.PLUGIN.getLogger().log(Level.SEVERE, "parse 发生异常", (Throwable)var4);
         return null;
      }
   }

   public static String format(@NotNull Date date, @NotNull String format) {
      SimpleDateFormat sdf = getSimpleDateFormat(format);
      return sdf.format(date);
   }

   public static int getDifferDay(@NotNull Long dateTime) {
      return (int)((System.currentTimeMillis() - dateTime) / 86400000L);
   }

   public static Date getDate(@NotNull Integer day) {
      Date date = new Date();
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      calendar.add(5, day);
      return calendar.getTime();
   }

   public static boolean isPerpetual(@NotNull Date date) {
      return date.getTime() > 4733481600000L;
   }

   public static Date getToday() {
      return beginOfDay(new Date());
   }

   public static Date getTodayEnd() {
      return endOfDay(getToday());
   }

   public static Date beginOfDay(@NotNull Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.set(11, 0);
      calendar.set(12, 0);
      calendar.set(13, 0);
      calendar.set(14, 0);
      return calendar.getTime();
   }

   public static Date endOfDay(@NotNull Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.set(11, 23);
      calendar.set(12, 59);
      calendar.set(13, 59);
      calendar.set(14, 999);
      return calendar.getTime();
   }

   public static Date getMonday() {
      Calendar calendar = Calendar.getInstance(Locale.CHINA);
      calendar.setFirstDayOfWeek(2);
      calendar.setTimeInMillis(getToday().getTime());
      calendar.set(7, 2);
      return calendar.getTime();
   }

   public static Date getSunday() {
      Calendar calendar = Calendar.getInstance(Locale.CHINA);
      calendar.setFirstDayOfWeek(2);
      calendar.setTimeInMillis(getToday().getTime());
      calendar.set(7, 1);
      return parse(format(calendar.getTime(), "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
   }

   public static Integer dayOfWeekEnum(@NotNull Date date) {
      Integer[] weekDays = new Integer[]{7, 1, 2, 3, 4, 5, 6};
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return weekDays[calendar.get(7) - 1];
   }

   public static Date toDate(@NotNull LocalDateTime localDateTime) {
      return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
   }

   public static LocalDateTime toLocalDateTime(@NotNull Date date) {
      return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
   }

   public static long toEpochSecond(@NotNull LocalDateTime localDateTime) {
      return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
   }

   public static long between(@NotNull Date dateOne, @NotNull Date dateTwo) {
      return between(dateOne, dateTwo, ChronoUnit.HOURS);
   }

   public static long between(@NotNull Date dateOne, @NotNull Date dateTwo, @NotNull ChronoUnit unit) {
      return unit.between(toLocalDateTime(dateOne), toLocalDateTime(dateTwo));
   }

   public static Date getWeek(@NotNull Date date, int week) {
      if (++week > 7) {
         week = 1;
      }

      Calendar calendar = Calendar.getInstance(Locale.CHINA);
      calendar.setFirstDayOfWeek(2);
      calendar.setTimeInMillis(date.getTime());
      calendar.set(7, week);
      return calendar.getTime();
   }

   public static Date addDate(@NotNull Date date, @NotNull Integer day) {
      return offset(date, 5, day);
   }

   public static Date getFirstDayOfMonth() {
      return toDate(LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS));
   }

   public static Date getFirstDayOfMonth(@NotNull Date date) {
      return toDate(toLocalDateTime(date).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS));
   }

   public static Date getLastDayOfMonth() {
      return toDate(LocalDateTime.now().plusMonths(1L).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).plus(-1L, ChronoUnit.MILLIS));
   }

   public static Date getLastDayOfMonth(@NotNull Date date) {
      return toDate(toLocalDateTime(date).plusMonths(1L).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).plus(-1L, ChronoUnit.MILLIS));
   }

   public static Date getMonth(@NotNull Date date, int month) {
      Calendar calendar = Calendar.getInstance(Locale.CHINA);
      calendar.setFirstDayOfWeek(2);
      calendar.setTimeInMillis(date.getTime());
      calendar.set(5, month);
      return calendar.getTime();
   }

   public static Date offset(@NotNull Date date, int field, int offset) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(field, offset);
      return cal.getTime();
   }

   @Nullable
   public static Integer parseTime(@Nullable String timeStr) {
      if (StrUtil.isEmpty(timeStr)) {
         return null;
      } else if (PatternUtil.DIGITS_ONLY.matcher(timeStr).matches()) {
         return Integer.parseInt(timeStr);
      } else {
         Matcher matcher = PatternUtil.TIME_WITH_UNIT.matcher(timeStr);
         if (matcher.matches()) {
            int value = Integer.parseInt(matcher.group(1));
            if (value <= 0) {
               return 0;
            } else {
               char unit = matcher.group(2).charAt(0);
               switch (unit) {
                  case 'M':
                     return value * 60 * 60 * 24 * 30;
                  case 'd':
                     return value * 60 * 60 * 24;
                  case 'h':
                     return value * 60 * 60;
                  case 'm':
                     return value * 60;
                  case 's':
                     return value;
                  case 'w':
                     return value * 60 * 60 * 24 * 7;
                  case 'y':
                     return value * 60 * 60 * 24 * 365;
                  default:
                     return null;
               }
            }
         } else {
            return null;
         }
      }
   }

   private static SimpleDateFormat getSimpleDateFormat(@NotNull String format) {
      ThreadLocal<SimpleDateFormat> threadLocal = DATE_FORMAT_CACHE.get(format);
      if (threadLocal != null) {
         return threadLocal.get();
      } else {
         return DATE_FORMAT_CACHE.size() >= 64
            ? new SimpleDateFormat(format)
            : DATE_FORMAT_CACHE.computeIfAbsent(format, key -> ThreadLocal.withInitial(() -> new SimpleDateFormat(key))).get();
      }
   }
}
