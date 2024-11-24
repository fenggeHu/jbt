package jbt.data;

import jbt.model.Bar;

import java.util.List;

/**
 * k线查询
 *
 * @author jinfeng.hu  @date 2022/10/9
 **/
public interface DataFeeder extends DataFormat {
    // 按时间周期读数据
    List<Bar> get(String symbol, String start, String end);

    // 读取最近n条数据
    List<Bar> get(String symbol, int count);

    /**
     * 读取本地存储的symbol
     */
    List<String> getSymbols();

    /**
     * 读取记录
     *
     * @param filename 类型
     */
    String read(String filename);

    /**
     * 从symbol目录读取最近n条数据
     */
    <T extends DataFormat> List<T> readLines(String symbol, Class<T> clazz, int count);
}
