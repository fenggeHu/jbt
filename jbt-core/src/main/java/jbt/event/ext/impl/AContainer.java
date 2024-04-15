package jbt.event.ext.impl;

import jbt.event.ext.Container;

/**
 * 一个值
 * @author max.hu  @date 2024/04/12
 **/
public class AContainer<T> implements Container<T> {
    private T value;

    @Override
    public void offer(T val) {
        this.value = val;
    }

    @Override
    public T poll() {
        return this.value;
    }

    @Override
    public T get() {
        T val = this.value;
        this.value = null;
        return val;
    }
}
