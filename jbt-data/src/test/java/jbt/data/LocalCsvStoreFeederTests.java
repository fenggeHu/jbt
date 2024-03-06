package jbt.data;

import jbt.data.local.LocalCsvStoreFeeder;
import jbt.model.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author max.hu  @date 2024/03/04
 **/
public class LocalCsvStoreFeederTests {
    private String localFolder = "/Users/max/.tibet";
    private LocalCsvStoreFeeder usLocalCsvFeeder = new LocalCsvStoreFeeder(localFolder, "us");

    @Test
    public void testGetData() {
        int n = 3;
        List<Row> rows = usLocalCsvFeeder.get("APLE", n);
        Assert.assertEquals(n, rows.size());

        rows = usLocalCsvFeeder.get("APLE", "2023-12-27", "2024-01-11");
        Assert.assertEquals(11, rows.size());
    }

    @Test
    public void testRWFile() {
        Row data = Row.builder().datetime("2013-01-01").open(3.13)
                .build();
        usLocalCsvFeeder.write("update", "AAPL", data);
        Map r = usLocalCsvFeeder.read("update");
        Assert.assertNotNull(r);
        Object n = usLocalCsvFeeder.readOne("update", "MSFT");
        Assert.assertNull(n);
    }
}
