package jbt.model;

/**
 * Description: 定义action枚举值
 *
 * @author jinfeng.hu  @date 2022-10-06
 **/
public enum Action {
    BUY(1),     // 买
    SELL(-1),   // 卖

    // biz action
    CANCEL(-2),   // 取消订单
    CLOSE(0),   // 平仓
    TARGET(2);  // 目标仓位 - 补/减仓到目标
    private int value;

    public int value() {
        return value;
    }

    Action(int i) {
        this.value = i;
    }
}
