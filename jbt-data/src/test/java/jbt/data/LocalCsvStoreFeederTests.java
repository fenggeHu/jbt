package jbt.data;

import jbt.data.local.LocalCsvStoreFeeder;
import jbt.model.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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
}
