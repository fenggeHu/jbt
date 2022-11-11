package jbt.data.local;

import jbt.model.Row;

import java.util.Collection;

/**
 * @author jinfeng.hu  @Date 2022/10/9
 **/
public interface DataStorage {
    // 数据存储
    void store(String symbol, Collection<Row> chartRow);
}
