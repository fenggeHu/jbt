package jbt.data.ext;

/**
 * @author max.hu  @date 2024/12/19
 **/
@FunctionalInterface
public interface Function3<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3);
}
