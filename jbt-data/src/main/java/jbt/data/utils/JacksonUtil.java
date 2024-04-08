package jbt.data.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.lang.reflect.Type;

/**
 * 原gson解析经常出现问题，尝试使用Jackson
 * @author max.hu  @date 2024/04/08
 **/
public class JacksonUtil {
    private static final ObjectMapper mapper = new ObjectMapper()
            // 设置在反序列化时忽略在JSON字符串中存在，而在Java中不存在的属性
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @SneakyThrows
    public static <T> T toObject(String json, Type t) {
        if (isBlank(json)) return null;
        return mapper.readValue(json, mapper.constructType(t));
    }

    @SneakyThrows
    public static <T> T toObject(String json, Class<T> t) {
        if (isBlank(json)) return null;
        return mapper.readValue(json, t);
    }

    @SneakyThrows
    public static String toJson(Object src) {
        return mapper.writeValueAsString(src);
    }

    // check string
    public static boolean isBlank(CharSequence cs) {
        int strLen = length(cs);
        if (strLen != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}
