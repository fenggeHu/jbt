package jbt.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import utils.PrimitiveValueUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * a row / Bar
 *
 * @author jinfeng.hu  @date 2022/10/10
 **/
@Data
@SuperBuilder
@NoArgsConstructor
public class Bar {
    // 时间字符串 - 可自定义格式 - 默认yyyy-MM-dd
    public String datetime;
    public double open;
    public double close;
    public double high;
    public double low;
    // size or money - default money/turnover
    public long volume;
    // 2024.9
    public double change;   // 涨跌额 (Change in Price)
    public double changeRate;   // 涨跌幅 (Change Percentage)  (%)
    public double turnoverRatio; // 换手率 (Turnover Rate)  (%)
    public double amplitude;    // 振幅  (%)
    // 自定义的扩展属性
    @Builder.Default        // 避免在使用build时此属性未被初始化
    protected Map<String, Object> _ext = new HashMap<>();

    // 构造函数
    public Bar(String datetime, double open, double high, double low, double close, long volume) {
        this.datetime = datetime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public Bar(String datetime, double open, double high, double low, double close, long volume,
               double change, double changeRate, double turnoverRatio, double amplitude) {
        this.datetime = datetime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.change = change;
        this.changeRate = changeRate;
        this.turnoverRatio = turnoverRatio;
        this.amplitude = amplitude;
    }

    public void setExt(String key, Object obj) {
        this._ext.put(key, obj);
    }

    // 判断 _ext是否存在null
    public boolean extNaN() {
        for (Object o : _ext.values()) {
            if (null == o) return true;
        }
        return false;
    }

    // 判断 _ext是否存在null或为0
    public boolean extNaNOrZero() {
        for (Object o : _ext.values()) {
            if (null == o) return true;
            if (o instanceof Number) {
                if (PrimitiveValueUtil.getAsDouble(o, 0) == 0) return true;
            }
        }
        return false;
    }

    // 做一下类型转换
    public <T> T o(String key, Class<T> tClass) {
        Object obj = this.o(key);
        return tClass.cast(obj);
    }

    /**
     * get object
     */
    public Object o(String key) {
        if (BarEnum.O.getKey().equals(key)) {
            return open;
        } else if (BarEnum.H.getKey().equals(key)) {
            return high;
        } else if (BarEnum.L.getKey().equals(key)) {
            return low;
        } else if (BarEnum.C.getKey().equals(key)) {
            return close;
        } else if (BarEnum.V.getKey().equals(key)) {
            return volume;
        }
        return _ext.containsKey(key) ? _ext.get(key) : null;
    }

    /**
     * get double
     */
    public double d(String key) {
        return d(key, 0.0);
    }

    /**
     * get double
     */
    public double d(String key, double def) {
        Object obj = o(key);
        if (null == obj) {
            return def;
        }
        return PrimitiveValueUtil.getAsDouble(obj);
    }

    /**
     * get long
     */
    public long l(String key) {
        return PrimitiveValueUtil.getAsLong(o(key));
    }

    public String title() {
        return "datetime,open,high,low,close,volume,amplitude,percentage,change,turnoverRate";
    }

    // 分隔符
    public static String delimiter = ",";

    // to a line
    public String line() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getDatetime()).append(delimiter)
                .append(this.getOpen()).append(delimiter)
                .append(this.getHigh()).append(delimiter)
                .append(this.getLow()).append(delimiter)
                .append(this.getClose()).append(delimiter)
                .append(this.getVolume()).append(delimiter)
                .append(this.amplitude).append(delimiter)
                .append(this.changeRate).append(delimiter)
                .append(this.change).append(delimiter)
                .append(this.turnoverRatio)
        ;
        return sb.toString();
    }

    public static Bar of(String line) {
        String[] vs = line.split(delimiter);
        Bar bar = new Bar();
        bar.setDatetime(vs[0]);
        bar.setOpen(PrimitiveValueUtil.toDouble(vs[1]));
        bar.setHigh(PrimitiveValueUtil.toDouble(vs[2]));
        bar.setLow(PrimitiveValueUtil.toDouble(vs[3]));
        bar.setClose(PrimitiveValueUtil.toDouble(vs[4]));
        bar.setVolume(PrimitiveValueUtil.toLong(vs[5]));
        // 2.0 扩充
        if (vs.length > 6) bar.setAmplitude(PrimitiveValueUtil.toDouble(vs[6]));
        if (vs.length > 7) bar.setChangeRate(PrimitiveValueUtil.toDouble(vs[7]));
        if (vs.length > 8) bar.setChange(PrimitiveValueUtil.toDouble(vs[8]));
        if (vs.length > 9) bar.setTurnoverRatio(PrimitiveValueUtil.toDouble(vs[9]));

        return bar;
    }
}
