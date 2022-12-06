package jbt.account;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jinfeng.hu  @Date 2022/10/10
 **/
@Data
public class Position {
    // 持仓
    int quantity;
    double principal; // 初始资金
    // 账单
    List<Bill> bills = new LinkedList<>();
    double balance; // 剩余现金
    double value; // 最新价值
    double returns; // 收益金额
    double percent; // 收益百分比 x%

    public Position(double principal) {
        this.principal = principal;
        this.balance = principal;
    }

    // add bill
    public Position addBill(Bill b) {
        this.bills.add(b);
        this.quantity += b.quantity * b.action.value();
        double price = b.price * b.action.value() + b.fee;
        this.balance -= price;
        return this;
    }

    // remove blank bills
    public void trim() {
        List<Bill> valid = this.getBills().stream().filter(e -> e.getTotal() > 0).collect(Collectors.toList());
        this.bills.clear();
        this.bills.addAll(valid);
    }

    /**
     * 按价格计算总价值
     */
    public Position compute(double price) {
        // 最新价值
        this.value = this.quantity * price;
        this.returns = this.value + this.balance - this.principal;
        this.percent = 100 * this.returns / this.principal;
        return this;
    }
}
