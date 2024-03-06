package jbt.data.local;

import jbt.constant.RowPropertyEnum;
import jbt.data.DataFeeder;
import jbt.data.DataStorage;
import jbt.data.feature.Record;
import jbt.data.utils.JsonUtil;
import jbt.model.Row;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import utils.PrimitiveValueUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * // TODO 不支持多线程操作
 * 目录结构
 * root/region/features/symbol/day.csv
 * datetime,O,H,L,C,V,...
 * 时间存储按datetime顺序，第一行是title
 *
 * @author jinfeng.hu  @date 2022/10/9
 **/
@Slf4j
public class LocalCsvStoreFeeder implements DataFeeder, DataStorage {
    // 换行符
    private char newlineChar = '\n';
    // 分隔符
    private String delimiter = ",";
    // 文件头起始字符串
    private String titleStart = RowPropertyEnum.D.getKey();
    @Setter
    private String localFolder = "~/.tibet";
    @Setter
    private String region = "cn";
    // 不可变空List
    private List EmptyList = Collections.unmodifiableList(new ArrayList<>(0));

    public LocalCsvStoreFeeder() {
    }

    public LocalCsvStoreFeeder(String localFolder) {
        this.localFolder = localFolder;
    }

    public LocalCsvStoreFeeder(String localFolder, String region) {
        this.localFolder = localFolder;
        this.region = region;
    }

    @Override
    public List<String> getSymbols() {
        String features = String.format("%s/%s/features/", localFolder, region);
        File file = new File(features);
        if (file.exists() && file.isDirectory()) {
            return Arrays.asList(file.list());
        }
        return new ArrayList<>(0);
    }

    @Override
    public List<Row> get(String symbol, String start, String end) {
        File file = getDayFile(symbol);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return EmptyList;
        }
        String title = null;
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line;
            List<Row> ret = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                if (null == line || line.length() == 0) {
                    continue;
                }
                if (null == title && line.startsWith(titleStart)) {
                    title = line;
                    continue;
                }

                String[] row = line.split(delimiter);
                String datetime = row[0];
                if (datetime.compareTo(start) >= 0 && datetime.compareTo(end) <= 0) {
                    Row bar = new Row();
                    bar.setDatetime(datetime);
                    bar.setOpen(PrimitiveValueUtil.getAsDouble(row[1]));
                    bar.setHigh(PrimitiveValueUtil.getAsDouble(row[2]));
                    bar.setLow(PrimitiveValueUtil.getAsDouble(row[3]));
                    bar.setClose(PrimitiveValueUtil.getAsDouble(row[4]));
                    bar.setVolume(PrimitiveValueUtil.getAsLong(row[5]));
                    ret.add(bar);
                }
            }
            return ret;
        } catch (Exception e) {
            log.error("LocalFeeder.read", e);
        }
        return EmptyList;
    }

    private Row toRow(String line) {
        String[] vs = line.split(delimiter);
        Row row = new Row();
        row.setDatetime(vs[0]);
        row.setOpen(PrimitiveValueUtil.getAsDouble(vs[1]));
        row.setHigh(PrimitiveValueUtil.getAsDouble(vs[2]));
        row.setLow(PrimitiveValueUtil.getAsDouble(vs[3]));
        row.setClose(PrimitiveValueUtil.getAsDouble(vs[4]));
        row.setVolume(PrimitiveValueUtil.getAsLong(vs[5]));
        return row;
    }

    @Override
    public List<Row> get(String symbol, int n) {
        File file = getDayFile(symbol);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return EmptyList;
        }
        List<Row> ret = new LinkedList<>();
        String title = null;
        // 使用 RandomAccessFile 读取文件
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // 使用双端队列（Deque）保存最后n行的内容
            Deque<String> lastNLines = new ArrayDeque<>(n);
            // 逐行读取文件内容
            String line;
            while ((line = reader.readLine()) != null) {
                if (null == line || line.length() == 0) {
                    continue;
                }
                if (null == title && line.startsWith(titleStart)) {
                    title = line;
                    continue;
                }
                // 将当前行加入队列
                lastNLines.addLast(line);

                // 如果队列大小超过n，移除队首元素
                if (lastNLines.size() > n) {
                    lastNLines.removeFirst();
                }
            }
            // to row
            if (lastNLines.size() > 0) {
                lastNLines.forEach(s -> ret.add(toRow(s)));
            }
        } catch (Exception e) {
            log.error("get local day data error: " + symbol, e);
        }

        return ret;
    }

    @SneakyThrows
    @Override
    public void store(String symbol, Collection<Row> chartRow, boolean overwrite) {
        File file = getDayFile(symbol);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        log.info("{}: store to {}", symbol, file.getPath());
        //数据顺序 - 按datetime顺序
        Map<String, String> treeMap = new TreeMap<>();
        String title = null;
        if (!overwrite) { // 如果要放弃历史数据就不读入历史数据
            try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (null == title) {
                        title = line;
                        continue;
                    }
                    String[] row = line.split(delimiter);
                    String datetime = row[0];
                    treeMap.put(datetime, line + newlineChar);
                }
            } catch (Exception e) {
                log.error("{}: read file", symbol, e);
            }
        }
        // 数据整合重写
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Row bar : chartRow) {
                if (null == title) {
                    title = bar.title();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(bar.getDatetime()).append(delimiter)
                        .append(bar.getOpen()).append(delimiter)
                        .append(bar.getHigh()).append(delimiter)
                        .append(bar.getLow()).append(delimiter)
                        .append(bar.getClose()).append(delimiter)
                        .append(bar.getVolume()).append(newlineChar);
                treeMap.put(bar.getDatetime(), sb.toString());
            }
            if (null != title) {
                bw.append(title + newlineChar);
            }
            for (String value : treeMap.values()) {
                bw.append(value);
            }
        } catch (Exception e) {
            log.error("{}: write file", symbol, e);
        }
    }

    private static final ReadWriteLock recordLock = new ReentrantReadWriteLock();

    @SneakyThrows
    @Override
    public boolean writeRecord(String type, String id, Record content) {
        if (null == type || type.trim().length() == 0 || null == id || id.contains(delimiter) || null == content) {
            throw new RuntimeException("Invalid type/id/content. type=" + type + ", id=" + id);
        }
        recordLock.writeLock().lock();
        try {
            File file = getRecordFile(type);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            File tempFile = File.createTempFile(type, id + ".txt.tmp");

            // 读写数据
            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int index = line.indexOf(delimiter);
                    String oid = line.substring(0, index);
                    if (id.equals(oid)) {
                        String json = line.substring(index + 1);
                        Record ord = JsonUtil.toObject(json, Record.class);
                        content.setCreatedAt(ord.getCreatedAt());
                    } else {
                        writer.write(line);
                    }
                }
                if (content.getCreatedAt() <= 0) {
                    content.setCreatedAt(System.currentTimeMillis());
                }
                content.setUpdatedAt(System.currentTimeMillis());
                line = id + delimiter + JsonUtil.toJson(content);
                writer.write(line);
                // 将临时文件替换原文件
                if (!file.delete()) {
                    log.error("Could not delete old file. {}", file.getPath());
                    return false;
                }
                if (!tempFile.renameTo(file)) {
                    log.error("Could not write file.{}", file.getPath());
                }
            } catch (Exception e) {
                log.error("{}: read history data", type, e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);  // 未知错误
        } finally {
            recordLock.writeLock().unlock();
        }
        return true;
    }

    @SneakyThrows
    @Override
    public Map<String, Record> readRecord(String type, String id) {
        if (null == type || type.trim().length() == 0) {
            throw new RuntimeException("Invalid type");
        }
        File file = getRecordFile(type);
        if (!file.exists()) {
            log.debug("record file is not exists: {}", file.getPath());
            return new HashMap<>(0);
        }
        Map<String, Record> records = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int index = line.indexOf(delimiter);
                String oid = line.substring(0, index);
                String json = line.substring(index + 1);
                Record ord = JsonUtil.toObject(json, Record.class);
                if (oid.equals(id)) {
                    records.clear();
                    records.put(oid, ord);
                    return records;     // 精确匹配到了立即返回
                } else {
                    records.put(oid, ord);
                }
            }
        }
        return records;
    }

    private File getRecordFile(String type) {
        String filename = String.format("%s/%s/features/%s.txt", localFolder, region, type);
        File file = new File(filename);
        return file;
    }

    private File getDayFile(String symbol) {
        String filename = String.format("%s/%s/features/%s/day.csv", localFolder, region, symbol);
        File file = new File(filename);
        return file;
    }
}
