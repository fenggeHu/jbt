package jbt.enhancement;

import jbt.model.Row;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 注释中的f标记来自eastmoney接口的对应关系
 *
 * @author max.hu  @date 2024/03/04
 **/
@Data
@SuperBuilder
@NoArgsConstructor
public class RealtimeRow extends Row {
    // 成交额(元) - f48  -->同时写入 super.volume
    public double turnover;
    // row.volume 可能是成交额，所以使用realVolume存成交量
    public long realVolume;

    // 涨停价Upper Limit Price f51
    public double upperLimit;
    // 跌停价Lower Limit Price f52
    public double lowerLimit;
    // 交易地区 US/CN/HK
    public String region;
    // f57
    public String symbol;
    // f58
    public String name;
    // f60
    public double preCLose;
    // f71
    // 总股本 Total Shares Outstanding - f84 - f277
    public long totalShares;
    // 流通股 Floating Shares - f85
    public long floatingShares;
    // 时间戳 - f86-不是交易发生的准确时间，好像是数据更新时间，可能是当日盘后的某个时间
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
    // 量比 Volume Ratio f50/100
    public double volumeRatio;  // 1.32
    // 委比 Order Imbalance Ratio f191/100 (%) - 买档/卖档的比例，范围[-100%, 100%]
    public double orderImbalanceRatio;  // -21.80
    // 涨跌金额 f169/100
    public double change;
    // 涨跌幅的百分比 f170/100 (%) - 如，涨跌幅是 -1.8%时，changeRate=-1.8
    public double changeRate;

    // 买卖5档行情 - 可能为空 - 0
    // 最低卖价Lowest Ask Price f301
    public double askPrice;
    List<VolumePrice> asks; // 卖档
    List<VolumePrice> bids; // 买档
//    // 卖5档行情 - 从高到低卖价/卖量 f31~f40
//    public double askPrice1;    // f31
//    public long askVolume1;     // f32
//    public double askPrice2;    // f33
//    public long askVolume2;     // f34
//    public double askPrice3;    // f35
//    public long askVolume3; // f36
//    public double askPrice4;    // f37
//    public long askVolume4; // f38
//    public double askPrice5;    // f39
//    public long askVolume5; // f40
//    // 买5档行情 - 从高到低买价/买量 f19~f11
//    public double bidPrice1;    // f19
//    public long bidVolume1;     // f20
//    public double bidPrice2;    // f17
//    public long bidVolume2;     // f18
//    public double bidPrice3;    // f15
//    public long bidVolume3;     // f16
//    public double bidPrice4;    // f13
//    public long bidVolume4;     // f14
//    public double bidPrice5;    // f11
//    public long bidVolume5;     // f12

    /**
     * 前n档卖金额
     */
    public double askTurnover() {
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
        double prices = 0;
        for (VolumePrice vp : this.bids) {
            prices += vp.getPrice() * vp.getVolume();
        }
        return prices;
    }
}
