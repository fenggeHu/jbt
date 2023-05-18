package utils;

import lombok.SneakyThrows;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jinfeng.hu  @date 2022/12/1
 **/
public class DatetimeUtils {
    public static String defaultFormat = "yyyy-MM-dd HH:mm:ss";
    public final static String yyyyMMdd = "yyyyMMdd";
    public final static String yyyy_MM_dd = "yyyy-MM-dd";

    @SneakyThrows
    public static Date parseDate(String ds) {
        if (ds.length() == 8) {
            return new SimpleDateFormat(yyyyMMdd).parse(ds);
        } else if (ds.length() == 10) {
            return new SimpleDateFormat(yyyy_MM_dd).parse(ds);
        }
        return new SimpleDateFormat(defaultFormat).parse(ds);
    }

    /**
     * TODO 时区问题
     *
     * @return ms
     */
    public static long parseTimestampMs(Date date) {
        return date.getTime();
    }

    public static long parseTimestampMs(String ds) {
        Date date = parseDate(ds);
        return parseTimestampMs(date);
    }

    /**
     * @param ds
     * @return ns
     */
    public static long parseTimestampNs(String ds) {
        return parseTimestampMs(ds) * 1000000;
    }
}
