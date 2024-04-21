package jbt.model.plus;

import jbt.model.Row;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 补充一些详细信息的Row
 * 注释中的f标记来自eastmoney接口的对应关系
 * eastmoney的不同接口的fn含义可能不同：
 * 1，https://push2.eastmoney.com/api/qt/stock/get
 * 2，https://push2.eastmoney.com/api/qt/ulist/get
 *
 * @author max.hu  @date 2024/03/04
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DetailRow extends Row {
    /**
     * 证券唯一代号 - 全球市场唯一
     */
    public String symbol;
    // 成交额(元) - f48  -->同时写入 super.volume
    public double turnover;
    // row.volume 可能是成交额，所以使用realVolume存成交量
    public long realVolume;

    // 涨停价Upper Limit Price f51
    public double upperLimit;
    // 跌停价Lower Limit Price f52
    public double lowerLimit;
    // 交易地区 US/CN/HK -- 地区 - @Region - CN(1), US(2), HK(3) 1-CN沪深A股、 2-US美股、3-HK港股
    public Region region;
    // f57
    public String code;
    // f58
    public String name;
    // f60
    public double preCLose;
    // f71
    // 总股本 Total Shares Outstanding - f84 - f277
    public long totalShares;
    // 流通股 Floating Shares - f85
    public long floatingShares;
    // 时间戳 - f86-不是交易发生的准确时间，好像是数据更新时间，盘中时间或当日盘后的某个时间
    public long timestamp;
    // 每股净资产 Book Value Per Share (BVPS) - f92
    public double bvps;
    // 每股收益 Earnings Per Share (EPS) - f108
    public double eps;
    // 总市值 - f116
    public double totalMarketCap;
    // 流通市值 - f117
    public double floatMarketCap;

    // 外盘 f49
    public long outside;
    // 内盘 f161
    public long inside;
    // 市盈（TTM - Trailing Twelve Months）是指公司的市盈率（Price-to-Earnings Ratio，P/E Ratio）基于过去12个月的收益数据计算而得
    public double ttmPERatio;      // f164
    // 静态市盈率：通常是指当前时刻的市盈率，它可以是基于过去一年的收益数据，也可以是基于未来预测的数据。
    public double staticPERatio;    // f163
    // 市净率 f167
    public double pbRatio;
    // 换手率 Turnover Ratio f168/100 (%)
    public double turnoverRatio;
    // 52周最高 52-Week High f174 /100
    public double w52high;
    // 52周最低 52-Week Low f175 /100
    public double w52low;
    public double d20changeRate;    // 20日涨幅 - 22.6
    public double d60changeRate;    // 60日涨幅 - 22.6
    public double yearChangeRate;    // 今年涨幅 - 22.6
    // 量比 Volume Ratio f50/100
    public double volumeRatio;  // 1.32
    // 委比 Order Imbalance Ratio f191/100 (%) - 买档/卖档的比例，范围[-100%, 100%]
    public double orderImbalanceRatio;  // -21.80
    // 涨跌金额 f169/100
    public double change;
    // 涨跌幅的百分比 f170/100 (%) - 如，涨跌幅是 -1.8%时，changeRate=-1.8
    public double changeRate;
    // 振幅 (%)
    public double amplitude;

    // 实时买卖行情
    public long bidAskHands;  // 现手（有正负-可能是买卖手的差） f30
    // 最低卖出价Lowest Ask Price f301 / 最高买入价
    public double askPrice; // f32卖出价
    public double bidPrice; // f31买入价
    // 买卖5档行情 - 可能为空 - 0
    List<VolumePrice> asks; // 卖档
    List<VolumePrice> bids; // 买档

    // 特殊值
    // eastmoney内部的市场代码-secid值前的市场代码 f107
    public int eastmoneyMarketCode;

    // ulist/get接口补充属性
    public String industry; // f100 -行业名称
    public String industryLeader; // f101 -行业领涨股的名称
    public String industryLeaderCode; // f146 -行业领涨股的代码
    public String tags; // f103 - 打标（HS300_,深成500,预盈预增,融资融券,创业成份,医疗器械概念,创业板综,深证100R,深股通,MSCI中国,医疗美容,化妆品概念,茅指数,宁组合,百元股,减肥药）
    public double netProfit; // f129 - (财报销售)净利率（%）

    /**
     * 前n档卖金额
     */
    public double askTurnover() {
        if (null == this.asks) return 0.0;
        double prices = 0;
        for (VolumePrice vp : this.asks) {
            prices += vp.getPrice() * vp.getVolume();
        }
        return prices;
    }

    /**
     * 前n档买金额
     */
    public double bidTurnover() {
        if (null == this.bids) return 0.0;
        double prices = 0;
        for (VolumePrice vp : this.bids) {
            prices += vp.getPrice() * vp.getVolume();
        }
        return prices;
    }
}
