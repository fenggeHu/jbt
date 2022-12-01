package jbt;

import jbt.account.Account;
import jbt.account.Position;
import jbt.event.Event;
import jbt.event.EventQueue;
import jbt.event.OrderEvent;
import jbt.handler.TradeHandler;
import jbt.model.Row;
import jbt.model.Sequence;
import jbt.model.Stats;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import utils.ClassUtils;
import utils.DatetimeUtils;

/**
 * 执行引擎
 * - 非线程安全，一个engine实例不能多线程并发调用，一个线程一个engine实例，估可以设计engine线程池
 *
 * @author jinfeng.hu  @Date 2022/10/27
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
    @Setter
    @Getter
    private Stats stats;

    // get engine
    public static Engine build(Strategy strategy, String start, String end) {
        Engine engine = new Engine(strategy);
        engine.setStart(start);
        engine.setEnd(end);
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

    // run
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
        if (null == stats) {
            stats = new Stats();
        }
        Row r1 = this.data.row(1);
        stats.setStart(r1.getDatetime());
        // run strategy
        while (this.next()) {
            Event event = eventQueue.poll();
            if (null != event) {
                this.notify(event);
            }
        }

        // 收集信息
        Row end = this.data.get();
        stats.setEnd(end.getDatetime());
        long d1 = DatetimeUtils.parseDate(stats.getStart()).getTime();
        long d2 = DatetimeUtils.parseDate(stats.getEnd()).getTime();
        stats.setDuration((d2 - d1) / 86400000.00); // days
        if (null != this.tradeHandler) {
            Position pos = tradeHandler.getPosition().compute(data.get().getClose());
            stats.setPosition(pos);
            stats.setTrades(pos.getBills().size());
            stats.setTotalReturn(pos.getPercent());
        }

        return stats;
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
            log.info("Engine run out");
            return false;
        }
        // 操作信号
        if (null != this.strategy) {
            strategy.next();
        }
        // 计算stats
        if (null != this.stats) {
            if (null != this.tradeHandler) {
                Position position = this.tradeHandler.getPosition().compute(row.getClose());
                double maxDraw = this.stats.getMaxDrawdown();
                this.stats.setMaxDrawdown(Math.min(maxDraw, position.getPercent()));
            }
        }

        return true;
    }

    // 处理事件
    protected void notify(Event e) {
        log.debug("{}", e);
        if (e instanceof OrderEvent) {
            // 处理订单
            this.tradeHandler.run((OrderEvent) e);
        }
    }

}
