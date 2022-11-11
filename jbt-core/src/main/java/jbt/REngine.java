package jbt;

import jbt.account.Account;
import jbt.event.Event;
import jbt.model.Row;
import jbt.model.Sequence;

/**
 * run最新数据的策略 - 增加或更新最后1行
 * 可用于实时触发和最新数据寻找机会
 *
 * @author jinfeng.hu  @Date 2022/10/31
 **/
public class REngine extends Engine {

    /**
     * build - 构建REngine & init
     */
    public static REngine build(Strategy strategy, final Sequence data) {
        REngine engine = new REngine(strategy, data);
        engine.init();
        return engine;
    }

    public static REngine build(Sequence data, Strategy strategy, Account account) {
        REngine engine = new REngine(strategy, data, account);
        engine.init();
        return engine;
    }

    public static REngine build(Sequence data, Strategy strategy, double principal) {
        REngine engine = new REngine(strategy, data, principal);
        engine.init();
        return engine;
    }

    public REngine(Strategy strategy) {
        super(strategy);
    }

    public REngine(Strategy strategy, final Sequence data) {
        super(strategy, data);
    }

    public REngine(Strategy strategy, final Sequence data, Account account) {
        super(strategy, data, account);
    }

    public REngine(Strategy strategy, final Sequence data, double principal) {
        super(strategy, data, principal);
    }

    /**
     * run最新的1行数据
     */
    public Event play() {
        // 数据预处理
        this.preNext();
        // 定位到最后
        this.data.toEnd();
        // 执行策略
        this.next();
        // 处理事件
        Event event = this.getEventQueue().poll();
        if (null != event) {
            this.notify(event);
        }
        return event;
    }

    /**
     * feed last data & play
     */
    public Event play(Row lastRow) {
        this.updateLast(lastRow);
        return play();
    }

    // 更新最近的数据
    private void updateLast(Row lastRow) {
        Row row = this.data.last();
        int ct = row.getDatetime().compareTo(lastRow.getDatetime());
        if (ct > 0) {
            throw new RuntimeException(String.format("it's not the last datetime. %s", lastRow.getDatetime()));
        }
        // 修改Sequence data
        if (ct == 0) {
            row.setOpen(lastRow.getOpen());
            row.setHigh(lastRow.getHigh());
            row.setLow(lastRow.getLow());
            row.setClose(lastRow.getClose());
            row.setVolume(lastRow.getVolume());
        } else {
            Row[] rows = this.data.get_rows();
            Row[] nrs = new Row[rows.length + 1];
            System.arraycopy(rows, 0, nrs, 0, rows.length);
            nrs[rows.length] = lastRow;
            this.data.init(nrs);
        }
    }
}
