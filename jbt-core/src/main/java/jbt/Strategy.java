package jbt;

import jbt.account.Position;
import jbt.event.Event;
import jbt.event.EventQueue;
import jbt.event.OrderEvent;
import jbt.model.Action;
import jbt.model.Row;
import jbt.model.Sequence;
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
    private EventQueue _eventQueue;
    private Position _position;

    public Position position() {
        return this._position;
    }

    // 计算实时仓位的价值 - 有些策略需要用到
    public Position position(double price) {    // 传入价格计算
        this._position.compute(price);
        return this._position;
    }

    // 非Sequence的扩展属性
    private Map<String, Object> _ext = new HashMap<>();

    protected void addExt(String k, Object v) {
        _ext.put(k, v);
    }

    protected Object ext(String k) {
        return _ext.get(k);
    }

    protected boolean extb(String k) {
        Object obj = ext(k);
        return null == obj ? false : PrimitiveValueUtil.getAsBool(obj);
    }

    protected double extd(String k) {
        Object obj = ext(k);
        return null == obj ? 0.0 : PrimitiveValueUtil.getAsDouble(obj);
    }

    protected long extl(String k) {
        Object obj = ext(k);
        return null == obj ? 0 : PrimitiveValueUtil.getAsLong(obj);
    }

    // _data
    protected double[] d(String key) {
        return _data.d(key);
    }

    protected double[] highs() {
        return _data.highs();
    }

    protected double[] lows() {
        return _data.lows();
    }

    protected double[] closes() {
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
    protected void buy() {
        buy(1, 0);
    }

    /**
     * buy
     *
     * @param ratio 比例
     * @param limit 限制
     */
    protected void buy(double ratio, int limit) {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.BUY)
                .price(row.getClose()).ratio(ratio).limit(limit).build();
        event.setRow(row);
        _eventQueue.offer(event);
    }

    // sell
    protected void sell() {
        sell(1, 0);
    }

    /**
     * sell
     *
     * @param ratio 比例
     * @param limit 限制
     */
    protected void sell(double ratio, int limit) {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.SELL)
                .price(row.getClose()).ratio(ratio).limit(limit).build();
        event.setRow(row);
        _eventQueue.offer(event);
    }

    /**
     * 平仓
     */
    protected void close() {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.CLOSE)
                .price(row.getClose()).build();
        event.setRow(row);
        _eventQueue.offer(event);
    }

    /**
     * 取消订单 - 如果已经提交的订单还未成交则取消该订单
     */
    protected void cancel() {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.CANCEL)
                .price(row.getClose()).build();
        event.setRow(row);
        _eventQueue.offer(event);
    }

    /**
     * 调仓到目标比例
     *
     * @param percent 取值[0,1]
     */
    protected void targetPercent(double percent) {
        Row row = get();
        Event event = OrderEvent.builder().datetime(row.getDatetime()).action(Action.TARGET)
                .price(row.getClose()).targetPercent(percent).build();
        event.setRow(row);
        _eventQueue.offer(event);
    }
}
