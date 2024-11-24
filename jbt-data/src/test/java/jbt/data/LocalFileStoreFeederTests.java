package jbt.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jbt.data.local.LocalFileStoreFeeder;
import jbt.model.Bar;
import jbt.model.plus.DetailBar;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author max.hu  @date 2024/03/04
 **/
public class LocalFileStoreFeederTests {
    private String localFolder = "/Users/max/.tibet";
    private LocalFileStoreFeeder usLocalCsvFeeder = new LocalFileStoreFeeder(localFolder, "us");
    private LocalFileStoreFeeder cnLocalCsvFeeder = new LocalFileStoreFeeder(localFolder, "cn");

    @Test
    public void testWriteLines() {
        String file = "test.txt";
        List<String> lines = new ArrayList<>(Arrays.asList("1", "2", "3", "4"));
        cnLocalCsvFeeder.writeLines(file, lines);
        var readLines = cnLocalCsvFeeder.readLines(file);
        Assert.assertEquals(lines.size(), readLines.size());
        // 覆盖
        lines.remove("2");
        cnLocalCsvFeeder.writeLines(file, lines);
        readLines = cnLocalCsvFeeder.readLines(file);
        Assert.assertEquals(lines.size(), readLines.size());

        // 清空
        lines.clear();
        cnLocalCsvFeeder.writeLines(file, lines);
    }

    @Test
    public void testFile() {
        File file = new File("~/logs/main.log");
        File f2 = file.getAbsoluteFile();
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getPath());
        System.out.println(f2.getPath());
    }

    @Test
    public void testJson() throws Exception {
        Gson gson = new GsonBuilder().create(); //.setPrettyPrinting().create();
        DetailBar row = DetailBar.builder()
                .symbol("13456").code("33535").change(12432.435).turnover(23985739857932745L).close(345.1)
                .build();
        String formattedJson = gson.toJson(row);
        System.out.println(formattedJson);
    }

    @Test
    public void testGetData() {
        int n = 3;
        List<Bar> bars = usLocalCsvFeeder.getBar("APLE", n);
        Assert.assertEquals(n, bars.size());

        bars = usLocalCsvFeeder.getBar("APLE", "2023-12-27", "2024-01-11");
        Assert.assertEquals(11, bars.size());
    }

    @Test
    public void testRWFile() {
        usLocalCsvFeeder.writeConfig("update", null);
        Bar data = Bar.builder().datetime("2013-01-01").open(3.13)
                .build();
        usLocalCsvFeeder.write(usLocalCsvFeeder.getConfigFilename("update"), "AAPL", data);
        Object r = usLocalCsvFeeder.readConfig("update", Bar.class);
        Assert.assertNotNull(r);
        usLocalCsvFeeder.writeConfig("update", data);
        Object r2 = usLocalCsvFeeder.readConfig("update", Bar.class);
        Assert.assertNotNull(r2);
        usLocalCsvFeeder.writeConfig("update", null);
        Object r3 = usLocalCsvFeeder.readConfig("update", Bar.class);
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
