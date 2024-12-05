package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jinfeng.hu  @date 2022/11/2
 **/
public class NumberUtil {

    /**
     * 保留n位小数 -- org.apache.commons.lang3.math.NumberUtils
     */
    public static double scale(double d, int n) {
        BigDecimal b = new BigDecimal(d);
        return b.setScale(n, RoundingMode.HALF_UP).doubleValue();
    }

    public static double scale2(double d) {
        return scale(d, 2);
    }

    public static double scale3(double d) {
        return scale(d, 3);
    }

    /**
     * 保留3位小数再比较数字大小
     */
    public static double compare3(double a, double b) {
        return scale3(a) - scale3(b);
    }

}
