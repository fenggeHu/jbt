package jbt.calculator;

import jbt.model.Row;

import java.util.LinkedList;
import java.util.List;

/**
 * 评估策略表现 - 计算各种指标，例如收益曲线、最大回撤、夏普比率等。
 * returns参数表示策略每日的收益率序列。收益率是衡量投资绩效的一种指标，通常用百分比表示。具体来说，每日收益率的计算方式是：
 * Daily Return = (今日收盘价 − 昨日收盘价)/ 昨日收盘价
 *
 * @author max.hu  @date 2024/02/27
 **/
public class StrategyCalculator {

    /**
     * 计算收益率
     *
     * @Params rows 升序的K线序列
     */
    public static List<Double> dailyReturns(Row[] rows) {
        List<Double> returns = new LinkedList<>();

        for (int i = 1; i < rows.length; i++) {
            Row currentRow = rows[i];
            Row previousRow = rows[i - 1];
            double dailyReturn = dailyReturn(currentRow, previousRow);
            returns.add(dailyReturn);
        }

        return returns;
    }

    /**
     * 计算每日收益率
     */
    private static double dailyReturn(Row currentRow, Row previousRow) {
        if (previousRow == null || previousRow.getClose() <= 0) {
            return 0.0; // 第一天的收益率为零或未定义
        }

        // 计算每日收益率
        return (currentRow.close - previousRow.close) / previousRow.close;
    }

    /**
     * 计算收益曲线
     */
    public static double cumulativeReturns(List<Double> returns) {
        double cumulativeReturns = 1.0;
        for (double dailyReturn : returns) {
            cumulativeReturns *= (1.0 + dailyReturn);
        }
        return cumulativeReturns - 1.0;
    }

    /**
     * 计算最大回撤
     */
    public static double maxDrawdown(List<Double> returns) {
        double maxDrawdown = 0.0;
        double peak = 1.0;

        for (double dailyReturn : returns) {
            double valley = peak * (1.0 + dailyReturn);
            maxDrawdown = Math.min(maxDrawdown, valley - peak);
            peak = Math.max(peak, valley);
        }

        return -maxDrawdown / peak;
    }

    /**
     * 计算夏普比率
     */
    public static double sharpeRatio(List<Double> returns, double riskFreeRate) {
        double averageReturn = averageReturn(returns);
        double volatility = volatility(returns);

        if (volatility == 0.0) {
            return 0.0;  // 避免除以零错误
        }

        return (averageReturn - riskFreeRate) / volatility;
    }

    /**
     * 计算平均收益率
     */
    private static double averageReturn(List<Double> returns) {
        double sum = 0.0;
        for (double dailyReturn : returns) {
            sum += dailyReturn;
        }
        return sum / returns.size();
    }

    /**
     * 计算收益率波动率
     */
    private static double volatility(List<Double> returns) {
        double sumOfSquares = 0.0;
        double averageReturn = averageReturn(returns);

        for (double dailyReturn : returns) {
            sumOfSquares += Math.pow(dailyReturn - averageReturn, 2);
        }

        return Math.sqrt(sumOfSquares / returns.size());
    }

}