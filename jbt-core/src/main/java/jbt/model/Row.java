package jbt.model;

import jbt.constant.RowPropertyEnum;
import lombok.Data;
import utils.PrimitiveValueUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * a row
 *
 * @author jinfeng.hu  @date 2022/10/10
 **/
@Data
public class Row {
    // 时间字符串 - 可自定义格式 - 默认yyyy-MM-dd
    public String datetime;
    public double open;
    public double close;
    public double high;
    public double low;
    // size or money
    public long volume;
    // 自定义的扩展属性
    private Map<String, Object> _ext = new HashMap<>();

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
            if (PrimitiveValueUtil.getAsDouble(o, 0) == 0) return true;
        }
        return false;
    }

    /**
     * get object
     */
    public Object o(String key) {
        if (RowPropertyEnum.O.getKey().equals(key)) {
            return open;
        } else if (RowPropertyEnum.H.getKey().equals(key)) {
            return high;
        } else if (RowPropertyEnum.L.getKey().equals(key)) {
            return low;
        } else if (RowPropertyEnum.C.getKey().equals(key)) {
            return close;
        } else if (RowPropertyEnum.V.getKey().equals(key)) {
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
        return RowPropertyEnum.basicTitle();
    }
}
