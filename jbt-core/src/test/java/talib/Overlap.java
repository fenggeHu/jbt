package talib;

import base.Triple;

import static talib.MyMath.*;

/**
 * Overlap Studies(重叠指标)
 *
 * @author jinfeng.hu  @Date 2022/10/31
 **/
public class Overlap {

    // Bollinger Bands.
    // （布林带）
    // Middle Band = 20-Period SMA.
    // Upper Band = 20-Period SMA + 2 (20-Period Std)
    // Lower Band = 20-Period SMA - 2 (20-Period Std)
    //
    // Returns middle band, upper band, lower band.
    public static Triple<double[], double[], double[]> BBands(double[] closing) {
        return BollingerBands(closing);
    }

    public static Triple<double[], double[], double[]> BollingerBands(double[] closing) {
        double[] middleBand = Sma(closing, 20);

        double[] std = StdFromSma(closing, middleBand, 20);
        double[] std2 = multiplyBy(std, 2);

        double[] upperBand = add(middleBand, std2);
        double[] lowerBand = subtract(middleBand, std2);

        return Triple.of(middleBand, upperBand, lowerBand);
    }

    // Dema calculates the Double Exponential Moving Average (DEMA).
    // （双指数移动平均线）
    // DEMA = (2 * EMA(values)) - EMA(EMA(values))
    //
    // Returns dema.
    public static double[] Dema(double[] values, int period) {
        double[] ema1 = Ema(values, period);
        double[] ema2 = Ema(ema1, period);
        double[] dema = subtract(multiplyBy(ema1, 2), ema2);

        return dema;
    }

    // Exponential Moving Average (EMA).
    public static double[] Ema(double[] values) {
        return Ema(values, 30);
    }

    public static double[] Ema(double[] values, int period) {
        double[] result = new double[values.length];

        double k = 2.00 / (1 + period);
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                result[i] = (values[i] * k) + (result[i - 1] * (1 - k));
            } else {
                result[i] = values[i];
            }
        }

        return result;
    }

    // Simple Moving Average (SMA).
    public static double[] Sma(double[] values, int period) {
        double[] result = new double[values.length];
        double sum = 0.00;

        for (int i = 0; i < values.length; i++) {
            sum += values[i];

            if (i >= period) {
                sum -= values[i - period];
            }
            if (i >= period - 1) {
                result[i] = sum / period;
            }
        }

        return result;
    }

    // Standard deviation.
    public static double[] Std(double[] values, int period) {
        return StdFromSma(values, Sma(values, period), period);
    }

    // 排除sma为0
    // Standard deviation from the given SMA.
    public static double[] StdFromSma(double[] values, double[] sma, int period) {
        double[] result = new double[values.length];

        double sum2 = 0.0;
        for (int i = 0; i < values.length; i++) {
            if (sma[i] == 0) continue;
            sum2 += values[i] * values[i];
            if (i < period - 1) {
                result[i] = 0.0;
            } else {
                result[i] = Math.sqrt(sum2 / period - sma[i] * sma[i]);
                double w = values[i - (period - 1)];
                sum2 -= w * w;
            }
        }

        return result;
    }
}
