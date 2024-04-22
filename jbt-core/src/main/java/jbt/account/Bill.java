package jbt.account;

import jbt.model.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账单
 *
 * @author jinfeng.hu  @date 2022/10/10
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    // 交易日
    String datetime;
    // 操作
    Action action;
    // 买/卖的单价
    double price;
    // 买/卖的金额-总价
    double amount;
    // 买1/卖-1的数量
    int quantity;
    // 交易费用
    double fee;
    // 总金额
    double total;
}
