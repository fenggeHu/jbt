package utils;

import org.junit.Test;

/**
 * @author max.hu  @date 2024/03/11
 **/
public class PrimitiveValueTests {

    @Test
    public void testPrimitive() {
        double b = PrimitiveValueUtil.doubleValue("123.5");
        double c = PrimitiveValueUtil.doubleValue(345);
        Integer a = PrimitiveValueUtil.intValue(null);
        System.out.println();
    }
}
