package jbt.event;

import jbt.model.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 订单信号和参数
 *
 * @author jinfeng.hu  @date 2022/10/14
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent extends Event {
    // 时间
    public String datetime;
    // 买卖信号
    public Action action;

    public double price;

    public double ratio;

    public int limit;
    // 目标比例 - 取值[0,1]
    public double targetPercent;
}
