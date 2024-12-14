package jbt.data.utils;

import java.util.Arrays;

/**
 * string tools
 *
 * @author max.hu  @date 2024/11/28
 **/
public class StringUtil {
    public static final String Comma_CN = "，";
    public static final String Comma_EN = ",";
    // 空格
    public static final String SPACE = " ";

    // Tools
    // 按中/英文逗号分割字符串
    public static String[] splitByComma(String str) {
        if (null == str) return new String[0];
        return str.replace(Comma_CN, Comma_EN).split(Comma_EN);
    }

    public static String[] splitBySpace(String str) {
        if (null == str) return new String[0];
        return str.split(SPACE);
    }

    // 字符串s含words中的任一个
    public static boolean include(String s, String... words) {
        return Arrays.stream(words).anyMatch(s::contains);
    }
}
