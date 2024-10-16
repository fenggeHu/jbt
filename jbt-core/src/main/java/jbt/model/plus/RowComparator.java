package jbt.model.plus;

import jbt.model.Bar;

import java.util.Comparator;

/**
 * @author max.hu  @date 2024/04/21
 **/
public class RowComparator {

    public static Comparator<Bar> CloseAscComparator = Comparator.comparingDouble(Bar::getClose);
    public static Comparator<Bar> CloseDescComparator = CloseAscComparator.reversed();
    public static Comparator<Bar> DateAscComparator = Comparator.comparing(Bar::getDatetime);
}
