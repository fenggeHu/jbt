package jbt;

import jbt.model.Action;
import jbt.model.Row;
import jbt.model.Sequence;
import jbt.event.Event;
import jbt.event.Container;
import jbt.event.OrderEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import utils.PrimitiveValueUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * strategy bean
 *
 * @author jinfeng.hu  @date 2022/10/27
 **/
@Slf4j
public class Strategy {
    /**
     * 引用engine的data\position
     */
    @Getter
    private Sequence _data;

    // 非Sequence的扩展属性
    private final Map<String, Object> _ext = new HashMap<>();

    public void addExt(String k, Object v) {
        _ext.put(k, v);
    }

    public void addExts(Map<String, Object> kvs) {
        if (null == kvs || kvs.isEmpty()) return;
        _ext.putAll(kvs);
    }

    public Object ext(String k) {
        return _ext.get(k);
    }

    public String exts(String k) {
        Object obj = ext(k);
        return PrimitiveValueUtil.stringValue(obj);
    }

    public boolean extb(String k) {
        Object obj = ext(k);
        return PrimitiveValueUtil.boolValue(obj);
    }

    public double extd(String k) {
        Object obj = ext(k);
        return PrimitiveValueUtil.doubleValue(obj);
    }

    public long extl(String k) {
        Object obj = ext(k);
        return PrimitiveValueUtil.longValue(obj);
    }

    // _data
    public double[] d(String key) {
        return _data.d(key);
    }

    public double[] highs() {
        return _data.highs();
    }

    public double[] lows() {
        return _data.lows();
    }

    public double[] closes() {
        return _data.closes();
    }

    // current row
    protected Row get() {
        return _data.get();
    }

    // index by first
    protected Row get(int index) {
        return _data.get(index);
    }

    // index of the data rows
    protected Row row(int index) {
        return _data.row(index);
    }

    protected void add(String indicator, Object[] values) {
        if (null != indicator && null != values) {
            _data.addExt(indicator, values);
        }
    }

    protected void add(String indicator, double[] values) {
        if (null != indicator && null != values) {
            _data.addExt(indicator, values);
        }
    }

    // 初始化数据
    public void init() {
    }

    // 迭代
    public void next() {
    }

    // buy
    protected Event buy() {
        return buy(1, 0);
    }

    /**
     * buy
     *
     * @param ratio 比例
     * @param limit 限制
     */
    protected Event buy(double ratio, int limit) {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.BUY)
                .price(row.getClose()).ratio(ratio).limit(limit).row(row).build();
        this.notify(event);
        return event;
    }

    // sell
    protected Event sell() {
        return sell(1, 0);
    }

    /**
     * sell
     *
     * @param ratio 比例
     * @param limit 限制
     */
    protected Event sell(double ratio, int limit) {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.SELL)
                .price(row.getClose()).ratio(ratio).limit(limit).row(row).build();
        this.notify(event);
        return event;
    }

    /**
     * 平仓
     */
    protected Event close() {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.CLOSE)
                .price(row.getClose()).row(row).build();
        this.notify(event);
        return event;
    }

    /**
     * 取消订单 - 如果已经提交的订单还未成交则取消该订单
     */
    protected Event cancel() {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.CANCEL)
                .price(row.getClose()).row(row).build();
        this.notify(event);
        return event;
    }

    /**
     * 调仓到目标比例
     *
     * @param percent 取值[0,1]
     */
    protected Event targetPercent(double percent) {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.TARGET)
                .price(row.getClose()).targetPercent(percent).row(row).build();
        this.notify(event);
        return event;
    }

    // 发送event通知
    private Container<Event> _notify;

    protected void notify(Event event) {
        if (null != _notify) {
            this._notify.offer(event);
        }
    }
}
