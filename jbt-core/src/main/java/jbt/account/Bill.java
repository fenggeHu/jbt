package jbt.account;

import jbt.model.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jinfeng.hu  @date 2022/10/10
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    String symbol;
    String datetime;
    Action action;
    double price;
    int quantity;
    double fee;
    double total;
}
