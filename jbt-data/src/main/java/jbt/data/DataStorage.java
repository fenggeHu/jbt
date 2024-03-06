package jbt.data;

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
     * 写配置/文件
     *
     * @param type    记录类型 - 配置
     * @param id      记录id - 配置内唯一
     * @param content 记录内容
     */
    void write(String type, String id, final Object content);
}
