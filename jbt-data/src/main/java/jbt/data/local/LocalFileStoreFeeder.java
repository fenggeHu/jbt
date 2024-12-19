package jbt.data.local;

import jbt.data.DataFormat;
import jbt.model.Bar;
import jbt.model.BarEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * // TODO 暂不支持并发操作文件
 * 目录结构
 * root/region/features/symbol/day.csv
 * datetime,O,H,L,C,V,...
 * 时间存储按datetime顺序，第一行是title
 *
 * @author jinfeng.hu  @date 2022/10/9
 **/
@Slf4j
public class LocalFileStoreFeeder extends AbstractLocalStore {
    // 文件头起始字符串
    private String titleStart = BarEnum.D.getKey();

    public LocalFileStoreFeeder() {
    }

    public LocalFileStoreFeeder(String localFolder) {
        this.localFolder = localFolder;
    }

    public LocalFileStoreFeeder(String localFolder, String region) {
        this.localFolder = localFolder;
        this.region = region.toLowerCase();
    }

    @Override
    public List<String> getSymbols() {
        String features = String.format("%s/features/", region);
        File file = new File(localFolder, features);
        if (file.exists() && file.isDirectory()) {
            return Arrays.asList(file.list());
        }
        return new ArrayList<>(0);
    }

    @Override
    public List<Bar> getBar(String symbol, String start, String end) {
        List<Bar> all = getBar(symbol);
        return all.stream().filter(e -> e.datetime.compareTo(start) >= 0 && e.datetime.compareTo(end) <= 0)
                .collect(Collectors.toList());
    }

    // 读取所有的数据
    public List<Bar> getBar(String symbol) {
        File file = getBarFile(symbol);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return Collections.emptyList();
        }

        String title = null;
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line;
            List<Bar> ret = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                if (null == line || line.length() == 0) {
                    continue;
                }
                if (null == title && line.startsWith(titleStart)) {
                    title = line;
                    continue;
                }

                Bar bar = Bar.of(line);
                ret.add(bar);
            }
            return ret;
        } catch (Exception e) {
            log.error("LocalFeeder.read", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Bar> getBar(String symbol, int n) {
        File file = getBarFile(symbol);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return Collections.emptyList();
        }
        List<Bar> ret = new LinkedList<>();
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
                lastNLines.forEach(s -> ret.add(Bar.of(s)));
            }
        } catch (Exception e) {
            log.error("get local day data error: " + symbol, e);
        }

        return ret;
    }

    @SneakyThrows
    @Override
    public void storeBar(String symbol, Collection<Bar> chartBar, boolean overwrite) {
        File file = getBarFile(symbol);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        log.info("{}: store to {}", symbol, file.getPath());
        //数据顺序 - 按datetime顺序
        Map<String, String> treeMap = new TreeMap<>();
        String title = null;
        if (!overwrite) { // 读入历史数据再合并 -- 注意出现复权问题
            Map<String, Bar> oldMap = new HashMap<>();
            try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (null == title) {
                        title = line;
                        continue;
                    }
                    Bar bar = Bar.of(line);
                    oldMap.put(bar.datetime, bar);
                }
            } catch (Exception e) {
                log.error("{}: read file", symbol, e);
            }
            // 数据整合前检查
            boolean isOk = true;
            for (Bar bar : chartBar) {
                Bar hb = oldMap.get(bar.datetime);
                if (null != hb) {
                    if (bar.getClose() != hb.getClose()) {
                        isOk = false;
                        break;
                    }
                }
            }
            if (isOk) {
                oldMap.forEach((k, v) -> treeMap.put(k, v.line() + newlineChar));
            }
        }
        // 数据整合重写
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Bar bar : chartBar) {
                if (null == title) {
                    title = bar.title();
                }
                treeMap.put(bar.getDatetime(), bar.line());
            }
            if (null != title) {
                bw.append(title + newlineChar);
            }
            for (String value : treeMap.values()) {
                if (null == value || value.trim().length() == 0){
                    continue;
                }
                bw.append(value).append(newlineChar);
            }
        } catch (Exception e) {
            log.error("{}: write file", symbol, e);
        }
    }

    protected File getBarFile(String symbol) {
        String filename = this.getFeatureFilename(symbol, "Bar.csv");
        return new File(localFolder, filename);
    }

    public String getFeatureFilename(String symbol, Class<?> clazz) {
        return this.getFeatureFilename(symbol, clazz.getSimpleName() + ".csv");
    }

    // 按行读取整个文件
    @Override
    public <T extends DataFormat> List<T> read(String symbol, Class<T> clazz) {
        String filename = this.getFeatureFilename(symbol, clazz);
        List<String> lines = this.readLines(filename);
        return this.parse(lines, clazz);
    }

    // 按行读取文件里满足起始条件的行
    @Override
    public <T extends DataFormat> List<T> read(String symbol, Class<T> clazz, String start, String end) {
        String filename = this.getFeatureFilename(symbol, clazz);
        List<String> lines = this.readLines(filename, start, end);
        return this.parse(lines, clazz);
    }

    @SneakyThrows
    protected <T extends DataFormat> List<T> parse(List<String> lines, Class<T> clazz) {
        if (null == lines || lines.isEmpty()) return Collections.emptyList();
        Object object = clazz.getDeclaredConstructor().newInstance();
        Method method = clazz.getDeclaredMethod("format", String.class);
        List<T> ret = new LinkedList<>();
        for (String e : lines) {
            ret.add((T) method.invoke(object, e));
        }
        return ret;
    }

    @Override
    public <T extends DataFormat> int store(String symbol, Collection<T> lines, boolean overwrite) {
        if (null == lines || lines.isEmpty()) return -1;
        Class<T> clazz = (Class<T>) lines.stream().findFirst().get().getClass();
        String filename = this.getFeatureFilename(symbol, clazz);
        List<String> list = this.toLines(lines);
        if (!overwrite) {
            List<String> odd = this.readLines(filename);
            if (null != odd && !odd.isEmpty()) {
                for (String ol : odd) {
                    int index = ol.indexOf(delimiter);
                    if (index > 0) {
                        String keyAndDelimiter = ol.substring(0, index + 1);
                        boolean isExists = false;
                        for (String nl : list) {
                            if (nl.startsWith(keyAndDelimiter)) {
                                isExists = true;
                                break;
                            }
                        }
                        if (!isExists) {
                            list.add(ol);
                        }
                    }
                }
                list.sort(String::compareTo);
            }
        }
        this.writeLines(filename, list);
        return list.size();
    }

    // 要求集合是同一个数据类型
    private <T extends DataFormat> List<String> toLines(Collection<T> lines) {
        return lines.stream().map(T::toLine).collect(Collectors.toList());
    }
}
