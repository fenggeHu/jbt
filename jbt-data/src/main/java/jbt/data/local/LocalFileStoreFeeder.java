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
 * // TODO 不支持多线程操作
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
    // 不可变空List
    private List EmptyList = Collections.unmodifiableList(new ArrayList<>(0));

    public LocalFileStoreFeeder() {
    }

    public LocalFileStoreFeeder(String localFolder) {
        this.localFolder = localFolder;
    }

    public LocalFileStoreFeeder(String localFolder, String region) {
        this.localFolder = localFolder;
        this.region = region;
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
    public List<Bar> get(String symbol, String start, String end) {
        File file = getDayFile(symbol);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return EmptyList;
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
                if (bar.datetime.compareTo(start) >= 0 && bar.datetime.compareTo(end) <= 0) {
                    ret.add(bar);
                }
            }
            return ret;
        } catch (Exception e) {
            log.error("LocalFeeder.read", e);
        }
        return EmptyList;
    }

    @Override
    public List<Bar> get(String symbol, int n) {
        File file = getDayFile(symbol);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return EmptyList;
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
    public void store(String symbol, Collection<Bar> chartBar, boolean overwrite) {
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
                    Bar bar = Bar.of(line);
                    treeMap.put(bar.datetime, line + newlineChar);
                }
            } catch (Exception e) {
                log.error("{}: read file", symbol, e);
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
                bw.append(value).append(newlineChar);
            }
        } catch (Exception e) {
            log.error("{}: write file", symbol, e);
        }
    }

    protected File getDayFile(String symbol) {
        String filename = this.getFeatureFilename(symbol, "day.csv");
        return new File(localFolder, filename);
    }

    protected String getFeatureFilename(String symbol, Class<?> clazz) {
        return this.getFeatureFilename(symbol, clazz.getSimpleName() + ".csv");
    }

    @Override
    @SneakyThrows
    public <T extends DataFormat> List<T> readLines(String symbol, int count, Class<T> clazz) {
        String filename = this.getFeatureFilename(symbol, clazz);
        // TODO 按需读取
        List<String> lines = this.readLines(filename);
        if (null == lines || lines.isEmpty()) return EmptyList;
        Object object = clazz.getDeclaredConstructor().newInstance();
        Method method = clazz.getDeclaredMethod("format", String.class);
        List<T> ret = new LinkedList<>();
        for (String e : lines) {
            ret.add((T) method.invoke(object, e));
        }
        // TODO 按需读取
        if (ret.size() <= count) {
            return ret;
        } else {
            return new ArrayList<>(ret.subList(ret.size() - count, ret.size()));
        }
    }

    @Override
    public <T extends DataFormat> int storeLines(String symbol, Collection<T> lines, boolean overwrite) {
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
