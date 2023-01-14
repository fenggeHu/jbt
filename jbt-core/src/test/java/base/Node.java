package base;

import lombok.Data;

/**
 * @author jinfeng.hu  @Date 2022-10-07
 **/
@Data
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
