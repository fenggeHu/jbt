package jbt.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import jbt.data.utils.JacksonUtil;
import jbt.model.Bar;
import jbt.model.plus.DetailBar;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author max.hu  @date 2024/04/08
 **/
public class JacksonTests {

    @Test
    public void testRead() throws Exception {
        List<Bar> list = mockList();
        String json = JacksonUtil.toJson(list);
        JsonNode node = JacksonUtil.getJsonNode(json);
//        if (node.isArray()) {
//            ArrayNode arrayNode = (ArrayNode) node;
//            arrayNode.iterator();
//        }
        System.out.println(node);

        List<Bar> readList = JacksonUtil.toList(json, new TypeReference<List<Bar>>() {
        });
        System.out.println(readList);
    }

    private List<Bar> mockList() {
        List<Bar> list = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(Bar.builder().datetime("2024" + i)
                    .open(124.546 + i).high(234.45 - i).low(234.45 - i).close(124.546 + i).volume(1278324 + 10 * i)
//                    .turnover(345345 - 10 * i)
                    .build());
        }
        return list;
    }

    // 数据类型转换--数据量越大Jackson效率越高、BeanUtils较慢
    @Test
    public void testConvert() {
        // 10ms
        List<Bar> list = new LinkedList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            list.add(Bar.builder().datetime("2024" + i)
                    .open(124.546 + i).high(234.45 - i).low(234.45 - i).close(124.546 + i).volume(1278324 + 10 * i)
//                    .turnover(345345 - 10 * i)
                    .build());
        }
        long p1 = System.currentTimeMillis();
        System.out.println("Building: " + (p1 - start));

        // 600ms
        List<DetailBar> list2 = new LinkedList<>();
        long start2 = System.currentTimeMillis();
        list.forEach(e -> list2.add(JacksonUtil.convert(e, DetailBar.class)));
        long p2 = System.currentTimeMillis();
        System.out.println("Convert2-Jackson: " + (p2 - start2));

        //
        List<Map> list3 = new LinkedList<>();
        long start3 = System.currentTimeMillis();
        list.forEach(e -> list3.add(JacksonUtil.convert(e, Map.class)));
        long p3 = System.currentTimeMillis();
        System.out.println("Convert3-Jackson: " + (p3 - start3));

//        //
//        List<Map> list4 = new LinkedList<>();
//        long start4 = System.currentTimeMillis();
//        list.forEach(e -> list4.add(JsonUtil.obj2map(e)));
//        long p4 = System.currentTimeMillis();
//        System.out.println("Convert4-gson: " + (p4 - start4));

    }

}
