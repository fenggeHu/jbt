package jbt;

import jbt.data.local.DataFeeder;
import jbt.data.local.LocalCsvStoreFeeder;
import jbt.event.Event;
import jbt.model.Row;
import jbt.model.Sequence;
import jbt.model.Stats;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Collection;

/**
 * @author jinfeng.hu  @Date 2022/10/27
 **/
@Slf4j
public class EngineTests {
    private String localFolder = "/Users/max/.tibet";

    @Test
    public void testREnginePlay() {
        String symbol = "SZ300760";
        LocalCsvStoreFeeder localCsvFeeder = new LocalCsvStoreFeeder(localFolder);
        Collection<Row> reads1 = localCsvFeeder.get(symbol, "2022-01-10", "2022-01-31");
        Collection<Row> reads2 = localCsvFeeder.get(symbol, "2022-02-01", "2022-10-31");
        for (Row r : reads2) {
            reads1.add(r);
            REngine engine = REngine.build(new MyStrategy(), Sequence.build(reads1));
            Event e = engine.play();
            log.info("Returns: {}, Percent:{}, Event:{} ",
                    engine.getTradeHandler().getPosition().getReturns(), engine.getTradeHandler().getPosition().getPercent(), e);
        }
    }

    @Test
    public void testREnginePlay2() {
        String symbol = "SZ300760";
        LocalCsvStoreFeeder localCsvFeeder = new LocalCsvStoreFeeder(localFolder);
        Collection<Row> reads1 = localCsvFeeder.get(symbol, "2022-01-10", "2022-01-31");
        REngine engine = REngine.build(new RocketReadyStrategy(), Sequence.build(reads1));
        Collection<Row> reads2 = localCsvFeeder.get(symbol, "2022-02-01", "2022-10-31");
        for (Row r : reads2) {
            Event e = engine.play(r);
            log.info("Returns: {}, Percent:{}, Event:{} ",
                    engine.getTradeHandler().getPosition().getReturns(), engine.getTradeHandler().getPosition().getPercent(), e);
        }
    }

    @Test
    public void testEngine() {
        String symbol = "SZ300760";
        DataFeeder localCsvFeeder = new LocalCsvStoreFeeder(localFolder);
        Engine engine = new Engine(new MyStrategy());
        Collection<Row> reads1 = localCsvFeeder.get(symbol, "2022-01-01", "2022-06-31");
        Collection<Row> reads2 = localCsvFeeder.get(symbol, "2022-07-01", "2022-10-31");
        for (int i = 0; i < 10; i++) {
            Stats stats = engine.run(Sequence.build(reads1));
            log.info("Data1-->Returns: {}, Percent:{}", stats.getPosition().getReturns(), stats.getPosition().getPercent());
            stats = engine.run(Sequence.build(reads2));
            log.info("Data2-->Returns: {}, Percent:{}", stats.getPosition().getReturns(), stats.getPosition().getPercent());
        }
    }

    @Test
    public void testEngine2() {
        String symbol = "SZ300760";
        LocalCsvStoreFeeder localCsvFeeder = new LocalCsvStoreFeeder(localFolder);
        Collection<Row> reads = localCsvFeeder.get(symbol, "2022-01-01", "2022-06-31");
//        Engine engine = new Engine(new MyStrategy(), Sequence.build(reads));
        // 测试回测起始时间和结束时间
        Engine engine = Engine.build(new MyStrategy(), Sequence.build(reads), "2022-04-28", "2022-05-31");
        Stats stats = engine.run();
        log.info("Data1-->Returns: {}, Percent:{}", stats.getPosition().getReturns(), stats.getPosition().getPercent());
    }

    @Test
    public void testStrategy() {
        String symbol = "SZ300760";
        LocalCsvStoreFeeder localCsvFeeder = new LocalCsvStoreFeeder(localFolder);
        Collection<Row> reads = localCsvFeeder.get(symbol, "2022-01-01", "2022-06-31");
        Engine engine = new Engine(new MyStrategy());
        Stats stats = engine.run(Sequence.build(reads));
        log.info("{}", stats);
        reads = localCsvFeeder.get(symbol, "2022-07-01", "2022-10-31");
        stats = engine.run(Sequence.build(reads));
        log.info("{}", stats);
    }

}
