package jbt.data.utils;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * json
 *
 * @author Jinfeng.hu  @Date 2021-11-2021/11/22
 **/
@Slf4j
public class JsonUtil {
    // 默认序列化忽略null
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();// .setPrettyPrinting()-序列化输出时多行
    public final static String ArrayStart = "[";
    public final static String ArrayEnd = "]";

    public static <T> T map2Obj(Map<String, Object> map, Class<T> t) {
        return toObject(toJson(map), t);
    }

    public static Map obj2map(Object obj) {
        if (obj instanceof Map) {
            return (Map) obj;
        } else {
            String json = toJson(obj);
            return toObject(json, HashMap.class);
        }
    }

    /**
     * 转对象
     *
     * @param json
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json, Class<T> t) {
        if (null == json) return null;
        return gson.fromJson(json, t);
    }

    public static <T> T toObject(String json, Type t) {
        if (null == json) return null;
        return gson.fromJson(json, t);
    }

    /**
     * 转成List
     *
     * @param json
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> t) {
        JsonElement element = JsonParser.parseString(json);
        return toList(element, t);
    }

    public static <T> List<T> toList(JsonElement element, Class<T> t) {
        List ret = new LinkedList<>();
        if (element.isJsonArray()) {
            for (JsonElement e : element.getAsJsonArray()) {
                ret.add(gson.fromJson(e, t));
            }
        } else {
            T obj = gson.fromJson(element, t);
            ret.add(obj);
        }
        return ret;
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static JsonObject getJsonObject(String json) {
        return gson.fromJson(json, JsonElement.class).getAsJsonObject();
    }

    /**
     * 搜索json节点
     * 未对node表达式做复杂判断
     *
     * @param root
     * @param node 注意格式 eg: result.data.rows[1].kline
     * @return
     */
    public static JsonElement getJsonElement(final JsonElement root, String node) {
        if (null == root) return null;
        if (null == node || node.trim().length() == 0) return root;
        try {
            JsonElement current = root;
            String[] trees = node.split("\\.");
            for (String t : trees) {
                if (t.contains(ArrayStart) && t.contains(ArrayEnd)) {
                    int st = t.indexOf(ArrayStart);
                    int end = t.indexOf(ArrayEnd, st);
                    String name = t.substring(0, st);
                    int index = Integer.parseInt(t.substring(st + 1, end));
                    current = current.getAsJsonObject().getAsJsonArray(name).getAsJsonArray().get(index);
                } else {
                    current = current.getAsJsonObject().getAsJsonObject(t);
                }
            }
            return current;
        } catch (Exception e) {
            log.warn("get sub element failed: node=" + node, e);
        }
        return null;
    }

    /**
     * jsonElement转对象 - 注意：如果是数组就取第1个
     * 如果取数组用toList
     *
     * @param root
     * @param node
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T toObject(final JsonElement root, String node, Class<T> t) {
        JsonElement jsonElement = getJsonElement(root, node);
        if (null == jsonElement) return null;

        List<T> list = toList(jsonElement, t);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
