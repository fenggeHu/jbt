package jbt.data;

import jbt.data.local.LocalCsvStoreFeeder;
import jbt.model.Row;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.attribute.BasicFileAttributes;
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

    @Test
    public void testRWFile() {
        usLocalCsvFeeder.writeConfig("update", null);
        Row data = Row.builder().datetime("2013-01-01").open(3.13)
                .build();
        usLocalCsvFeeder.write(usLocalCsvFeeder.getConfigFilename("update"), "AAPL", data);
        Object r = usLocalCsvFeeder.readConfig("update");
        Assert.assertNotNull(r);
        usLocalCsvFeeder.writeConfig("update", data);
        Object r2 = usLocalCsvFeeder.readConfig("update");
        Assert.assertNotNull(r2);
        usLocalCsvFeeder.writeConfig("update", null);
        Object r3 = usLocalCsvFeeder.readConfig("update");
        Assert.assertNull(r3);
    }

    @Test
    public void testFileAttribute() {
        usLocalCsvFeeder.write("real_time_monitoring", "{}");
        BasicFileAttributes fileAttributes = usLocalCsvFeeder.getConfigAttributes("real_time_monitoring");

        System.out.println("Creation Time: " + fileAttributes.creationTime());
        System.out.println("Last Modified Time: " + fileAttributes.lastModifiedTime().toMillis());
    }
}
