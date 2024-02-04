package jbt.data;

import jbt.model.Row;

import java.util.Collection;

/**
 * k线存储
 *
 * @author jinfeng.hu  @date 2022/10/9
 **/
public interface DataStorage {
    /**
     * 数据存储
     *
     * @param symbol
     * @param chartRow
     * @param overwrite 是否重写
     */
    void store(String symbol, Collection<Row> chartRow, boolean overwrite);

    /**
     * 读取本地存储的symbol
     */
    Collection<String> getLocalSymbols();
}
