package jbt.notify.impl;

import jbt.notify.Event;
import jbt.notify.Notify;

/**
 * @author max.hu  @date 2024/04/12
 **/
public class OneEventNotify implements Notify<Event> {
    private Event event;

    @Override
    public void send(Event event) {
        this.event = event;
    }

    @Override
    public Event get() {
        return this.event;
    }

    @Override
    public Event getAndClear() {
        Event event = this.event;
        this.event = null;
        return event;
    }
}
