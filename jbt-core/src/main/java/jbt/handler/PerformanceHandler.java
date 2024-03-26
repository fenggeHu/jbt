package jbt.handler;

import jbt.account.Bill;
import jbt.account.Position;
import jbt.model.Row;
import jbt.model.Sequence;
import jbt.model.Stats;
import jbt.performance.Returns;
import lombok.Getter;
import lombok.Setter;
import utils.DatetimeUtils;

import java.util.List;

/**
 * 策略表现评估
 *
 * @author max.hu  @date 2024/03/25
 **/
public class PerformanceHandler implements Handler {
    @Setter
    @Getter
    public Row start;//2004-08-19 00:00:00
    @Setter
    @Getter
    public Row end;  //2013-03-01 00:00:00
    // trades
    @Setter
    private Position position;
    double value; // 最新价值
    double returns; // 收益金额
    double percent; // 收益百分比 x%
    double maxDrawdown; //最大回撤 %
    double maxProfit; //最大盈利 %

    public List<Bill> getBills() {
        return this.position.getBills();
    }

    public Stats run(Sequence data) {
        String sdt = this.getStart().getDatetime();
        String edt = this.getEnd().getDatetime();
        List<Row> ranges = data.rangeRows(sdt, edt);
        List<Double> dailyReturns = Returns.getReturns(ranges);

        long d1 = DatetimeUtils.parseDate(sdt).getTime();
        long d2 = DatetimeUtils.parseDate(edt).getTime();
        return Stats.builder()
                .start(this.getStart().getDatetime())
                .end(this.getEnd().getDatetime())
                .duration((d2 - d1) / 86400000.00)  // days
                .bills(this.getBills())
                .trades(this.getBills().size())
                .totalReturn(Returns.totalReturns(dailyReturns))
                .maxDrawdown(Returns.maxDrawdown(dailyReturns))
                .build();
    }

}
