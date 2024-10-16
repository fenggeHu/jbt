package jbt.data;

import jbt.model.Bar;

import java.util.List;

/**
 * k线查询
 *
 * @author jinfeng.hu  @date 2022/10/9
 **/
public interface DataFeeder {
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
     * @param name 类型
     */
    String read(String name);
}
