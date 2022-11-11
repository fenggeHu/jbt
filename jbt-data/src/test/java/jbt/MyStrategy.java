package jbt;

import jbt.model.Row;
import talib.Trend;

/**
 * @author jinfeng.hu  @Date 2022/10/27
 **/
public class MyStrategy extends Strategy {
    @Override
    public void init() {
        double[] closes = closes();
        add("ma1", Trend.Sma(closes, 5));
        add("ma2", Trend.Sma(closes, 20));
    }

    @Override
    public void next() {
        Row pre = row(-1); // 取绝对的前一行
        if (pre.d("ma2") == 0) {
            return;
        }
        Row now = get();
        if (pre.close < pre.d("ma1") && now.close > now.d("ma1")) {
            // 价格突破ma1
            buy(0.2, 500);
        } else if (pre.close > pre.d("ma2") && now.close < now.d("ma2")) {
            // 价格跌破 ma2
            sell();
        }
    }
}
