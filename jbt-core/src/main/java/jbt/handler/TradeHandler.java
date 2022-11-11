package jbt.handler;

import jbt.account.Account;
import jbt.account.Bill;
import jbt.account.Position;
import jbt.event.OrderEvent;
import jbt.model.Action;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jinfeng.hu  @Date 2022/10/28
 **/
@Slf4j
public class TradeHandler implements Handler {
    @Getter
    private final Account account;
    @Getter
    private final Position position;

    public TradeHandler(Account account) {
        this.account = account;
        this.position = new Position(account.getPrincipal());
    }

    public TradeHandler(Account account, Position position) {
        this.account = account;
        this.position = position;
    }

    // 订单逻辑
    public void run(OrderEvent oe) {
        if (oe.getAction() == Action.BUY) {
            double lotPrice = account.getLotSize() * oe.getPrice();
            int lot;
            if (oe.getRatio() > 0) {
                double maxTotal = position.getBalance() * oe.getRatio();  // 取总值的比率
                lot = (int) Math.floor(maxTotal / lotPrice);
            } else if (oe.getLimit() > 0) {
                lot = Math.min(oe.getLimit(), (int) Math.floor(position.getBalance() / lotPrice));
            } else {
                lot = (int) Math.floor(position.getBalance() / lotPrice);
            }

            if (lot < 1) {
                log.info("not enough balance");
                // record
                Bill bill = Bill.builder().datetime(oe.getDatetime()).action(oe.getAction())
                        .price(0).quantity(0).fee(0).total(0).build();
                position.addBill(bill);
                return;
            }
            int quantity = lot * account.getLotSize();
            double price = lotPrice * lot;
            double fee = Math.max(account.getOpenCost() * price, account.getMinCost());
            Bill bill = Bill.builder().datetime(oe.getDatetime()).action(oe.getAction())
                    .price(price).quantity(quantity).fee(fee).total(price + fee).build();
            position.addBill(bill);
        } else if (oe.getAction() == Action.SELL) {
            if (position.getQuantity() > 0) {
                int quantity;
                if (oe.getRatio() > 0) {
                    quantity = (int) Math.floor(position.getQuantity() * oe.getRatio());    // 取总持仓的比率
                } else if (oe.getLimit() > 0) {
                    quantity = Math.min(oe.getLimit(), position.getQuantity());
                } else {
                    quantity = position.getQuantity();
                }

                double price = quantity * oe.getPrice();
                double fee = Math.max(account.getCloseCost() * price, account.getMinCost());
                Bill bill = Bill.builder().datetime(oe.getDatetime()).action(oe.getAction())
                        .price(price).quantity(quantity).fee(fee).total(price + fee).build();
                position.addBill(bill);
            } else {
                log.debug("no position");
                Bill bill = Bill.builder().datetime(oe.getDatetime()).action(oe.getAction())
                        .price(0).quantity(0).fee(0).total(0).build();
                position.addBill(bill);
            }
        }
    }
}
