package jbt;

import jbt.model.Row;
import lombok.extern.slf4j.Slf4j;
import talib.Trend;
import utils.NumberUtils;

/**
 * 火箭启动
 *
 * @author jinfeng.hu  @Date 2022/10/31
 **/
@Slf4j
public class RocketReadyStrategy extends Strategy {
    @Override
    public void init() {
        double[] closes = closes();
        add("ma5", Trend.Sma(closes, 5));
        add("ma10", Trend.Sma(closes, 10));
        add("ma20", Trend.Sma(closes, 20));
    }

    @Override
    public void next() {
        Row now = get();
        // 封盘
        if (NumberUtils.compare3(now.open, now.high) == 0 && NumberUtils.compare3(now.open, now.close) == 0) {
            log.info("封盘了");
            return;
        }
        // 量(万)
        if (now.volume < 30000) {
            log.info("人气太低-最近成交不足{}", now.volume);
            return;
        }
        // 盘面弱势 - 最新价格低于当日蜡线长度的1/3
        if ((now.getClose() - now.getOpen()) / (now.getHigh() - now.getOpen()) < 0.3) {
            log.info("盘面弱势-最新价格低于当日蜡线长度的0.3");
            return;
        }
        // 5日线高于10日线和20日线
        // 改成波动比率 - 接近就可以
        double ma510 = (now.d("ma5") - now.d("ma10")) / now.d("ma5");
        double ma520 = (now.d("ma5") - now.d("ma20")) / now.d("ma5");
        if (ma510 < -0.01 || ma520 < -0.01) {
            log.info("均线未突破Ma5: {}, Ma10: {}, Ma20: {}",
                    now.d("ma5"), now.d("ma10"), now.d("ma20"));
            return;
        }
        //

        Row pre = row(-1); // 取绝对的前一行
        // 最近1日上涨和阳线
        if (NumberUtils.compare3(now.close, now.open) < 0 || NumberUtils.compare3(now.close, pre.close) < 0) {
            log.info("阴线，昨收价{}, 开盘价{} 现价{}", pre.getClose(), now.getOpen(), now.getClose());
            return;
        }
        //
        double hcpv = (now.getHigh() - now.getClose()) / now.getClose();
        if (hcpv > 0.04) {
            log.info("最新价{}低于最高价{}较大{}%", now.getClose(), now.getHigh(), 100 * hcpv);
            return;
        }
        Row pre2 = row(-2); // 前2天
        // 5日均线高于前2天，有向上趋势
        if (NumberUtils.compare3(now.d("ma5"), pre.d("ma5")) < 0
                || NumberUtils.compare3(now.d("ma5"), pre2.d("ma5")) < 0) {
            log.info("5日均线{}低于前2个交易日的5日均线", now.d("ma5"));
            return;
        }

        if (!isNewHigh(20)) {
            log.info("没有达到20日的新高");
            return;
        }
        Row pre3 = row(-3);
        double p4pct = (now.close - pre3.close) / pre3.close;
        if (p4pct > 0.3) {
            log.info("最近4日涨幅已经较大{}", p4pct);
            return;
        }

        if (pre.close < pre.d("ma5") && now.close > now.d("ma5")) {
            // 价格突破ma1
            buy();
        } else if (pre.close > pre.d("ma20") && now.close < now.d("ma20")) {
            // 价格跌破 ma2
            sell();
        }
    }

    /**
     * 股价是否创n日新高
     */
    public boolean isNewHigh(int days) {
        if (days <= 0) {
            // 不比较
            return true;
        }
        if (this.get_data().length() < days) {
            log.warn("数据不足{}", days);
            return false;
        }
        double price = get().getClose();
        for (int i = 1; i < days; i++) {
            if (NumberUtils.compare3(get(i).getClose(), price) > 0) {
                return false;
            }
        }
        return true;
    }
}
