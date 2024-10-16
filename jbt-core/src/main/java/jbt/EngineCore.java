package jbt;

import jbt.event.Container;
import jbt.event.Event;
import jbt.event.impl.AContainer;
import jbt.model.Bar;
import jbt.model.Sequence;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import utils.ClassUtils;

/**
 * 执行引擎 - 基本能力和支持回测
 * - 非线程安全，一个engine实例不能多线程并发调用，一个线程一个engine实例，估可以设计engine线程池
 *
 * @author jinfeng.hu  @date 2022/10/27
 **/
@Slf4j
public class EngineCore {
    // 数据
    @Setter
    protected Sequence data;
    // 回测起始时间 - 默认为空则忽略
    @Setter
    protected String start;
    // 回测结束时间 - 默认为空则忽略
    @Setter
    protected String end;
    @Setter
    protected Strategy strategy;
    // 事件容器 - 存储event
    @Setter
    protected Container<Event> eventContainer;

    // Strategy绑定属性
    protected void init() {
        // 初始化strategy
        if (null != strategy) {
            // 注入属性到strategy
            ClassUtils.silencedInjection(strategy, "_data", data);
            if (null == eventContainer) {
                eventContainer = new AContainer();
            }
            ClassUtils.silencedInjection(strategy, "_notify", eventContainer);
        }
    }

    // 数据准备
    protected void prepare() {
        if (null != strategy) {
            //
            strategy.init();
            //
            data.range(start, end);
        }
    }

    // 读取数据行，执行策略逻辑
    protected boolean next() {
        Bar bar = data.next();
        if (null == bar) {
            log.debug("Engine run out");
            return false;
        }
        // 操作信号
        if (null != this.strategy) {
            strategy.next();
        }

        return true;
    }

}
