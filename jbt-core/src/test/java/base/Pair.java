package base;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author jinfeng.hu  @Date 2022/10/8
 **/
@Getter
public class Pair<L, R> implements Serializable {
    private static final long serialVersionUID = 1000202210082L;
    public final L left;
    public final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair(left, right);
    }
}
