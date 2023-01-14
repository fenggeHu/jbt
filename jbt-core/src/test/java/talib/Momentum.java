package talib;

import base.Pair;

import static talib.MyMath.subtract;
import static talib.Overlap.Ema;

/**
 * Momentum Indicators(动量指标类)
 *
 * @author jinfeng.hu  @Date 2022/11/23
 **/
public class Momentum {

    // Moving Average Convergence Divergence (MACD).
    //
    // MACD = 12-Period EMA - 26-Period EMA.
    // Signal = 9-Period EMA of MACD.
    //
    // Returns MACD, signal.
    public static Pair<double[], double[]> Macd(double[] close) {
        double[] ema12 = Ema(close, 12);
        double[] ema26 = Ema(close, 26);
        double[] macd = subtract(ema12, ema26);
        double[] signal = Ema(macd, 9);

        return Pair.of(macd, signal);
    }

}
