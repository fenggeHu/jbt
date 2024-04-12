package jbt;

import jbt.account.Account;
import jbt.event.Event;
import jbt.event.OrderEvent;
import jbt.handler.PerformanceHandler;
import jbt.handler.TradeHandler;
import jbt.model.Row;
import jbt.model.Sequence;
import jbt.model.Stats;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 执行引擎 - 基本能力和支持回测
 * - 非线程安全，一个engine实例不能多线程并发调用，一个线程一个engine实例，估可以设计engine线程池
 *
 * @author jinfeng.hu  @date 2022/10/27
 **/
@Slf4j
public class BacktestEngine extends EngineCore {
    @Getter
    private Account account;
    // trade handler
    @Setter
    @Getter
    private TradeHandler tradeHandler;
    // 表现评估
    @Setter
    @Getter
    private PerformanceHandler performanceHandler;

    // get engine
    public static BacktestEngine build(Strategy strategy, String start, String end) {
        BacktestEngine backtestEngine = new BacktestEngine(strategy);
        backtestEngine.setStart(start);
        backtestEngine.setEnd(end);
        // init
        backtestEngine.initTradeHandler();
        return backtestEngine;
    }

    // get engine
    public static BacktestEngine build(Strategy strategy, String start, String end, double principal) {
        BacktestEngine backtestEngine = new BacktestEngine(strategy);
        backtestEngine.setStart(start);
        backtestEngine.setEnd(end);
        if (principal > 0) {
            backtestEngine.account = new Account();
            backtestEngine.account.setPrincipal(principal);
        }
        // init
        backtestEngine.initTradeHandler();
        return backtestEngine;
    }

    // get engine
    public static BacktestEngine build(Strategy strategy, String start, String end, Sequence data) {
        BacktestEngine backtestEngine = new BacktestEngine(strategy, data);
        backtestEngine.setStart(start);
        backtestEngine.setEnd(end);
        // init
        backtestEngine.initTradeHandler();
        return backtestEngine;
    }

    public BacktestEngine() {
    }

    @SneakyThrows
    public BacktestEngine(Class<? extends Strategy> strategy) {
        this.strategy = strategy.newInstance();
    }

    public BacktestEngine(Strategy strategy) {
        this.strategy = strategy;
    }

    public BacktestEngine(Strategy strategy, final Sequence data) {
        this.data = data;
        this.strategy = strategy;
    }

    public BacktestEngine(Strategy strategy, final Sequence data, Account account) {
        this.data = data;
        this.strategy = strategy;
        this.account = account;
    }

    public BacktestEngine(Strategy strategy, final Sequence data, double principal) {
        this.data = data;
        this.strategy = strategy;
        this.account = new Account();
        this.account.setPrincipal(principal);
    }

    /**
     * 按顺序播放数据序列
     */
    public Stats play(final Sequence data) {
        return this.play(data, null);
    }

    /**
     * 按顺序播放数据序列
     */
    public Stats play(final Sequence data, Account account) {
        this.data = data;
        this.account = account;
        this.eventQueue.clear();
        return this.play();
    }

    /**
     * main - 按顺序播放数据序列
     */
    @SneakyThrows
    public Stats play() {
        if (null == this.data) {
            throw new RuntimeException("no data");
        }
        //
        this.injectionStrategyProperties();
        //
        this.preNext();
        //
        Row start = this.data.row(1);
        // run strategy
        while (this.next()) {
            Event event = eventQueue.poll();
            this.notify(event);

            // after - 后置处理-策略和事件处理后
            this.after();
        }

        // 收集信息
        if (null == performanceHandler) {
            performanceHandler = new PerformanceHandler();
        }
        performanceHandler.setStart(start);
        performanceHandler.setEnd(this.data.get());
        if (null != this.tradeHandler) {
            performanceHandler.setPosition(tradeHandler.getPosition().trim());
        }

        return performanceHandler.apply(this.data);
    }

    // 预处理&初始化必要的属性
    protected void initTradeHandler() {
        // 初始化account
        if (null == account) {
            this.account = new Account();
        }
        // 初始化trade handler
        if (null == tradeHandler) {
            tradeHandler = new TradeHandler(account);
        }
    }

    // strategy策略执行后的操作
    protected void after() {
        // 交易策略 - 触发止损/止盈/减仓/平仓/仓位平衡等
        if (null != this.tradeHandler) {
            Event event = this.tradeHandler.after(data);
            this.notify(event);
        }
    }

    // 处理事件
    protected void notify(Event e) {
        if (null == e) return;

        if (log.isDebugEnabled()) {
            log.debug("Notify: {}", e);
        }
        if (e instanceof OrderEvent) {
            // 处理订单
            if (null != this.tradeHandler) {
                this.tradeHandler.apply((OrderEvent) e);
            }
        }
    }

}
