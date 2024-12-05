package hu.jinfeng;

import jbt.model.Bar;
import jbt.model.Sequence;
import utils.NumberUtil;
import utils.PrimitiveValueUtil;

import java.util.List;

/**
 * 自定义的一些计算指标，不是标准的ta-lib
 *
 * @author jinfeng.hu  @Date 2022-11-12
 **/
public class MyTalib {
    // 最近n日的最低点
    public static Bar lowestPriceRow(Sequence seq, int n) {
        Bar ret = seq.get();
        for (int i = 1; i < seq.length(); i++) {
            if (n-- == 0) {
                break;
            }
            Bar his = seq.row(-i);
            if (null == his) {
                break;
            }
            if (NumberUtil.compare3(his.getLow(), ret.getLow()) < 0) {
                ret = his;
            }
        }
        return ret;
    }

    // 最近n日的最高点
    public static Bar highestPriceRow(Sequence seq, int n) {
        Bar ret = seq.get();
        for (int i = 1; i < seq.length(); i++) {
            if (n-- == 0) {
                break;
            }
            Bar his = seq.row(-i);
            if (null == his) {
                break;
            }
            if (NumberUtil.compare3(his.getHigh(), ret.getHigh()) > 0) {
                ret = his;
            }
        }
        return ret;
    }

    // n日价格新低
    public static int newLowPrice(Sequence seq) {
        Bar now = seq.get();
        double price = now.getClose();
        for (int i = 1; i < seq.length(); i++) {
            Bar his = seq.row(-i);
            if (null == his || his.getDatetime().compareTo(now.getDatetime()) > 0 //到底了
                    || NumberUtil.compare3(his.getLow(), price) < 0) {  //与历史最低价比较
                return i - 1;
            }
        }
        return 0;
    }

    // n日价格新高
    public static int newHighPrice(Sequence seq) {
        Bar now = seq.get();
        double price = now.getClose();
        for (int i = 1; i < seq.length(); i++) {
            Bar his = seq.row(-i);
            if (null == his || his.getDatetime().compareTo(now.getDatetime()) > 0 //到底了
                    || NumberUtil.compare3(his.getHigh(), price) > 0) {  //与历史最高价比较
                return i - 1;
            }
        }
        return 0;
    }

    // n日成交额新高
    public static int newHighVolume(Sequence seq) {
        Bar now = seq.get();
        long vol = now.getVolume();
        for (int i = 1; i < seq.length(); i++) {
            Bar his = seq.row(-i);
            if (null == his || his.getDatetime().compareTo(now.getDatetime()) > 0 //到底了
                    || NumberUtil.compare3(his.getVolume(), vol) > 0) {
                return i - 1;
            }
        }
        return 0;
    }

    // 相邻2个的比例
    public static double[] adjacentRate(List list) {
        double[] ret = new double[list.size() - 1];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = PrimitiveValueUtil.getAsDouble(list.get(i + 1)) / PrimitiveValueUtil.getAsDouble(list.get(i));
        }
        return ret;
    }

    // 相邻2个的比例
    public static double[] adjacentRate(long[] array) {
        double[] ret = new double[array.length - 1];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (1.00 * array[i + 1]) / array[i];
        }
        return ret;
    }

    // 相邻2个的比例
    public static double[] adjacentRate(double[] array) {
        double[] ret = new double[array.length - 1];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = array[i + 1] / array[i];
        }
        return ret;
    }
}
