package utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Description: 值判断和转换为java基础类型
 * 注意转换为Primitive类型的未做null判断，需要在使用前做判断
 * 参考: com.google.gson.JsonPrimitive
 *
 * @author jinfeng.hu  @date 2021-12-2021/12/21
 **/
public class PrimitiveValueUtil {

    public static boolean isBoolean(Object value) {
        return value instanceof Boolean;
    }

    public static boolean isNumber(Object value) {
        return value instanceof Number;
    }

    public static Number getAsNumber(Object value) {
        return value instanceof Number ? (Number) value : new LazilyParsedNumber(String.valueOf(value));
    }

    public static double getAsDouble(Object value, double def) {
        return null == value ? def : getAsDouble(value);
    }

    public static double getAsDouble(Object value) {
        return getAsNumber(value).doubleValue();
    }

    public static float getAsFloat(Object value) {
        return getAsNumber(value).floatValue();
    }

    public static BigDecimal getAsBigDecimal(Object value) {
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
    }

    public static BigInteger getAsBigInteger(Object value) {
        return value instanceof BigInteger ? (BigInteger) value : new BigInteger(value.toString());
    }

    public static long getAsLong(Object value, long def) {
        return null == value ? def : getAsLong(value);
    }

    public static long getAsLong(Object value) {
        return getAsNumber(value).longValue();
    }

    public static short getAsShort(Object value) {
        return getAsNumber(value).shortValue();
    }

    public static int getAsInt(Object value) {
        return getAsNumber(value).intValue();
    }

    public static byte getAsByte(Object value) {
        return isNumber(value) ? ((Number) value).byteValue() : Byte.parseByte(value.toString());
    }

    public static char getAsCharacter(Object value) {
        return getAsString(value).charAt(0);
    }

    public static boolean getAsBool(Object value) {
        if (isBoolean(value)) {
            return ((Boolean) value).booleanValue();
        }
        // Check to see if the value as a String is "true" in any case.
        return Boolean.parseBoolean(value.toString());
    }

    public static String getAsString(Object value) {
        if (null == value) return null;
        if (isNumber(value)) {
            return getAsNumber(value).toString();
        } else if (isBoolean(value)) {
            return ((Boolean) value).toString();
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * 适配值
     *
     * @param value
     * @param type
     */
    public static Object warpValue(Object value, Class type) {
        if (null == value || value.getClass() == type) {
            return value;
        }
        if (type == String.class) {
            return getAsString(value);
        }
        if (type == Integer.class || type == int.class) {
            return getAsInt(value);
        }
        if (type == Long.class || type == long.class) {
            return getAsLong(value);
        }
        if (type == Double.class || type == double.class) {
            return getAsDouble(value);
        }
        if (type == Boolean.class || type == boolean.class) {
            return getAsBool(value);
        }
        if (type == BigDecimal.class) {
            return getAsBigDecimal(value);
        }
        if (type == BigInteger.class) {
            return getAsBigInteger(value);
        }
        if (type == Character.class || type == char.class) {
            return getAsCharacter(value);
        }
        if (type == Byte.class || type == byte.class) {
            return getAsByte(value);
        }
        if (type == Float.class || type == float.class) {
            return getAsFloat(value);
        }
        if (type == Short.class || type == short.class) {
            return getAsShort(value);
        }

        return value;
    }
}
