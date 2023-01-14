package base;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jinfeng.hu  @Date 2022-10-07
 **/
public class Tree {
    @Getter
    @Setter
    Node root;

    public static Tree New() {
        return new Tree();
    }

    // Inserts the given value.
    public void insert(Number value) {
        Node newNode = Node.New(value);

        if (null == this.root) {
            this.root = newNode;
            return;
        }

        Node curNode = this.root;

        while (true) {
            // 比较2个数字
            if (compare(newNode.value, curNode.value) <= 0) {
                if (curNode.left == null) {
                    curNode.left = newNode;
                    return;
                } else {
                    curNode = curNode.left;
                }
            } else {
                if (curNode.right == null) {
                    curNode.right = newNode;
                    return;
                } else {
                    curNode = curNode.right;
                }
            }
        }
    }

    // 比较时四舍五入保留6位小数 // 使用float类型注意精度损失
    private int compare(Number v1, Number v2) {
        if (null == v1) {
            v1 = 0;
        }
        if (null == v2) {
            v2 = 0;
        }
        // 为避免转换的精度问题 - 数字类型转换成字符串再转成BigDecimal
        BigDecimal bd1 = new BigDecimal(v1.toString()).setScale(6, RoundingMode.HALF_UP);
        BigDecimal bd2 = new BigDecimal(v2.toString()).setScale(6, RoundingMode.HALF_UP);
        return bd1.compareTo(bd2);
    }

    // Removes the given value.
    public boolean remove(Number value) {
        Node parent = null;
        Node node = this.root;

        while (node != null) {
            switch (compare(value, node.value)) {
                case 0:
                    this.removeNode(parent, node);
                    return true;
                case -1:
                    parent = node;
                    node = node.left;
                    break;
                case 1:
                    parent = node;
                    node = node.right;
                    break;
            }
        }

        return false;
    }

    // Min value.
    public Number min() {
        Node node = minNode(this.root).getLeft();
        if (node == null) {
            return null;
        }

        return node.value;
    }

    // Max value.
    public Number max() {
        Node node = maxNode(this.root).getLeft();
        if (node == null) {
            return null;
        }

        return node.value;
    }

    // Remove node.
    public void removeNode(Node parent, Node node) {
        if (node.left != null && node.right != null) {
            Pair<Node, Node> pair = minNode(node.right);
            Node min = pair.getLeft();
            Node minParent = pair.getRight();
            if (minParent == null) {
                minParent = node;
            }

            this.removeNode(minParent, min);
            node.value = min.value;
        } else {
            Node child;
            if (node.left != null) {
                child = node.left;
            } else {
                child = node.right;
            }

            if (node == this.root) {
                this.root = child;
            } else if (parent.left == node) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        }
    }

    // Min node. Returns min node and its parent.
    public Pair<Node, Node> minNode(Node root) {
        if (root == null) {
            return Pair.of(null, null);
        }

        Node parent = null;
        Node node = root;

        while (node.left != null) {
            parent = node;
            node = node.left;
        }

        return Pair.of(node, parent);
    }

    // Max node. Returns max node and its parent.
    public Pair<Node, Node> maxNode(Node root) {
        if (root == null) {
            return Pair.of(null, null);
        }

        Node parent = null;
        Node node = root;

        while (node.right != null) {
            parent = node;
            node = node.right;
        }

        return Pair.of(node, parent);
    }
}
