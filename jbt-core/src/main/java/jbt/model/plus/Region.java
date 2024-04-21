package jbt.model.plus;

/**
 * @author jinfeng.hu  @Date 2022/8/11
 * @Description: 市场地区
 **/
public enum Region {
    UNKNOWN(0), CN(1), US(2), HK(3);

    private int value;

    Region(int i) {
        this.value = i;
    }

    public int value() {
        return value;
    }

    public static boolean isValid(Integer in) {
        if (null == in) return false;
        int n = in.intValue();
        return n == 1 || n == 2 || n == 3;
    }
}
