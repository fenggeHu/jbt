package jbt.account;

import lombok.Data;

/**
 * @author jinfeng.hu  @Date 2022/10/10
 **/
@Data
public class Account {
    // 本金
    double principal = 200000.00;
    // 买入费率
    double openCost = 0.003;
    // 卖出费率
    double closeCost = 0.003;
    // 最小交易费
    double minCost = 5.00;
    // 一手股数
    int lotSize = 100;
}
