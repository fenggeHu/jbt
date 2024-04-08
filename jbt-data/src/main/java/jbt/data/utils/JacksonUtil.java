package jbt.data.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 原gson解析经常出现问题，尝试使用Jackson
 * --> JsonNode：
 * JsonNode是Jackson库中用于表示JSON数据的核心接口。它是一个抽象类，代表了JSON数据中的一个节点，可以是一个对象、数组、字符串、数字、布尔值或null。
 * JsonNode提供了一系列方法来访问和操作JSON数据，例如获取节点的类型、获取节点的值、遍历节点等。
 * JsonNode的主要用途是作为JSON数据的容器，它可以用于表示JSON对象、数组、字符串、数字、布尔值或null。
 * 你可以使用JsonNode的方法来访问和操作JSON数据，例如获取节点的类型、获取节点的值、遍历节点等。
 * --> JsonParser：
 * JsonParser是Jackson库中用于解析JSON数据的接口。它提供了一系列方法来读取和解析JSON数据，包括读取JSON元素、处理异常等。
 * JsonParser是一个低级接口，它允许你手动管理JSON解析的状态，这与使用ObjectMapper解析JSON字符串到Java对象的方式不同，后者提供了更多的抽象和封装。
 *
 * @author max.hu  @date 2024/04/08
 **/
public class JacksonUtil {
    //
    public static final ObjectMapper mapper = new ObjectMapper()
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

    // 类转换
    public static <T> T convert(Object obj, Type t) {
        if (null == obj) return null;
        return mapper.convertValue(obj, mapper.constructType(t));
    }

    public static Map toMap(Object obj) {
        if (null == obj) return null;
        if (obj instanceof Map) {
            return (Map) obj;
        } else {
            return convert(obj, Map.class);
        }
    }

    /**
     * 也可以使用 @see #JacksonUtil.toObject(String json, Type t)
     */
    @SneakyThrows
    public static <T> List<T> toList(String jsonArray, TypeReference<List<T>> tr) {
        return toObject(jsonArray, tr.getType());
    }

    /**
     * 有些场景需要解析JsonNode。JsonNode的用法参考：
     * JsonNode jsonNode = objectMapper.readTree(jsonString);
     * String name = jsonNode.get("name").asText();
     * int age = jsonNode.get("age").asInt();
     */
    @SneakyThrows
    public static JsonNode getJsonNode(String json) {
        if (isBlank(json)) return null;
        return mapper.readTree(json);
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
