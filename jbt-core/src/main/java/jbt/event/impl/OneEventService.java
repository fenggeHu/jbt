package jbt.event.impl;

import jbt.event.Event;
import jbt.event.EventService;

/**
 * @author max.hu  @date 2024/04/12
 **/
public class OneEventService implements EventService {
    private Event event;

    @Override
    public void add(Event event) {
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
