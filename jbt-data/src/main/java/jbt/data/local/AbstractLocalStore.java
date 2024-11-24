package jbt.data.local;

import jbt.data.DataFeeder;
import jbt.data.DataStorage;
import jbt.data.utils.JacksonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 本地文件的操作
 * 为防止越权增加读写文件的目录限制
 *
 * @author max.hu  @date 2024/03/18
 **/
@Slf4j
public abstract class AbstractLocalStore implements DataFeeder, DataStorage {
    @Setter
    protected String localFolder = "~/.tibet";
    @Setter
    @Getter
    protected String region = "cn";
    // 不可变空List
    protected List EmptyList = Collections.emptyList();

    // 根目录
    public String root() {
        return String.format("%s/%s", localFolder, region);
    }

    // 根目录下的相对目录
    public String root(String folder) {
        return String.format("%s/%s", region, folder);
    }

    public String getFilename(String name) {
        return this.getFilename(name, "txt");
    }

    // 相对路径
    public String getFilename(String name, String extension) {
        return String.format("%s/%s.%s", region, name, extension);
    }

    // config
    public String getConfigFilename(String name) {
        return getFilename(name, "cfg");
    }

    // symbolic features
    public String getFeatureFilename(String symbol, String name) {
        return String.format("%s/features/%s/%s", region, symbol, name);
    }

    /**
     * 按行写文件 - 按row id更新或插入
     * 注意：此时文件是一个Map<String, Object> 结构
     *
     * @param filename 文件的相对路径
     * @param id       记录id - 配置内唯一。 当obj为空时删除改id
     * @param content  记录内容 - 当obj为空时删除改id
     */
    @SneakyThrows
    public void write(String filename, String id, Object content) {
        if (null == filename || filename.trim().length() == 0 || null == id || id.contains(delimiter)) {
            throw new RuntimeException("Invalid filename/id/content. type=" + filename + ", id=" + id);
        }
        File file = new File(localFolder, filename);    // 使用相对路径
        Map<String, Object> result = null;
        if (!file.exists()) {
            if (null == content) {
                return;
            }
            file.getParentFile().mkdirs();
            file.createNewFile();
            result = new HashMap<>();
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
             FileChannel channel = randomAccessFile.getChannel()) {
            // 获取文件独占写锁
            FileLock lock = channel.lock();
            // 在锁定状态下执行写操作，例如向文件写入数据
            // 读取整个文件的字节数组
            if (null == result) {
                // 获取文件大小
                long fileSize = channel.size();
                // 创建一个ByteBuffer，大小为文件大小
                ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
                // 将文件内容读取到ByteBuffer中
                int bytesRead = channel.read(buffer);
                // 将ByteBuffer切换为读模式
                buffer.flip();
                // 将ByteBuffer中的字节转换为字符串
                String txt = new String(buffer.array(), 0, bytesRead);
                result = JacksonUtil.toObject(txt, Map.class);
            }

            if (null == content) { // 当obj为空时删除改id
                result.remove(id);
            } else {
                result.put(id, content);
            }
            if (result.isEmpty()) {
                Files.deleteIfExists(file.toPath());
            } else {
                String json = JacksonUtil.toJson(result);
                Files.write(file.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            }
            // 释放写锁
            lock.release();
        } catch (IOException e) {
            throw new RuntimeException(e);  // 未知错误
        }
    }

    /**
     * string覆盖写入文件
     *
     * @param txt 传入null则删除文件
     */
    @Override
    public void write(String filename, String txt) {
        File file = new File(localFolder, filename);
        this.write(file, txt);
    }

    @SneakyThrows
    protected void write(File file, String txt) {
        if (!file.exists()) {
            if (null == txt) {
                return;
            }
            file.getParentFile().mkdirs();
            file.createNewFile();
        } else if (null == txt) {
            Files.deleteIfExists(file.toPath());
            return;
        }
        Files.write(file.toPath(), txt.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    /**
     * 读取整个文件
     */
    @Override
    public String read(String filename) {
        File file = new File(localFolder, filename);
        return read(file);
    }

    @SneakyThrows
    protected String read(File file) {
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return null;
        }

        // 读取整个文件的字节数组
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        // 将字节数组转换为字符串（根据文件编码）
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    // 转成json字符串
    @SneakyThrows
    public void writeJson(String filename, Object obj) {
        String json = null == obj ? null : JacksonUtil.NON_DEFAULT_MAPPER.writeValueAsString(obj);
        this.write(filename, json);
    }

    public Object readJson(String filename, Type type) {
        String json = this.read(filename);
        return JacksonUtil.toObject(json, type);
    }

    /**
     * 写配置
     */
    public void writeConfig(String name, Object obj) {
        if (null == name || name.trim().length() == 0) {
            throw new RuntimeException("Invalid type");
        }
        this.writeJson(this.getConfigFilename(name), obj);
    }

    public Object readConfig(String name, Type type) {
        if (null == name || name.trim().length() == 0) {
            throw new RuntimeException("Invalid type");
        }
        String filename = this.getConfigFilename(name);
        return readJson(filename, type);
    }

    // 按条件序列读取数据。要求文件里的数据按顺序存放 - 按第一个字段排序
    protected List<String> readLines(File file, String start, String end) {
        List<String> lines = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                int index = line.indexOf(delimiter);
                if (index > 0) {// 检查条件：这里是简单的字符串匹配
                    String datetime = line.substring(0, index);
                    if (datetime.compareTo(start) >= 0 && datetime.compareTo(end) <= 0) {
                        lines.add(line);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    // 按行读取文件里满足起始条件的行
    public List<String> readLines(String filename, String start, String end) {
        File file = new File(localFolder, filename);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return EmptyList;
        }
        return readLines(file, start, end);
    }

    // 按行读整个文件
    @SneakyThrows
    protected List<String> readLines(File file) {
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    // 按行读取整个文件
    public List<String> readLines(String filename) {
        File file = new File(localFolder, filename);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return EmptyList;
        }
        return this.readLines(file);
    }

    @SneakyThrows
    public void writeLines(String filename, Collection<String> lines) {
        File file = new File(localFolder, filename);
        // 如果为空则删除文件
        if (null == lines || lines.isEmpty()) {
            Files.deleteIfExists(file.toPath());
            log.debug("file is cleaned {}", file.getPath());
            return;
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            log.debug("file is not exists, so created file {}", file.getPath());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // 写入换行符
            }
        } catch (Exception e) {
            log.error("Exception while writing:" + filename, e);
        }
    }

    // 配置文件的属性
    public BasicFileAttributes getConfigAttributes(String name) {
        return getFileAttributes(this.getConfigFilename(name));
    }

    // 获取文件属性
    @SneakyThrows
    public BasicFileAttributes getFileAttributes(String filename) {
        File file = new File(localFolder, filename);
        if (file.exists()) {
            return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } else {
            return null;
        }
    }

    // 读文件夹
    public File[] list(String folder, String extension) {
        File file = new File(localFolder, folder);
        if (!file.exists()) {
            log.debug("{} is not exists", folder);
            return null;
        }

        if (!file.isDirectory()) {
            log.debug("{} is not Directory", folder);
            return null;
        }
        return file.listFiles((dir, name) -> name.endsWith(extension));    // .txt
    }

    // 前缀过滤
    public File[] list(String folder, String extension, String prefix) {
        File[] files = this.list(folder, extension);
        if (null == files) return null;
        return Arrays.stream(files).filter(f -> f.getName().startsWith(prefix)).toArray(File[]::new);
    }

    /**
     * 检测文件的路径必须位于localFolder目录
     */
}
