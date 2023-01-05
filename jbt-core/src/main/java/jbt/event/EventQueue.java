package jbt.event;

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

    // clear
    public void clear() {
        this.queue.clear();
    }
}
