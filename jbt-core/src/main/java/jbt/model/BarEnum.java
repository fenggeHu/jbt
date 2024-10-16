package jbt.model;

import lombok.Getter;

/**
 * row的一些常用的属性
 *
 * @author max.hu  @date 2024/02/04
 **/
public enum BarEnum {
    /**
     * basic - row基本必须的属性。 D-OHLCV
     */
    D("datetime"), O("open"), H("high"), L("low"), C("close"), V("volume"),
    /**
     * expend 扩展/计算的属性
     */
    PreC("preClose"),
    ;
    @Getter
    String key;

    BarEnum(String v) {
        this.key = v;
    }

}
