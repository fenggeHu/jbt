package hu.jinfeng;

import jbt.Strategy;
import jbt.model.Bar;
import lombok.extern.slf4j.Slf4j;
import utils.NumberUtils;

import static talib.Overlap.Ema;

/**
 *  策略demo
 *
 * @author jinfeng.hu  @Date 2022/10/31
 **/
@Slf4j
public class MyStrategy extends Strategy {
    private double YI = 10000 * 10000.00;
    private double minRate = 2.0; // 能算爆发的最小比
    private double maxRetracePer = 5;  // 最大回撤点数
    private double maxDrawdown = -10;  // 止损点

    @Override
    public void init() {
        // 尝试ema
        double[] closes = closes();
        add("ma5", Ema(closes, 5));
        add("ma10", Ema(closes, 10));
        add("ma20", Ema(closes, 20));
    }

    @Override
    public void next() {
        Bar now = get();
        // 止损点 - 随着价格上涨不断上移，并限制最大亏损
//        Position pos = this.position(now.close);
//        if (pos.getQuantity() > 0 && (pos.getMaxProfit() - pos.getPercent() > maxRetracePer || pos.getPercent() < maxDrawdown)) { //回撤大于止损点数就sell
//            sell();
//        }
        //
        Bar pre = row(-1); // 取绝对的前一行
        Bar pre2 = row(-2); // 前2天
        if (null == pre || null == pre2) {
            return;
        }
        if ((pre.close < pre2.close && now.close < pre.close) || now.close < now.d("ma5")) {
            // 价格下行
            sell();
        }
        // 封盘
        if (NumberUtils.compare3(now.open, now.high) == 0 && NumberUtils.compare3(now.open, now.close) == 0) {
            log.info("封盘了");
            return;
        }
        // 成交额最少多少
        if (now.getVolume() < YI) {
            log.info("{} 成交额 {}(亿) 太少", now.getDatetime(), now.getVolume() / YI);
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
            log.info(String.format("均线未突破Ma5: %.3f, Ma10: %.3f, Ma20: %.3f",
                    now.d("ma5"), now.d("ma10"), now.d("ma20")));
            return;
        }
        // 最近1日上涨和阳线
        if (NumberUtils.compare3(now.close, now.open) < 0 || NumberUtils.compare3(now.close, pre.close) < 0) {
            log.info("阴线，昨收价{}, 开盘价{} 现价{}", pre.getClose(), now.getOpen(), now.getClose());
            return;
        }
        // 成交额爆发
        if (now.getVolume() / pre.getVolume() < minRate) {
            log.info(String.format("%s 成交额没有爆发 今日%.3f(亿) 前日%.3f(亿)",
                    now.getDatetime(), now.getVolume() / YI, pre.getVolume() / YI));
            return;
        }
        // 均线突破
        if (now.d("ma5") < now.d("ma10") || now.d("ma5") < now.d("ma20")) {
            log.info(String.format("%s 均线未突破Ma5: %.3f, Ma10: %.3f, Ma20: %.3f", now.getDatetime(),
                    now.d("ma5"), now.d("ma10"), now.d("ma20")));
            return;
        }
        //
        double hcpv = (now.getHigh() - now.getClose()) / now.getClose();
        if (hcpv > 0.04) {
            log.info("最新价{}低于最高价{}较大{}%", now.getClose(), now.getHigh(), 100 * hcpv);
            return;
        }
        // 5日均线高于前2天，有向上趋势
        if (NumberUtils.compare3(now.d("ma5"), pre.d("ma5")) < 0
                || NumberUtils.compare3(now.d("ma5"), pre2.d("ma5")) < 0) {
            log.info("5日均线{}低于前2个交易日的5日均线", now.d("ma5"));
            return;
        }

        Bar pre3 = row(-3);
        double p4pct = (now.close - pre3.close) / pre3.close;
        if (p4pct > 0.3) {
            log.info("最近4日涨幅已经较大{}", p4pct);
            return;
        }

        int days = MyTalib.newHighPrice(this.get_data());
        if (days < 40) {
            log.info("创{}日价格新高，没有达到40日的价格新高", days);
            return;
        }
        int days2 = MyTalib.newHighVolume(this.get_data());
        if (days2 < 40) {
            log.info("创{}日成交额新高，没有达到40日的成交额新高", days2);
            return;
        }

        if (pre.close < pre.d("ma5") && now.close > now.d("ma5")) {
            // 价格突破ma1
            buy();
        }
    }

}
