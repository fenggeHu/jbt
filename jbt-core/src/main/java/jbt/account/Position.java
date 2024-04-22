package jbt.account;

import jbt.model.Action;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 持仓 - todo 单票的持仓
 *
 * @author jinfeng.hu  @date 2022/10/10
 **/
@Data
public class Position {
    String symbol;
    // 持仓
    int quantity;
    double principal; // 初始资金
    // 总账单
    List<Bill> bills = new LinkedList<>();
    double balance; // 剩余现金
    double value; // 最新价值
    double returns; // 收益金额
    double percent; // 收益百分比 x%
    double maxDrawdown; //最大回撤 %
    double maxProfit; //最大盈利 %
    // 当前的持仓账单
    double posCosts; // 持仓成本
    double posValue; // 持仓价值
    double posReturns;  // 持仓收益
    double posPercent; // 持仓收益率 %
    double posMaxDrawdown; // 持仓最大回撤 %
    double posMaxProfit; // 持仓最大盈利 %

    public Position(double principal) {
        this.principal = principal;
        this.balance = principal;
    }

    // add bill 操作仓位必须调用这个方法. 这里只有买卖
    public Position addBill(Bill b) {
        if (!Action.BUY.equals(b.getAction()) && !Action.SELL.equals(b.getAction())) {
            throw new RuntimeException("Invalid Bill Action: " + b.getAction());
        }
        this.bills.add(b);
        int vol = b.quantity * b.action.value();
        this.quantity += vol;
        double amount = b.getAmount() * b.action.value();
        this.posCosts += amount + b.fee; // 扣除费用
        this.balance -= amount + b.fee;
        return this;
    }

    // 瞬时持仓信息
    public Position instant(double price) {
        if (this.bills.size() == 0) {   // 没有订单就不需要计算
            return this;
        }
        this.posValue = this.quantity * price;
        if (this.posCosts != 0) {
            this.posReturns = this.posValue - this.posCosts;
            this.posPercent = this.posReturns / this.posCosts;
        }

        // 计算持仓回撤和收益
        if (this.posPercent < 0) {
            this.posMaxDrawdown = Math.min(this.posMaxDrawdown, this.posPercent);
        } else if (this.posPercent > 0) {
            this.posMaxProfit = Math.max(this.posMaxProfit, this.posPercent);
        }

        return this;
    }

    // 平仓 TODO
    public void close(String date, double price, double fee) {
        if (this.quantity == 0) {
            return;
        }
        Bill bill = Bill.builder().datetime(date)
                .price(price).fee(fee)
                .build();
        if (this.quantity > 0) {

        } else {

        }
    }

    // remove blank bills
    public Position trim() {
        List<Bill> valid = this.getBills().stream().filter(e -> e.getTotal() > 0).collect(Collectors.toList());
        this.bills.clear();
        this.bills.addAll(valid);
        return this;
    }

    /**
     * 按价格计算总价值
     */
    public Position summary(double price) {
        // 最新价值
        this.value = this.quantity * price;
        this.returns = this.value + this.balance - this.principal;
        this.percent = 100 * this.returns / this.principal;
        // 计算回撤和收益
        if (this.percent < 0) {
            this.maxDrawdown = Math.min(this.maxDrawdown, this.percent);
        } else if (this.percent > 0) {
            this.maxProfit = Math.max(this.maxProfit, this.percent);
        }

        return this;
    }

}
