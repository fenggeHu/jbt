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
    List<Bar> getBar(String symbol, String start, String end);

    // 读取最近n条数据
    List<Bar> getBar(String symbol, int count);

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
     * 从symbol目录读取整个clazz文件的数据
     */
    <T extends DataFormat> List<T> read(String symbol, Class<T> clazz);

    /**
     * 按起（含）始（含）位置读取数据
     */
    <T extends DataFormat> List<T> read(String symbol, Class<T> clazz, String start, String end);
}
