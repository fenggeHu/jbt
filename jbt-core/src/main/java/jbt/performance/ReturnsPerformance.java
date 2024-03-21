package jbt.performance;

import jbt.model.Row;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 收益表现评价
 * 基础数据：收益率 - 可以是每日的收益、每周的收益、每月的收益等。
 * Daily Returns = (今日收盘价 − 昨日收盘价) / 昨日收盘价
 * 常用的评价指标：
 * 1，累积收益曲线：
 * 根据量化策略的交易信号和交易记录计算累积收益曲线。累积收益曲线显示了策略从开始执行到结束的累积收益变化情况，可以直观地展示策略的表现。
 * 2，年化收益率：
 * 年化收益率是策略每年平均收益的百分比，可以帮助评估策略的盈利能力。
 * 3，夏普比率：
 * 夏普比率是衡量策略风险调整后的收益能力的指标。夏普比率越高，表示单位风险下的收益越高。
 * 4，最大回撤：
 * 即策略在历史交易中最大的损失幅度。最大回撤是衡量策略风险的重要指标，较大的最大回撤意味着较高的风险水平。
 * 5，胜率和盈亏比：
 * 计算量化策略的胜率和盈亏比，以评估策略的交易胜率和盈利能力。
 * 胜率是指策略获得正收益的交易次数占总交易次数的比例，盈亏比是指策略平均获利交易的盈利额与平均亏损交易的亏损额之比。
 *
 * @author max.hu  @date 2024/03/20
 **/
public class ReturnsPerformance {

    /**
     * 计算连续交易日的每日收益率
     *
     * @Params rows 升序的K线序列
     */
    public static List<Double> getReturns(Row[] rows) {
        List<Double> returns = new LinkedList<>();

        for (int i = 1; i < rows.length; i++) {
            Row currentRow = rows[i];
            Row previousRow = rows[i - 1];
            double dailyReturn = getReturns(currentRow, previousRow);
            returns.add(dailyReturn);
        }

        return returns;
    }

    /**
     * 计算日收益率
     */
    public static double getReturns(Row currentRow, Row previousRow) {
        if (previousRow == null) {
            return 0.0; // 第一天的收益率为零或未定义
        }

        return getReturns(currentRow.close, previousRow.close);
    }

    /**
     * 收益率
     */
    public static double getReturns(double close, double preClose) {
        return (close - preClose) / preClose;
    }

    /**
     * 计算每日收益率
     *
     * @param closes
     * @param preCloses
     */
    public static List<Double> getReturns(double[] closes, double[] preCloses) {
        List<Double> returns = new ArrayList<>();
        for (int i = 0; i < closes.length; i++) {
            if (preCloses[i] <= 0) {
                returns.add(null);
            } else {
                returns.add(getReturns(closes[i], preCloses[i]));
            }
        }
        return returns;
    }

    /**
     * 累积收益率
     *
     * @param returns 收益率 - List<Double> returns
     *                returns.add(0.05); // 第一天的收益率为5%
     *                returns.add(-0.02); // 第二天的收益率为-2%
     *                returns.add(0.03); // 第三天的收益率为3%
     */
    public static List<Double> cumulativeReturns(List<Double> returns) {
        List<Double> cumulativeReturns = new ArrayList<>();
        double cumulativeReturn = 1.0; // 初始累积收益为1（即初始资金）
        cumulativeReturns.add(cumulativeReturn); // 将初始累积收益添加到列表中

        // 遍历每个交易日的收益率，计算累积收益曲线
        for (double dailyReturn : returns) {
            cumulativeReturn *= (1 + dailyReturn); // 计算累积收益率
            cumulativeReturns.add(cumulativeReturn); // 将累积收益率添加到列表中
        }

        return cumulativeReturns;
    }

    // 累积收益率
    public static List<Double> cumulativeReturns(double[] closes, double[] preCloses) {
        List<Double> returns = getReturns(closes, preCloses);
        return cumulativeReturns(returns);
    }

    // 预设一年有250个交易日
    private static final double TradingDaysOfYear = 250.0;

    /**
     * 年化收益率
     *
     * @param returns 每日收益率
     */
    public static double annualizedReturn(List<Double> returns) {
        // 将每日收益率累乘，得到总收益率
        double totalReturn = 1.0;
        for (double dailyReturn : returns) {
            totalReturn *= (1 + dailyReturn);
        }
        // 计算总收益率的天数（假设每日收益率数量就是交易天数）
        int numDays = returns.size();
        // 计算年化收益率（假设一年有TradingDaysOfYear个交易日）
        return Math.pow(totalReturn, TradingDaysOfYear / numDays) - 1;
    }

    /**
     * 计算整段数据的收益率
     *
     * @param returns 每日收益率
     */
    public static double totalReturns(List<Double> returns) {
        double cumulativeReturns = 1.0;
        for (double dailyReturn : returns) {
            cumulativeReturns *= (1.0 + dailyReturn);
        }
        return cumulativeReturns - 1.0;
    }

    /**
     * 计算夏普比率（Sharpe Ratio）- 一种衡量投资组合每承受一单位风险所产生的超额回报的指标。
     *
     * @param returns      每日收益率
     * @param riskFreeRate 无风险利率
     * @return
     */

    public static double sharpeRatio(List<Double> returns, double riskFreeRate) {
        // 计算策略收益率的平均值和标准差
        double meanReturn = averageReturns(returns);
        double stdDeviation = volatility(returns);

        // 计算夏普比率
        double sharpeRatio = (meanReturn - riskFreeRate) / stdDeviation;

        return sharpeRatio;
    }

    /**
     * 计算平均收益率
     */
    public static double averageReturns(List<Double> data) {
        double sum = 0.0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.size();
    }

    /**
     * 计算收益率波动率
     * 1，Math.sqrt(sumOfSquares / returns.size())：
     * 这个方法用于计算样本标准差，其中 sumOfSquares 是每个数据点与均值的差的平方和，returns.size() 是数据点的数量。
     * 这个方法适用于整个总体数据都作为样本的情况，例如，您拥有整个总体的数据并想要计算标准差。
     * 2，Math.sqrt(sumOfSquares / (returns.size() - 1))：
     * 这个方法用于计算样本标准偏差，其中 sumOfSquares 是每个数据点与均值的差的平方和，returns.size() - 1 是自由度，通常用于对样本总体中的未知参数进行估计。
     * 这个方法适用于从总体中抽取的样本，用于估计总体的标准差。
     * 因此，选择哪个方法取决于您处理的数据是整个总体还是样本。如果您处理的是整个总体的数据，则应该使用第一个方法；如果您处理的是从总体中抽取的样本，则应该使用第二个方法。
     */
    private static double volatility(List<Double> returns) {
        double averageReturn = averageReturns(returns);
        double sumOfSquares = 0.0;
        for (double dailyReturn : returns) {
            sumOfSquares += Math.pow(dailyReturn - averageReturn, 2);
        }

        return Math.sqrt(sumOfSquares / returns.size());  // TODO 那个？ Math.sqrt(sumOfSquares / (returns.size() - 1));
    }

    /**
     * 最大回撤（Maximum Drawdown） - 评估资产或策略的最大风险
     * 通过遍历每个交易日的收益率，计算每个交易日的回撤比例，并保留最大的回撤比例作为最大回撤。
     * 这个方法直接计算每个交易日的回撤比例，然后找到其中的最小值。这种方法直接计算回撤比例，而不涉及峰谷的概念。
     * 这种方法通常用于衡量资产或策略的最大风险。
     *
     * @param returns 每日收益率
     */
    public static double maxDrawdown(List<Double> returns) {
        double maxDrawdown = 0.0;
        double peak = Double.NEGATIVE_INFINITY;

        // 遍历每个交易日的收益率，计算最大回撤
        for (double dailyReturn : returns) {
            peak = Math.max(peak, 1 + dailyReturn); // 更新峰值
            double drawdown = (1 + dailyReturn) / peak - 1; // 计算回撤比例
            maxDrawdown = Math.min(maxDrawdown, drawdown); // 更新最大回撤
        }
        return maxDrawdown;
    }

    /**
     * 最大回撤（Maximum Drawdown）- 衡量策略或资产组合的最大损失幅度，以评估其风险水平。
     * 这个方法使用了峰谷法（Peak-to-Valley Approach），通过维护一个峰值和一个谷值来计算最大回撤。在遍历每个交易日的收益率时，计算出每个谷值，并更新最大回撤值。最终，返回的是最大回撤的绝对值除以峰值。
     * 这种方法通常用于衡量策略的最大损失。
     *
     * @param returns
     * @return
     */
//    public static double maxDrawdown(List<Double> returns) {
//        double maxDrawdown = 0.0;
//        double peak = 1.0;
//
//        for (double dailyReturn : returns) {
//            double valley = peak * (1.0 + dailyReturn);
//            maxDrawdown = Math.min(maxDrawdown, valley - peak);
//            peak = Math.max(peak, valley);
//        }
//
//        return -maxDrawdown / peak;
//    }

    /**
     * 计算胜率（Win Rate）- 统计盈利交易的数量并除以总交易数量
     *
     * @param trades 策略交易结果保存在一个列表中，1 表示盈利，-1 表示亏损
     *               示例交易结果数据: List<Integer> trades = List.of(1, -1, 1, 1, -1, 1, 1, 1, 1, -1);
     */
    public static double winRate(List<Integer> trades) {
        double numWins = 0.00;
        for (int trade : trades) {
            if (trade == 1) {
                numWins++;
            }
        }
        return numWins / trades.size();
    }

    /**
     * 盈亏比（Profit-to-Loss Ratio）
     *
     * @param trades 策略交易结果保存在一个列表中，1 表示盈利，-1 表示亏损
     */
    public static double profitLossRatio(List<Integer> trades) {
        double numWins = 0.00;
        int numLosses = 0;
        for (int trade : trades) {
            if (trade == 1) {
                numWins++;
            } else if (trade == -1) {
                numLosses++;
            }
        }
        return numWins / numLosses;
    }
}
