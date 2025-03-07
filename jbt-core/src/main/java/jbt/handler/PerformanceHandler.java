package jbt.handler;

import jbt.account.Bill;
import jbt.account.Position;
import jbt.model.Bar;
import jbt.model.Sequence;
import jbt.model.Stats;
import jbt.performance.Returns;
import lombok.Getter;
import lombok.Setter;
import utils.DatetimeUtil;

import java.util.List;

/**
 * 策略表现评估
 *
 * @author max.hu  @date 2024/03/25
 **/
public class PerformanceHandler implements Handler {
    @Setter
    @Getter
    public Bar start;//2004-08-19 00:00:00
    @Setter
    @Getter
    public Bar end;  //2013-03-01 00:00:00
    // trades
    @Setter
    @Getter
    private Position position;

    public List<Bill> getBills() {
        return this.position.getBills();
    }
    // run
    public Stats apply(Sequence data) {
        String sdt = this.getStart().getDatetime();
        String edt = this.getEnd().getDatetime();
        List<Bar> ranges = data.rangeRows(sdt, edt);

        List<Double> dailyReturns = Returns.getReturns(ranges);

        // 分析账单
        long d1 = DatetimeUtil.parseDate(sdt).getTime();
        long d2 = DatetimeUtil.parseDate(edt).getTime();
        return Stats.builder()
                .start(this.getStart().getDatetime())
                .end(this.getEnd().getDatetime())
                .duration((d2 - d1) / DatetimeUtil.A_DAY_MS)  // days
                .position(this.getPosition().summary(this.getEnd().getClose()))
                .tradeTimes(this.getBills().size())
                .rangeReturn(Returns.rangeReturns(ranges) * 100)
                .sharpeRatio(Returns.sharpeRatio(dailyReturns, 0.002))
                .maxDrawdown(Returns.maxDrawdown(dailyReturns) * 100)
                .build();
    }

}
