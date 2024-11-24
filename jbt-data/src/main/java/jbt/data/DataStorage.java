package jbt.data;

import jbt.model.Bar;

import java.util.Collection;

/**
 * 数据存储
 *
 * @author jinfeng.hu  @date 2022/10/9
 **/
public interface DataStorage extends DataFormat {
    /**
     * kline数据存储
     *
     * @param symbol
     * @param chartBar
     * @param overwrite 是否重写
     */
    void storeBar(String symbol, Collection<Bar> chartBar, boolean overwrite);

    /**
     * 写配置/文件
     *
     * @param filename
     * @param content
     */
    void write(String filename, String content);

    /**
     * 按行把数据存入symbol目录 - 数据按第一列（通常是日期时间）去重覆盖
     *
     * @param overwrite true - 覆盖；false - 补充更新
     */
    <T extends DataFormat> int store(String symbol, Collection<T> lines, boolean overwrite);
}
