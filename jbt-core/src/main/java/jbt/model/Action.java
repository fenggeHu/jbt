package jbt.model;

/**
 * Description:
 *
 * @author jinfeng.hu  @date 2022-10-06
 **/
public enum Action {
    SELL(-1),
    HOLD(0),
    BUY(1);
    private int value;

    public int value() {
        return value;
    }

    Action(int i) {
        this.value = i;
    }
}
