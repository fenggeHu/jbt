package jbt.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * 数据序列
 *
 * @author jinfeng.hu  @Date 2022/10/18
 **/
@Slf4j
public class Sequence {
    // 默认忽略0值 - 针对无效的double/long为0的值
    @Setter
    private boolean ignoreZero = true;
    @Getter
    private Row[] _rows;
    // 起点 - rows数组index
    protected int first = 0;
    // 终点 - rows数组index
    protected int end = 0;
    // 当前point对应的rows数组index，调用next行号+1
    @Getter
    protected int point = -1;

    // build a Sequence
    public static Sequence build(Row[] rows) {
        return new Sequence().init(rows);
    }

    public static Sequence build(Collection<Row> rows) {
        return new Sequence().init(rows.toArray(new Row[0]));
    }

    /**
     * get object
     */
    public Object[] o(String key) {
        Object[] ret = new Object[_rows.length];
        if (key.equals("open")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].open;
            }
        } else if (key.equals("high")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].high;
            }
        } else if (key.equals("low")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].low;
            }
        } else if (key.equals("close")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].close;
            }
        } else if (key.equals("volume")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].volume;
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].o(key);
            }
        }
        return ret;
    }

    /**
     * get double
     */
    public double[] d(String key) {
        double[] ret = new double[_rows.length];
        if (key.equals("open")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].open;
            }
        } else if (key.equals("high")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].high;
            }
        } else if (key.equals("low")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].low;
            }
        } else if (key.equals("close")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].close;
            }
        } else if (key.equals("volume")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].volume;
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _rows[i].d(key);
            }
        }
        return ret;
    }

    public double[] closes() {
        return d("close");
    }

    // 初始化数据
    public Sequence init(Row[] rs) {
        this._rows = rs;
        this.first = 0;
        this.end = rs.length - 1;
        this.point = -1;
        return this;
    }

    // 加入扩展/自定义属性
    public void addExt(String key, double[] obs) {
        if (obs.length != _rows.length) {
            throw new RuntimeException(String.format("%s行数[%d]不一致,原数据行%d", key, obs.length, _rows.length));
        }
        for (int i = 0; i < _rows.length; i++) {
            _rows[i].setExt(key, obs[i]);
        }
    }

    // 加入扩展/自定义属性
    public void addExt(String key, Object[] obs) {
        if (obs.length != _rows.length) {
            throw new RuntimeException(String.format("%s行数[%d]不一致,原数据行%d", key, obs.length, _rows.length));
        }
        for (int i = 0; i < _rows.length; i++) {
            _rows[i].setExt(key, obs[i]);
        }
    }

    // 滚动到下一行，并返回行数据
    public Row next() {
        if (this.point + this.first == this.end) {
            // 数据到底了 next为空
            return null;
        }
        this.point++;
        return get();
    }

    // 返回当前行
    public Row get() {
        return get(0);
    }

    // 以当前point为0点，读取相对位置的数据
    public Row get(int i) {
        int count = this.count();
        if (0 == count) {
            return null;
        }
        int index = point + i;
        if (index < 0) { // 向前追溯行
            index = count - (-1 * index) % count;
        } else if (index >= count) {
            index = index % count;
        }
        return _rows[index + first];
    }

    // 当前行的绝对行 - 不考虑起点first, 有时候要取绝对位置
    public Row row(int i) {
        int index = point + first + i;
        if (index < 0 || index >= this._rows.length) {
            log.warn("index out of _rows range(0~{}). index: {}", this._rows.length, index);
            return null;
        }
        return _rows[index];
    }

    // 指定范围，排除含null的行，把数据对齐
    public int range(String startDatetime, String endDatetime) {
        int start = _rows.length - 1;
        for (int i = 0; i < _rows.length; i++) {
            if (null != startDatetime && startDatetime.compareTo(_rows[i].datetime) > 0) {
                continue;
            }
            if (ignoreZero) {  // 忽略扩展属性的null和0
                if (!_rows[i].extNaNOrZero()) {
                    start = i;
                    break;
                }
            } else if (!_rows[i].extNaN()) {    // 判断扩展属性是否有null
                start = i;
                break;
            }
        }

        int end = _rows.length - 1;
        if (null != endDatetime) {
            for (int i = _rows.length - 1; i >= 0; i--) {
                if (endDatetime.compareTo(_rows[i].datetime) < 0) {
                    continue;
                }
                end = i;
                break;
            }
        }
        this.range(start, end);
        return start;
    }

    // 指定起点/终点位置
    public void range(int start, int end) {
        if (start >= _rows.length) {
            throw new RuntimeException(String.format("drop: %d > data count: %d", start, _rows.length));
        }
        this.first = start;
        this.end = end;
    }

    // 获取行数组的最后一行
    public Row last() {
        return this._rows[_rows.length - 1];
    }

    // 重置到初始数据位
    public void reset() {
        this.point = first - 1;
    }

    // 移动point最后 - 倒数第2个位置
    public void toEnd() {
        this.point = this.end - this.first - 1;
    }

    // TODO - 暂无应用
    private void to(int pos) {
        this.point = pos + first;
    }

    // 从起始位置到数组结尾的Row个数
    public int count() {
        if (null == _rows) {
            return 0;
        } else {
            return _rows.length - first;
        }
    }

    // rows总行数
    public int length() {
        if (null == _rows) {
            return 0;
        } else {
            return _rows.length;
        }
    }

    // 含有效时间的行数
    public int size() {
        return this.end - this.first + 1;
    }
}
