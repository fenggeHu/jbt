package jbt.notify;

/**
 * 通知接口
 * @author max.hu  @date 2024/04/12
 **/
public interface Notify<T> {
    void add(T event);

    T get();

    T getAndClear();
}
