package jbt.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据序列
 *
 * @author jinfeng.hu  @date 2022/10/18
 **/
@Slf4j
public class Sequence {
    // 默认忽略0值 - 针对无效的double/long为0的值
    @Setter
    private boolean ignoreZero = true;
    @Getter
    private Bar[] _bars;
    // 起点，绝对位置 - rows数组index
    private int first = 0;
    // 终点，绝对位置 - rows数组index
    private int end = 0;
    // 指针，相对位置 - 从first起计数为0
    private int point = -1;

    // build a Sequence
    public static Sequence build(Bar[] bars) {
        return new Sequence().init(bars);
    }

    public static Sequence build(Collection<Bar> bars) {
        return new Sequence().init(bars.toArray(new Bar[0]));
    }

    /**
     * get object
     */
    public Object[] o(String key) {
        Object[] ret = new Object[_bars.length];
        if (key.equals("open")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].open;
            }
        } else if (key.equals("high")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].high;
            }
        } else if (key.equals("low")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].low;
            }
        } else if (key.equals("close")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].close;
            }
        } else if (key.equals("volume")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].volume;
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].o(key);
            }
        }
        return ret;
    }

    /**
     * get double
     */
    public double[] d(String key) {
        double[] ret = new double[_bars.length];
        if (key.equals("open")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].open;
            }
        } else if (key.equals("high")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].high;
            }
        } else if (key.equals("low")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].low;
            }
        } else if (key.equals("close")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].close;
            }
        } else if (key.equals("volume")) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].volume;
            }
        } else {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = _bars[i].d(key);
            }
        }
        return ret;
    }

    public double[] highs() {
        return d("high");
    }

    public double[] lows() {
        return d("low");
    }

    public double[] closes() {
        return d("close");
    }

    public double[] volumes() {
        return d("volume");
    }

    // 初始化数据
    public Sequence init(Bar[] rs) {
        this._bars = rs;
        this.first = 0;
        this.end = rs.length - 1;
        this.point = -1;
        return this;
    }

    // 加入扩展/自定义属性
    public void addExt(String key, double[] obs) {
        if (obs.length != _bars.length) {
            throw new RuntimeException(String.format("%s行数[%d]不一致,原数据行%d", key, obs.length, _bars.length));
        }
        for (int i = 0; i < _bars.length; i++) {
            _bars[i].setExt(key, obs[i]);
        }
    }

    // 加入扩展/自定义属性
    public void addExt(String key, Object[] obs) {
        if (obs.length != _bars.length) {
            throw new RuntimeException(String.format("%s行数[%d]不一致,原数据行%d", key, obs.length, _bars.length));
        }
        for (int i = 0; i < _bars.length; i++) {
            _bars[i].setExt(key, obs[i]);
        }
    }

    // 滚动到下一行，并返回行数据
    public Bar next() {
        if (this.point + this.first == this.end) {
            // 数据到底了 next为空
            return null;
        }
        this.point++;
        return get();
    }

    // 返回当前行
    public Bar get() {
        return get(0);
    }

    // 取range范围内的相对位置 - 以当前point为0点，读取相对位置的数据
    public Bar get(int i) {
        int size = this.size();
        if (0 == size) {
            return null;
        }
        int index = point + i;
        if (index < 0) { // 环状--向前追溯行
            index = size - (-1 * index) % size;
        } else if (index >= size) { // 环状--向后追溯
            index = index % size;
        }
        try {
            return _bars[index + first];
        } catch (Exception e) {
            log.error("rowLen:{}, index:{}, first:{}, i:{}, point:{}, end:{}", _bars.length, index, first, i, point, this.end);
            throw new RuntimeException(e);
        }
    }

    // 当前的绝对位置的index
    private int absoluteIndex() {
        return this.point + this.first;
    }

    // 取绝对位置 - 从当前行开始计算 - 不考虑起点first, 有时候要取绝对位置
    public Bar row(int i) {
        int index = this.absoluteIndex() + i;
        if (index < 0 || index >= this._bars.length) {
            // warn -> debug
            log.debug("index out of _rows range(0~{}). index: {}", this._bars.length, index);
            return null;
        }
        return _bars[index];
    }

    // 获取行数组的最后一行
    public Bar last() {
        return this._bars[_bars.length - 1];
    }

    // 取时间范围内的所有row
    public List<Bar> rangeRows(String startDatetime, String endDatetime) {
        List<Bar> ret = new LinkedList<>();
        for (int i = 0; i < _bars.length; i++) {
            if (startDatetime.compareTo(_bars[i].datetime) <= 0 && endDatetime.compareTo(_bars[i].datetime) >= 0) {
                ret.add(_bars[i]);
            }
        }
        return ret;
    }

    // 指定范围，排除含null的行，把数据对齐
    public Sequence range(String startDatetime, String endDatetime) {
        int start = -1, end = -1;
        for (int i = 0; i < _bars.length; i++) {
            Bar bar = this._bars[i];
            if (ignoreZero) {  // 忽略扩展属性端上的null和0
                if (bar.extNaNOrZero()) {
                    continue;
                }
            }
            // 从最小位置找起始位
            if (-1 == start && (null == startDatetime || bar.datetime.compareTo(startDatetime) >= 0)) {
                start = i;
            }
            //
            if (null == endDatetime || bar.datetime.compareTo(endDatetime) <= 0) {
                end = i;
            }
        }
        try {
            return this.range(start, end);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Range rows Err. start: %s, end: %s",
                    startDatetime, endDatetime), e);
        }
    }

    // 指定起点/终点位置
    public Sequence range(int start, int end) {
        if (start < 0 || end < 0 || start >= _bars.length) {
            throw new RuntimeException(String.format("Range Index. startIndex: %d, endIndex: %d, count: %d",
                    start, end, _bars.length));
        }
        this.first = start;
        this.end = end;

        return this;
    }

    // 把指针重置为-1
    public Sequence reset() {
        this.point = -1;
        return this;
    }

    // 移动到有效序列范围的倒数第2个位置
    public Sequence toSecondLast() {
        this.point = this.size() - 2;
        return this;
    }

    // 移动到有效序列范围的最后
    public Sequence toLast() {
        this.point = this.size() - 1;
        return this;
    }

    // TODO - 暂无应用
    private Sequence to(int pos) {
        this.point = pos;
        return this;
    }

    // 从起始位置到数组结尾的Row个数
    public int count() {
        if (null == _bars) {
            return 0;
        } else {
            return _bars.length - first;
        }
    }

    // rows总行数
    public int length() {
        if (null == _bars) {
            return 0;
        } else {
            return _bars.length;
        }
    }

    // 有效位置的总行数
    public int size() {
        if (null == _bars) {
            return 0;
        } else {
            return this.end - this.first + 1;
        }
    }
}
