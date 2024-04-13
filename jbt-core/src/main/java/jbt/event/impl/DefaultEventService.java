package jbt.event.impl;

import jbt.event.Event;
import jbt.event.EventQueue;
import jbt.event.EventService;

/**
 * 默认通知的实现 - 队列
 * @author max.hu  @date 2024/04/12
 **/
public class DefaultEventService implements EventService {
    private EventQueue _eventQueue = new EventQueue();

    @Override
    public void add(Event event) {
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
