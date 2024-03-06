package jbt.data;

import jbt.data.feature.Record;
import jbt.model.Row;

import java.util.List;
import java.util.Map;

/**
 * k线查询
 *
 * @author jinfeng.hu  @date 2022/10/9
 **/
public interface DataFeeder {
    // 按时间周期读数据
    List<Row> get(String symbol, String start, String end);

    // 读取最近n条数据
    List<Row> get(String symbol, int count);

    /**
     * 读取本地存储的symbol
     */
    List<String> getSymbols();

    /**
     * 读取记录
     *
     * @param type 类型
     * @param id 数据id
     * @return Map<id, Record>
     */
    Map<String, Record> readRecord(String type, String id);
}
