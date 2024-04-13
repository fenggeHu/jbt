package jbt.event;

/**
 * 通知接口
 * @author max.hu  @date 2024/04/12
 **/
public interface EventService {
    void add(Event event);

    Event get();

    Event getAndClear();
}
