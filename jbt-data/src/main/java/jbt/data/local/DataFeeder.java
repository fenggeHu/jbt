package jbt.data.local;

import jbt.model.Row;

import java.util.Collection;

/**
 * @author jinfeng.hu  @Date 2022/10/9
 **/
public interface DataFeeder {
    // 读数据
    Collection<Row> get(String symbol, String start, String end);
}
