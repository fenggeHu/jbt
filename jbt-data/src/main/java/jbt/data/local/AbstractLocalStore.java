package jbt.data.local;

import jbt.data.DataFeeder;
import jbt.data.DataStorage;
import jbt.data.utils.JsonUtil;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地文件的操作
 *
 * @author max.hu  @date 2024/03/18
 **/
@Slf4j
public abstract class AbstractLocalStore implements DataFeeder, DataStorage {
    @Setter
    protected String localFolder = "~/.tibet";
    @Setter
    protected String region = "cn";
    // 换行符
    protected char newlineChar = '\n';
    // 分隔符
    protected String delimiter = ",";

    public String getFilename(String name, String extension) {
        if (null == extension) extension = "txt";
        return String.format("%s/%s/%s.%s", localFolder, region, name, extension);
    }

    // config
    public String getConfigFilename(String name) {
        return getFilename(name, "cfg");
    }

    // symbolic features
    public String getFeatureFilename(String symbol, String name) {
        return String.format("%s/%s/features/%s/%s.csv", localFolder, region, symbol, name);
    }

    /**
     * 按行写文件 - 按row id更新或插入
     * 注意：此时文件是一个Map<String, Object> 结构
     *
     * @param filename 文件路径
     * @param id       记录id - 配置内唯一。 当obj为空时删除改id
     * @param content  记录内容 - 当obj为空时删除改id
     */
    @SneakyThrows
    public void write(String filename, String id, Object content) {
        if (null == filename || filename.trim().length() == 0 || null == id || id.contains(delimiter)) {
            throw new RuntimeException("Invalid filename/id/content. type=" + filename + ", id=" + id);
        }
        Map<String, Object> result = null;
        File file = new File(filename);
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
                result = JsonUtil.toObject(txt, Map.class);
            }

            if (null == content) { // 当obj为空时删除改id
                result.remove(id);
            } else {
                result.put(id, content);
            }
            if (result.isEmpty()) {
                Files.deleteIfExists(file.toPath());
            } else {
                String json = JsonUtil.toJson(result);
                Files.write(file.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }
            // 释放写锁
            lock.release();
        } catch (IOException e) {
            throw new RuntimeException(e);  // 未知错误
        }
    }

    /**
     * 把对象转成json string覆盖写入文件
     */
    @SneakyThrows
    @Override
    public void write(String filename, Object obj) {
        File file = new File(filename);
        if (!file.exists()) {
            if (null == obj) {
                return;
            }
            file.getParentFile().mkdirs();
            file.createNewFile();
        } else if (null == obj) {
            Files.deleteIfExists(file.toPath());
            return;
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
             FileChannel channel = randomAccessFile.getChannel()) {
            // 获取文件独占写锁
            FileLock lock = channel.lock();

            String json = JsonUtil.toJson(obj);
            Files.write(file.toPath(), json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            // 释放写锁
            lock.release();
        } catch (IOException e) {
            throw new RuntimeException(e);  // 未知错误
        }
    }

    /**
     * 读取整个文件
     */
    @SneakyThrows
    @Override
    public String read(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return null;
        }

        // 读取整个文件的字节数组
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        // 将字节数组转换为字符串（根据文件编码）
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public List<String> readLines(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return null;
        }
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public void writeLines(String filename, List<String> lines) {
        File file = new File(filename);
        if (!file.exists()) {
            log.debug("file is not exists: {}", file.getPath());
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // 写入换行符
            }
        } catch (IOException e) {
            log.error("Exception while writing:" + filename, e);
        }
    }

    /**
     * 写配置
     */
    public void writeConfig(String name, Object obj) {
        if (null == name || name.trim().length() == 0) {
            throw new RuntimeException("Invalid type");
        }
        this.write(this.getConfigFilename(name), obj);
    }

    public String readConfig(String name) {
        if (null == name || name.trim().length() == 0) {
            throw new RuntimeException("Invalid type");
        }
        String filename = this.getConfigFilename(name);
        return read(filename);
    }

    // 配置文件的属性
    public BasicFileAttributes getConfigAttributes(String name) {
        return getFileAttributes(this.getConfigFilename(name));
    }

    // 获取文件属性
    @SneakyThrows
    public BasicFileAttributes getFileAttributes(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } else {
            return null;
        }
    }
}
