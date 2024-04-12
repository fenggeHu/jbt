package jbt.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 事件队列 - 收集事件信息
 *
 * @author jinfeng.hu  @date 2022/10/18
 **/
public class EventQueue {
    //
    private final Queue<Event> queue = new ConcurrentLinkedQueue<>();

    // insert
    public EventQueue offer(Event obj) {
        this.queue.offer(obj);
        return this;
    }

    // get & remove
    public Event poll() {
        return queue.poll();
    }

    private int maxSize = 100;

    // get all Or max size-防止持续的Event导致阻塞的bug
    public List<Event> pollAll() {
        List<Event> events = new ArrayList<>();
        while (!queue.isEmpty() && events.size() < maxSize) {
            events.add(queue.poll());
        }
        return events;
    }

    // clear
    public void clear() {
        this.queue.clear();
    }
}
