package utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Description: 值判断和转换为java基础类型
 * 注意转换为Primitive类型的未做null判断，需要在使用前做判断
 * 参考: com.google.gson.JsonPrimitive
 *
 * @author jinfeng.hu  @date 2021-12-2021/12/21
 **/
@Slf4j
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

    private static String blank = "";

    public static String stringValue(Object value) {
        String val = getAsString(value);
        return null == val ? blank : val;
    }

    /**
     * 适配值
     *
     * @param value
     * @param type
     */
    public static Object warpValue(Object value, Class type) {
        if (null == type) {
            return value;
        }
        if (type == String.class) {
            return getAsString(value);
        }
        if (type == Integer.class || type == int.class) {
            if (null == value) {
                if (type == int.class) {
                    return 0;
                } else {
                    return null;
                }
            }
            return getAsInt(value);
        }
        if (type == Long.class || type == long.class) {
            if (null == value) {
                if (type == long.class) {
                    return 0L;
                } else {
                    return null;
                }
            }
            return getAsLong(value);
        }
        if (type == Double.class || type == double.class) {
            if (null == value) {
                if (type == double.class) {
                    return 0.0;
                } else {
                    return null;
                }
            }
            return getAsDouble(value);
        }
        if (type == Boolean.class || type == boolean.class) {
            if (null == value) {
                if (type == boolean.class) {
                    return false;
                } else {
                    return null;
                }
            }
            return getAsBool(value);
        }
        if (type == BigDecimal.class) {
            if (null == value) {
                return null;
            }
            return getAsBigDecimal(value);
        }
        if (type == BigInteger.class) {
            if (null == value) {
                return null;
            }
            return getAsBigInteger(value);
        }
        if (type == Character.class || type == char.class) {
            return getAsCharacter(value);
        }
        if (type == Byte.class || type == byte.class) {
            if (null == value) {
                if (type == byte.class) {
                    return (byte) 0;
                } else {
                    return null;
                }
            }
            return getAsByte(value);
        }
        if (type == Short.class || type == short.class) {
            if (null == value) {
                if (type == short.class) {
                    return (short) 0;
                } else {
                    return null;
                }
            }
            return getAsShort(value);
        }
        if (type == Float.class || type == float.class) {
            if (null == value) {
                if (type == float.class) {
                    return (float) 0;
                } else {
                    return null;
                }
            }
            return getAsFloat(value);
        }

        return value;
    }

    public static boolean isPrimitiveNumber(Class type) {
        return type == int.class || type == short.class || type == byte.class
                || type == long.class || type == float.class || type == double.class;
    }

    // 处理了null值 - 2024-3-11
    public static int intValue(Object value) {
        return (int) warpValue(value, int.class);
    }

    // 非number类型的特殊处理
    public static int toInt(Object value) {
        try {
            return intValue(value);
        } catch (Exception e) {
            log.warn("value: " + value, e);
        }
        return 0;
    }

    public static long longValue(Object value) {
        return (long) warpValue(value, long.class);
    }

    // 非number类型的特殊处理
    public static long toLong(Object value) {
        try {
            return longValue(value);
        } catch (Exception e) {
            log.warn("value: " + value, e);
        }
        return 0L;
    }


    public static double doubleValue(Object value) {
        return (double) warpValue(value, double.class);
    }

    // 非number类型的特殊处理
    public static double toDouble(Object value) {
        try {
            return doubleValue(value);
        } catch (Exception e) {
            log.warn("value: " + value, e);
        }
        return 0L;
    }


    public static byte byteValue(Object value) {
        return (byte) warpValue(value, byte.class);
    }

    public static short shortValue(Object value) {
        return (short) warpValue(value, short.class);
    }

    public static float floatValue(Object value) {
        return (float) warpValue(value, float.class);
    }

    public static boolean boolValue(Object value) {
        return (boolean) warpValue(value, boolean.class);
    }

}
