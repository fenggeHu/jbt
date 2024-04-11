package jbt;

import jbt.model.Row;
import jbt.model.Sequence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.DatetimeUtils;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author max.hu  @date 2024/04/11
 **/
public class SequenceTests {
    Collection<Row> rows = new LinkedList<>();

    @Before
    public void before() {
        Date start = DatetimeUtils.format("2024-04-10");

        for (int i = 50; i >= 0; i--) {
            Date day = DatetimeUtils.addDays(start, -i);
            if (DatetimeUtils.isWeekend(day)) continue;
            Row row = new Row();
            row.setDatetime(DatetimeUtils.format(day));
            row.setOpen(123.1 + i);
            row.setHigh(130.12 + i);
            row.setLow(90.89 + i);
            row.setClose(100.88 + i);
            rows.add(row);
        }
    }

    @Test
    public void testIndex() {
        // 取中间段
        Sequence sequence = Sequence.build(rows).range("2024-04-01", "2024-04-08").toLast();
        int size = sequence.size();
        Assert.assertEquals(size, 6);
        Row point = sequence.get();
        this.assertDate(point, "2024-04-08");
        Row point_1 = sequence.get(-1);
        this.assertDate(point_1, "2024-04-05");
        Row point_2 = sequence.get(-2);
        this.assertDate(point_2, "2024-04-04");
        Row point_6 = sequence.get(-6);
        this.assertDate(point_6, "2024-04-08");
        Row point_10 = sequence.get(-10);
        this.assertDate(point_10, "2024-04-02");
        Row row = sequence.row(0);
        this.assertDate(row, "2024-04-08");
        Row row1 = sequence.row(1);
        this.assertDate(row1, "2024-04-09");
        Row row2 = sequence.row(2);
        this.assertDate(row2, "2024-04-10");
        Row row_1 = sequence.row(-1);
        this.assertDate(row_1, "2024-04-05");
        Row row_10 = sequence.row(-10);
        this.assertDate(row_10, "2024-03-25");

        //重新range并reset
        sequence.range("2024-03-01", "2024-04-18").reset();
        Row rrow = sequence.next();
        this.assertDate(rrow, "2024-03-01");
    }

    private void assertDate(Row row, String date) {
        Assert.assertEquals(row.getDatetime(), date);
    }
}
