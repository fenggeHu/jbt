package jbt.data.feature;

import lombok.Builder;
import lombok.Data;

/**
 * 更新信息记录
 *
 * @author max.hu  @date 2024/03/06
 **/
@Data
@Builder
public class Record {
    String symbol;
    String name;
    // 创建记录的时间戳
    long createdAt;
    // 修改记录的时间戳
    long updatedAt;
    // 记录详情
    String description;
}
