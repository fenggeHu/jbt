package base;

import java.io.Serializable;

/**
 * 返回5个值
 *
 * @author jinfeng.hu  @Date 2022/10/8
 **/
public class Quintuple<A, B, C, D, E> implements Serializable {
    private static final long serialVersionUID = 1000202210085L;
    public final A first;
    public final B second;
    public final C third;
    public final D forth;
    public final E fifth;

    public Quintuple(A first, B second, C third, D forth, E fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.forth = forth;
        this.fifth = fifth;
    }

    public static <A, B, C, D, E> Quintuple<A, B, C, D, E> of(A one, B two, C three, D four, E five) {
        return new Quintuple<>(one, two, three, four, five);
    }

    public A first() {
        return this.first;
    }

    public B second() {
        return this.second;
    }

    public C third() {
        return this.third;
    }

    public D forth() {
        return this.forth;
    }

    public E fifth() {
        return this.fifth;
    }

    public E last() {
        return this.fifth;
    }

}
