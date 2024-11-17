package jbt.model;

import jbt.account.Position;
import lombok.Builder;
import lombok.Data;

/**
 * 收集结果
 * Start2004-08-19 00:00:00
 * End  2013-03-01 00:00:00
 * Duration                   3116 days 00:00:00
 * Exposure Time [%]  94.27
 * Equity Final [$]68935.12
 * Equity Peak [$] 68991.22
 * Return [%]        589.35
 * Buy & Hold Return [%]                  703.46
 * Return (Ann.) [%]  25.42
 * Volatility (Ann.) [%]                   38.43
 * Sharpe Ratio        0.66
 * Sortino Ratio       1.30
 * Calmar Ratio        0.77
 * Max. Drawdown [%] -33.08
 * Avg. Drawdown [%]  -5.58
 * Max. Drawdown Duration      688 days 00:00:00
 * Avg. Drawdown Duration       41 days 00:00:00
 * # Trades              93
 * Win Rate [%]       53.76
 * Best Trade [%]     57.12
 * Worst Trade [%]   -16.63
 * Avg. Trade [%]      1.96
 * Max. Trade Duration         121 days 00:00:00
 * Avg. Trade Duration          32 days 00:00:00
 * Profit Factor       2.13
 * Expectancy [%]      6.91
 * SQN                 1.78
 * _strategy              SmaCross(n1=10, n2=20)
 * _equity_curve     Equ...
 * _trades  Size  EntryB...
 * dtype: object
 *
 * @author jinfeng.hu  @date 2022/10/28
 **/
@Data
@Builder
public class Stats {
    public String start;//2004-08-19 00:00:00
    public String end;  //2013-03-01 00:00:00
    public double duration;                   // 3116.5 days
    //    public double exposureTime;            // [%] 94.27
//    public double equityFinal; //[$]68935.12
//    public double equityPeak; //[$] 68991.22
    public double rangeReturn; //时间区间[start, end]内的总收益率[%]        589.35
    //    public double buyHoldReturn; //[%]                  703.46
//    public double annReturn; //(Ann.) [%]  25.42
//    public double annVolatility; //(Ann.) [%]                   38.43
    public double sharpeRatio;    //    0.66
    //    public double sortinoRatio;  //     1.30
//    public double calmarRatio;   //     0.77
    public double maxDrawdown; //[%] -33.08
    //    public double avgDrawdown; //[%]  -5.58
//    public double maxDrawdownDuration;      // 688 days 00:00:00
//    public double avgDrawdownDuration;       //41 days 00:00:00
    //# Trades              93
    public int tradeTimes;     // trade times
//    public double winRate;// [%]       53.76
//    public double bestTrade;// [%]     57.12
//    public double worstTrade;// [%]   -16.63
//    public double avgTrade;// [%]      1.96
//    public double maxTradeDuration;//         121 days 00:00:00
//    public double AvgTradeDuration;//          32 days 00:00:00
//    public double profitFactor;//       2.13
//    public double expectancy;// [%]      6.91
//    public double SQN;//                1.78
//    public String _strategy;              //SmaCross(n1=10, n2=20)
//    public String _equity_curve;//     Equ...
//    public String _trades;//  Size  EntryB...

    // 仓位
    public Position position;

    // 快捷操作
    public boolean hasBuy() {
        if (null == position || position.getBills() == null || position.getBills().isEmpty()) {
            return false;
        }
        return position.getBills().stream().filter(e -> e.getAction().equals(Action.BUY)).count() > 0;
    }
}
