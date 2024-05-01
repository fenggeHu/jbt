package utils;

import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 用于处理各种日期格式的类
 *
 * @author jinfeng.hu  @date 2022/12/1
 **/
public class DatetimeUtils {
    public final static String yyyy_MM_dd = "yyyy-MM-dd";
    public final static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public final static String yyyyMMdd = "yyyyMMdd";
    // a day
    public final static long A_DAY_MS = 1000 * 60 * 60 * 24;

    // 解析日期格式
    @SneakyThrows
    public static Date parseDate(String ds) {
        if (ds.length() == 8) {
            return new SimpleDateFormat(yyyyMMdd).parse(ds);
        } else if (ds.length() == 10) {
            return new SimpleDateFormat(yyyy_MM_dd).parse(ds);
        }
        return new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss).parse(ds);
    }

    /**
     * 现在的日期
     *
     * @param zone 转换成带时区的日期
     * @return
     */
    public static String nowStr(TimeZone zone) {
        Calendar cal = Calendar.getInstance(zone);
        return format(cal.getTime(), zone);
    }

    public static Date now() {
        Calendar cal = Calendar.getInstance();      // zone好像没什么用
        return cal.getTime();
    }

    public static String now8s(TimeZone zone) {
        return format(new Date(), yyyyMMdd, zone);
    }

    /**
     * @param fromStr
     * @param toStr
     * @return
     */
    public static int rangeDays(String fromStr, String toStr) {
        Date from = format(fromStr);
        Date to = format(toStr);
        return diff2days(from, to);
    }

    /**
     * 计算2个日期之间相差的天数
     *
     * @param from 输入的日期1
     * @param to   输入的日期2
     * @return int
     * 2个日期间隔的天数
     */

    public static int diff2days(Date from, Date to) {
        if (to.compareTo(from) <= 0) {
            return 0;
        } else {
            long interval = to.getTime() - from.getTime();
            return (int) (interval / A_DAY_MS);
        }
    }

    /**
     * 20220101 转 2022-01-01
     */
    public static String dayStr8To10(int yyyyMMdd) {
        String ds = String.valueOf(yyyyMMdd);
        return dayStr8To10(ds);
    }

    public static String dayStr8To10(String yyyyMMdd) {
        if (null == yyyyMMdd) return null;
        if (yyyyMMdd.length() != 8) {
            Date d = parseDate(yyyyMMdd);
            return format(d);
        }
        return yyyyMMdd.substring(0, 4) + "-" + yyyyMMdd.substring(4, 6) + "-" + yyyyMMdd.substring(6);
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(yyyy_MM_dd);
    private static DateTimeFormatter formatter4NumDay = DateTimeFormatter.ofPattern(yyyyMMdd);

    /**
     * yyyy-MM-dd
     *
     * @param dateTime
     * @return
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    public static String format(long timestamp, TimeZone zone) {
        return format(new Date(timestamp), zone);
    }

    public static String format(Date date) {
        return format(date, TimeZone.getDefault());
    }

    /**
     * date format string
     *
     * @param date
     * @param pattern
     * @param zone
     * @return
     */
    public static String format(Date date, String pattern, TimeZone zone) {
        SimpleDateFormat df = null == pattern ? new SimpleDateFormat(yyyy_MM_dd) : new SimpleDateFormat(pattern);
        if (null != zone) {
            df.setTimeZone(zone);
        }
        return df.format(date);
    }

    public static String format(Date date, TimeZone zone) {
        return format(date, yyyy_MM_dd, zone);
    }

    public static Date format(String dateStr, TimeZone zone) {
        SimpleDateFormat df = new SimpleDateFormat(yyyy_MM_dd);
        try {
            if (null != zone) {
                df.setTimeZone(zone);
            }
            return df.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Date String[" + dateStr + "] is not right format: " + yyyy_MM_dd);
        }
    }

    public static Date format(String dateStr) {
        return format(dateStr, null);
    }

    /**
     * yyyyMMdd - 8个字符格式
     *
     * @param dateTime
     * @return
     */
    public static int toNum8(LocalDateTime dateTime) {
        return Integer.parseInt(dateTime.format(formatter4NumDay));
    }

    /**
     * yyyyMMdd - 8个字符格式
     *
     * @param date
     * @param zone
     * @return
     */
    public static int toNum8(Date date, TimeZone zone) {
        String ds = format(date, yyyyMMdd, zone);
        return Integer.parseInt(ds);
    }

    /**
     * 0点的值缺少小时
     */
    public static int toNumber(Date date, String pattern, TimeZone zone) {
        String format = format(date, pattern, zone);
        return Integer.parseInt(format);
    }

    /**
     * 时间差HHmm
     *
     * @param t1
     * @param t2
     * @return
     */
    public static int betweenMin(int t1, int t2) {
        String m1 = String.valueOf(t1);
        String m2 = String.valueOf(t2);
        int h1 = Integer.valueOf(m1.substring(0, m1.length() - 2));
        int h2 = Integer.valueOf(m2.substring(0, m2.length() - 2));
        return (t2 - t1) - (h2 - h1) * 40;
    }

    public static boolean isWeekend(Date date) {
        // 创建 Calendar 对象并设置为给定日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 获取给定日期的星期几
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 判断是否是周六或周日（星期日为1，星期六为7）
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    private static Date add(Date date, int calendarField, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }
}
