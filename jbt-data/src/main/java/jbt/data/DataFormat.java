package jbt.data;

/**
 * @author max.hu  @date 2024/11/23
 **/
public interface DataFormat {
    // 换行符
    char newlineChar = '\n';
    // 分隔符
    String delimiter = ",";

    default String toLine() {
        throw new RuntimeException("Not Support");
    }

    default DataFormat format(String line) {
        throw new RuntimeException("Not Support");
    }
}
