package jbt;

import jbt.event.EventQueue;
import jbt.model.Row;
import jbt.model.Sequence;
import lombok.Getter;
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
    // 事件
    @Getter
    protected final EventQueue eventQueue = new EventQueue();

    // Strategy绑定属性
    protected void injectionStrategyProperties() {
        // 初始化strategy
        if (null != strategy) {
            // 注入属性到strategy
            ClassUtils.silencedInjection(strategy, "_data", data);
            ClassUtils.silencedInjection(strategy, "_eventQueue", eventQueue);
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

}
