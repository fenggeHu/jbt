package jbt.data.ext;

import jbt.data.local.LocalFileStoreFeeder;
import jbt.model.Bar;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 扩展数据
 *
 * @author max.hu  @date 2024/12/19
 **/
public class ExtLocalFileStoreFeeder extends LocalFileStoreFeeder {

    public ExtLocalFileStoreFeeder(String s, String cn) {
        super(s, cn);
    }

    // 读取本地的数据，如果数据不完整，则调用func获取数据
    // 没有判断交易日历
    public List<Bar> getBar(String symbol, String start, String end, Function3<String, String, String, List<Bar>> func) {
        List<Bar> bars = super.getBar(symbol);
        // 简单的判断数据是否完整
        if (!bars.isEmpty()) {
            long pre = bars.stream().filter(e -> e.datetime.compareTo(start) <= 0).count();
            long last = bars.stream().filter(e -> e.datetime.compareTo(end) >= 0).count();
            if (pre > 0 && last > 0) {  // 数据完整
                return bars.stream().filter(e -> e.datetime.compareTo(start) >= 0 && e.datetime.compareTo(end) <= 0)
                        .collect(Collectors.toList());
            }
        }

        bars = func.apply(symbol, start, end);
        super.storeBar(symbol, bars, false);
        return bars;
    }
}
