package jbt.model.plus;

import jbt.model.Row;

import java.util.Comparator;

/**
 * @author max.hu  @date 2024/04/21
 **/
public class RowComparator {

    public static Comparator<Row> CloseAscComparator = Comparator.comparingDouble(Row::getClose);
    public static Comparator<Row> CloseDescComparator = CloseAscComparator.reversed();
    public static Comparator<Row> DateAscComparator = Comparator.comparing(Row::getDatetime);
}
