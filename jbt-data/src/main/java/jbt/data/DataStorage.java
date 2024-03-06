package jbt.data;

import jbt.data.feature.Record;
import jbt.model.Row;

import java.util.Collection;

/**
 * 数据存储
 *
 * @author jinfeng.hu  @date 2022/10/9
 **/
public interface DataStorage {
    /**
     * kline数据存储
     *
     * @param symbol
     * @param chartRow
     * @param overwrite 是否重写
     */
    void store(String symbol, Collection<Row> chartRow, boolean overwrite);

    /**
     * 写记录
     *
     * @param type    记录类型
     * @param content 记录内容
     */
    boolean writeRecord(String type, String id, final Record content);
}
