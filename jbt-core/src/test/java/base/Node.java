package base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jinfeng.hu  @Date 2022-10-07
 **/
@Getter
@Setter
public class Node {
    Number value;
    Node left;
    Node right;

    public static Node New(Number v) {
        Node n = new Node();
        n.setValue(v);
        return n;
    }
}
