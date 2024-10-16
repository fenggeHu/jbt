package jbt;

import jbt.event.Event;
import jbt.model.Bar;
import jbt.model.Sequence;

/**
 * 右侧执行引擎 - run最新数据的策略，返回最多一个信号
 * 可用于实时触发和最新数据寻找机会
 * 使用方式一：
 * 使用策略和数据初始化，并使用最右端行情执行策略
 * 使用方式二：
 * 1，使用策略和历史数据初始化REngine
 * 2，接收最新的实时行情，并执行策略
 * <p>
 * 当服务器的内存够用时，使用第二种方式可提高性能。
 *
 * @author jinfeng.hu  @date 2022/10/31
 **/
public class REngine extends EngineCore {

    /**
     * build - 构建REngine & init
     */
    public static REngine build(Strategy strategy, final Sequence data) {
        REngine engine = new REngine(strategy, data);
        engine.init();
        return engine;
    }

    public REngine(Strategy strategy) {
        this.strategy = strategy;
    }

    public REngine(Strategy strategy, final Sequence data) {
        this.strategy = strategy;
        this.data = data;
    }

    /**
     * run最新的1行数据
     */
    public Event run() {
        // 数据预处理
        this.prepare();
        // 定位到倒数第二行
        this.data.toSecondLast();
        // 执行策略
        this.next();
        // 处理事件
        Event event = this.eventContainer.get();
        return event;
    }

    /**
     * 支持接收实时行情并执行策略 - feed last data & play
     */
    public Event run(Bar lastBar) {
        this.updateLast(lastBar);
        return run();
    }

    // 更新最近的数据
    private void updateLast(Bar lastBar) {
        Bar bar = this.data.last();
        int ct = bar.getDatetime().compareTo(lastBar.getDatetime());
        if (ct > 0) {
            throw new RuntimeException(String.format("it's not the last datetime. %s", lastBar.getDatetime()));
        }
        // 替换最新row - 修改Sequence data
        if (ct == 0) {
            bar.setOpen(lastBar.getOpen());
            bar.setHigh(lastBar.getHigh());
            bar.setLow(lastBar.getLow());
            bar.setClose(lastBar.getClose());
            bar.setVolume(lastBar.getVolume());
        } else {
            Bar[] bars = this.data.get_bars();
            Bar[] nrs = new Bar[bars.length + 1];
            System.arraycopy(bars, 0, nrs, 0, bars.length);
            nrs[bars.length] = lastBar;
            this.data.init(nrs);
        }
    }
}
