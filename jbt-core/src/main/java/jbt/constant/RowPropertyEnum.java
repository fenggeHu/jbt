package jbt.constant;

import lombok.Getter;

/**
 * row的一些常用的属性
 *
 * @author max.hu  @date 2024/02/04
 **/
public enum RowPropertyEnum {
    /**
     * basic - row基本必须的属性
     */
    D("datetime"), O("open"), H("high"), L("low"), C("close"), V("volume"),
    /**
     * expend 扩展/计算的属性
     */
    PreC("preClose"),
    ;
    @Getter
    String key;

    RowPropertyEnum(String v) {
        this.key = v;
    }

    public static String basicTitle() {
        return "datetime,open,high,low,close,volume";
    }
}
