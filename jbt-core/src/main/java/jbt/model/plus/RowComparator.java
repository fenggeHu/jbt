package jbt.model.plus;

import jbt.model.Row;

import java.util.Comparator;

/**
 * @author max.hu  @date 2024/04/21
 **/
public class RowComparator {

    public static Comparator<Row> CloseDescComparator = (o1, o2) -> Double.compare(o2.getClose(), o1.getClose());
    public static Comparator<Row> CloseAscComparator = (o1, o2) -> Double.compare(o1.getClose(), o2.getClose());
    public static Comparator<Row> DateAscComparator = (o1, o2) -> o1.getDatetime().compareTo(o2.getDatetime());
}
