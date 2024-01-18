package jbt.data;

import jbt.model.Row;

import java.util.Collection;

/**
 * k线存储
 * @author jinfeng.hu  @date 2022/10/9
 **/
public interface DataStorage {
    // 数据存储
    void store(String symbol, Collection<Row> chartRow);
}
