package jbt;

import jbt.account.Account;
import jbt.event.Event;
import jbt.event.EventQueue;
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
import utils.ClassUtils;

/**
 * 执行引擎
 * - 非线程安全，一个engine实例不能多线程并发调用，一个线程一个engine实例，估可以设计engine线程池
 *
 * @author jinfeng.hu  @date 2022/10/27
 **/
@Slf4j
public class Engine {
    // Sequence Row.datetime的格式
    @Setter
    private String datetimeFormat = "yyyy-MM-dd";
    // 数据
    @Setter
    protected Sequence data;
    // 回测起始时间 - 默认为空则忽略
    @Setter
    private String start;
    // 回测结束时间 - 默认为空则忽略
    @Setter
    private String end;
    @Setter
    protected Strategy strategy;
    @Getter
    private Account account;
    // 事件
    @Getter
    private final EventQueue eventQueue = new EventQueue();
    // trade handler
    @Setter
    @Getter
    private TradeHandler tradeHandler;
    // 表现评估
    @Setter
    @Getter
    private PerformanceHandler performanceHandler;

    // get engine
    public static Engine build(Strategy strategy, String start, String end) {
        Engine engine = new Engine(strategy);
        engine.setStart(start);
        engine.setEnd(end);
        return engine;
    }

    // get engine
    public static Engine build(Strategy strategy, String start, String end, double principal) {
        Engine engine = new Engine(strategy);
        engine.setStart(start);
        engine.setEnd(end);
        if (principal > 0) {
            engine.account = new Account();
            engine.account.setPrincipal(principal);
        }
        return engine;
    }

    // get engine
    public static Engine build(Strategy strategy, String start, String end, Sequence data) {
        Engine engine = new Engine(strategy, data);
        engine.setStart(start);
        engine.setEnd(end);
        return engine;
    }

    public Engine() {
    }

    @SneakyThrows
    public Engine(Class<? extends Strategy> strategy) {
        this.strategy = strategy.newInstance();
    }

    public Engine(Strategy strategy) {
        this.strategy = strategy;
    }

    public Engine(Strategy strategy, final Sequence data) {
        this.data = data;
        this.strategy = strategy;
    }

    public Engine(Strategy strategy, final Sequence data, Account account) {
        this.data = data;
        this.strategy = strategy;
        this.account = account;
    }

    public Engine(Strategy strategy, final Sequence data, double principal) {
        this.data = data;
        this.strategy = strategy;
        this.account = new Account();
        this.account.setPrincipal(principal);
    }

    // run
    public Stats run(final Sequence data) {
        return this.run(data, null);
    }

    // run
    public Stats run(final Sequence data, Account account) {
        this.data = data;
        this.account = account;
        this.tradeHandler = null;
        this.eventQueue.clear();
        return this.run();
    }

    // run main
    @SneakyThrows
    public Stats run() {
        if (null == this.data) {
            throw new RuntimeException("no data");
        }
        //
        this.init();
        //
        this.preNext();
        //
        Row start = this.data.row(1);
        // run strategy
        while (this.next()) {
            Event event = eventQueue.poll();
            if (null != event) {
                this.notify(event);
            }
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

        return performanceHandler.run(this.data);
    }

    // 预处理&初始化必要的属性
    protected void init() {
        // 初始化account
        if (null == account) {
            this.account = new Account();
        }
        // 初始化trade handler
        if (null == tradeHandler) {
            tradeHandler = new TradeHandler(account);
        }
        // 初始化strategy
        if (null != strategy) {
            // 注入属性到strategy
            ClassUtils.silencedInjection(strategy, "_data", data);
            ClassUtils.silencedInjection(strategy, "_eventQueue", eventQueue);
            ClassUtils.silencedInjection(strategy, "_position", tradeHandler.getPosition());
        }
    }

    // 数据准备
    protected void preNext() {
        if (null != strategy) {
            //
            strategy.init();
            //
            data.range(start, end);
        }
    }

    // 读取数据行，执行策略逻辑
    protected boolean next() {
        Row row = data.next();
        if (null == row) {
            log.debug("Engine run out");
            return false;
        }
        // 操作信号
        if (null != this.strategy) {
            strategy.next();
        }

        return true;
    }

    // 处理事件
    protected void notify(Event e) {
        if (log.isDebugEnabled()) {
            log.debug("Notify: {}", e);
        }
        if (e instanceof OrderEvent) {
            // 处理订单
            this.tradeHandler.run((OrderEvent) e);
        }
    }

}
