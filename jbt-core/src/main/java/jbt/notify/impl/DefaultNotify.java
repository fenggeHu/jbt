package jbt.notify.impl;

import jbt.notify.Event;
import jbt.notify.EventQueue;
import jbt.notify.Notify;

/**
 * 默认通知的实现 - 队列
 * @author max.hu  @date 2024/04/12
 **/
public class DefaultNotify implements Notify<Event> {
    private EventQueue _eventQueue = new EventQueue();

    @Override
    public void send(Event event) {
        this._eventQueue.offer(event);
    }

    @Override
    public Event get() {
        return this._eventQueue.poll();
    }

    @Override
    public Event getAndClear() {
        Event event = this._eventQueue.poll();
        this._eventQueue.clear();
        return event;
    }
}
