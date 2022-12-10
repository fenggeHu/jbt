package jbt.event;

import jbt.model.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单信号
 *
 * @author jinfeng.hu  @date 2022/10/14
 **/
@Data
@Builder
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
}
