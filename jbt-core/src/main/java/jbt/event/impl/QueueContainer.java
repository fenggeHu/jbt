package jbt.event.impl;

import jbt.event.Container;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 默认通知的实现 - 队列
 *
 * @author max.hu  @date 2024/04/12
 **/
public class QueueContainer<T> implements Container<T> {
    private final Queue<T> _queue = new ConcurrentLinkedQueue();

    @Override
    public void offer(T event) {
        this._queue.offer(event);
    }

    @Override
    public T poll() {
        return this._queue.poll();
    }

    @Override
    public T get() {
        T val = this._queue.poll();
        this._queue.clear();
        return val;
    }

    // 所有
    public List<T> all() {
        List<T> ret = new LinkedList<>();
        while (!_queue.isEmpty()) {
            ret.add(_queue.poll());
        }
        return ret;
    }
}
