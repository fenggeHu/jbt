package jbt.data;

import jbt.model.Row;

import java.util.List;

/**
 * k线查询
 * @author jinfeng.hu  @date 2022/10/9
 **/
public interface DataFeeder {
    // 读数据
    List<Row> get(String symbol, String start, String end);
}
