package jbt.event;

/**
 * 通知容器接口
 *
 * @author max.hu  @date 2024/04/12
 **/
public interface Container<T> {
    /**
     * 写入
     */
    void offer(T event);

    /**
     * 提取一个
     */
    T poll();

    /**
     * 取出一个后重置
     */
    T get();
}
