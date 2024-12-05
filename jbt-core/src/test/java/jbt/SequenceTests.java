package jbt;

import jbt.model.Bar;
import jbt.model.Sequence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.DatetimeUtil;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author max.hu  @date 2024/04/11
 **/
public class SequenceTests {
    Collection<Bar> bars = new LinkedList<>();

    @Before
    public void before() {
        Date start = DatetimeUtil.format("2024-04-10");

        for (int i = 50; i >= 0; i--) {
            Date day = DatetimeUtil.addDays(start, -i);
            if (DatetimeUtil.isWeekend(day)) continue;
            Bar bar = new Bar();
            bar.setDatetime(DatetimeUtil.format(day));
            bar.setOpen(123.1 + i);
            bar.setHigh(130.12 + i);
            bar.setLow(90.89 + i);
            bar.setClose(100.88 + i);
            bars.add(bar);
        }
    }

    @Test
    public void testIndex() {
        // 取中间段
        Sequence sequence = Sequence.build(bars).range("2024-04-01", "2024-04-08").toLast();
        int size = sequence.size();
        Assert.assertEquals(size, 6);
        Bar point = sequence.get();
        this.assertDate(point, "2024-04-08");
        Bar point_1 = sequence.get(-1);
        this.assertDate(point_1, "2024-04-05");
        Bar point_2 = sequence.get(-2);
        this.assertDate(point_2, "2024-04-04");
        Bar point_6 = sequence.get(-6);
        this.assertDate(point_6, "2024-04-08");
        Bar point_10 = sequence.get(-10);
        this.assertDate(point_10, "2024-04-02");
        Bar bar = sequence.row(0);
        this.assertDate(bar, "2024-04-08");
        Bar bar1 = sequence.row(1);
        this.assertDate(bar1, "2024-04-09");
        Bar bar2 = sequence.row(2);
        this.assertDate(bar2, "2024-04-10");
        Bar bar_1 = sequence.row(-1);
        this.assertDate(bar_1, "2024-04-05");
        Bar bar_10 = sequence.row(-10);
        this.assertDate(bar_10, "2024-03-25");

        //重新range并reset
        sequence.range("2024-03-01", "2024-04-18").reset();
        Bar rrow = sequence.next();
        this.assertDate(rrow, "2024-03-01");
    }

    private void assertDate(Bar bar, String date) {
        Assert.assertEquals(bar.getDatetime(), date);
    }
}
