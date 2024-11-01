package jbt.data;

import jbt.model.Bar;

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
     * @param chartBar
     * @param overwrite 是否重写
     */
    void store(String symbol, Collection<Bar> chartBar, boolean overwrite);

    /**
     * 写配置/文件
     * @param name
     * @param content
     */
    void write(String name, String content);
}
