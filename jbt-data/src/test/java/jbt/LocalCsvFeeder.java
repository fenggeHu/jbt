package jbt;

import jbt.model.Row;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import utils.PrimitiveValueUtil;

import java.io.*;
import java.util.*;

/**
 * 目录结构
 * root/region/features/symbol/day.csv
 * datetime,O,H,L,C,V,...
 * 时间存储按datetime顺序，第一行是title
 *
 * @author jinfeng.hu  @Date 2022/10/9
 **/
@Slf4j
public class LocalCsvFeeder {
    @Setter
    private String localFolder = "~/.tibet";
    @Setter
    private String region = "cn";

    public LocalCsvFeeder() {
    }

    public LocalCsvFeeder(String localFolder) {
        this.localFolder = localFolder;
    }

    public LocalCsvFeeder(String localFolder, String region) {
        this.localFolder = localFolder;
        this.region = region;
    }

    public Collection<Row> get(String symbol, String start, String end) {
        File file = getFile(symbol);
        if (!file.exists()) {
            log.warn("file is not exists: {}", file.getPath());
            return null;
        }
        String title = null;
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line;
            List<Row> barList = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                if (null == title) {
                    title = line;
                    continue;
                }
                String[] row = line.split(",");
                String datetime = row[0];
                if (datetime.compareTo(end) > 0) {
                    break;
                }
                if (datetime.compareTo(start) >= 0 && datetime.compareTo(end) <= 0) {
                    Row bar = new Row();
                    bar.setDatetime(datetime);
                    bar.setOpen(PrimitiveValueUtil.getAsDouble(row[1]));
                    bar.setHigh(PrimitiveValueUtil.getAsDouble(row[2]));
                    bar.setLow(PrimitiveValueUtil.getAsDouble(row[3]));
                    bar.setClose(PrimitiveValueUtil.getAsDouble(row[4]));
                    bar.setVolume(PrimitiveValueUtil.getAsLong(row[5]));
                    barList.add(bar);
                }
            }
            return barList;
        } catch (Exception e) {
            log.error("LocalFeeder.read", e);
        }
        return null;
    }

    @SneakyThrows
    public void store(String symbol, Collection<Row> chartRow) {
        File file = getFile(symbol);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        log.info("{}: store to ", symbol, file.getPath());
        //数据顺序 - 按datetime顺序
        Map<String, String> treeMap = new TreeMap<>();
        String title = null;
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (null == title) {
                    title = line;
                    continue;
                }
                String[] row = line.split(",");
                String datetime = row[0];
                treeMap.put(datetime, line + "\n");
            }
        } catch (Exception e) {
            log.error("{}: read file", symbol, e);
        }
        // 数据整合重写
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Row bar : chartRow) {
                if (null == title) {
                    title = bar.title();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(bar.getDatetime()).append(",")
                        .append(bar.getOpen()).append(",")
                        .append(bar.getHigh()).append(",")
                        .append(bar.getLow()).append(",")
                        .append(bar.getClose()).append(",")
                        .append(bar.getVolume()).append("\n");
                treeMap.put(bar.getDatetime(), sb.toString());
            }
            if (null != title) {
                bw.append(title + "\n");
            }
            for (String value : treeMap.values()) {
                bw.append(value);
            }
        } catch (Exception e) {
            log.error("{}: write file", symbol, e);
        }
    }

    private File getFile(String symbol) {
        String filename = String.format("%s/%s/features/%s/day.csv", localFolder, region, symbol);
        File file = new File(filename);
        return file;
    }
}
