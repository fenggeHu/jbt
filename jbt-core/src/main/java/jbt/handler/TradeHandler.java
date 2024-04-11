package jbt.handler;

import jbt.account.Account;
import jbt.account.Bill;
import jbt.account.Position;
import jbt.event.Event;
import jbt.event.OrderEvent;
import jbt.model.Action;
import jbt.model.Row;
import jbt.model.Sequence;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 实现交易/订单逻辑
 *
 * @author jinfeng.hu  @date 2022/10/28
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

    // 策略执行后
    @Setter
    private double maxRetracePer = 4;  // 最大回撤%
    @Setter
    private double maxDrawdown = -5;  // 止损点%

    public Event after(final Sequence seq) {
        Row now = seq.get();
        Position pos = this.position.compute(now.getClose());
        if (pos.getQuantity() > 0) {
            // 止损/止盈
            if ((pos.getMaxProfit() > maxRetracePer && pos.getMaxProfit() - pos.getPercent() > maxRetracePer)
                    || pos.getPercent() < maxDrawdown) {
                return sell(now);
            }
        }

        return null;
    }

    // sell
    protected Event sell(Row row) {
        return sell(row, 1, 0);
    }

    /**
     * sell
     *
     * @param ratio 比例
     * @param limit 限制
     */
    protected Event sell(Row row, double ratio, int limit) {
        return OrderEvent.builder().datetime(row.getDatetime()).action(Action.SELL)
                .price(row.getClose()).ratio(ratio).limit(limit).row(row).build();
    }

    // 订单逻辑
    public void apply(OrderEvent oe) {
        switch (oe.getAction()) {
            case BUY:
                this.buy(oe);
                break;
            case SELL:
                this.sell(oe);
                break;
            case CLOSE:
                this.close(oe);
                break;
            case CANCEL:
                this.cancel(oe);
                break;
            case TARGET:
                this.target(oe);
                break;
            default:
                log.error("NOT SUPPORT");
        }
    }

    /**
     * 处理buy信号
     *
     * @param oe
     */
    private void buy(OrderEvent oe) {
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
            log.info("Buy-{}: not enough balance", oe.getDatetime());
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
    }

    /**
     * 处理sell信号
     *
     * @param oe
     */
    private void sell(OrderEvent oe) {
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
            log.info("Sell-{}: no position", oe.getDatetime());
            Bill bill = Bill.builder().datetime(oe.getDatetime()).action(oe.getAction())
                    .price(0).quantity(0).fee(0).total(0).build();
            position.addBill(bill);
        }
    }

    /**
     * 处理平仓信号
     *
     * @param oe
     */
    private void close(OrderEvent oe) {
        // TODO
        log.error("TODO:{} close function", oe.getDatetime());
    }

    /**
     * 处理取消订单信号
     *
     * @param oe
     */
    private void cancel(OrderEvent oe) {
        // TODO
        log.error("TODO:{} cancel function", oe.getDatetime());
    }

    /**
     * 处理调仓到目标仓位的信号
     *
     * @param oe
     */
    private void target(OrderEvent oe) {
        // TODO
        log.error("TODO:{} target function", oe.getDatetime());
    }
}
