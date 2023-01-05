package jbt.event;

import jbt.model.Row;
import lombok.Data;

/**
 * 事件
 * @author jinfeng.hu  @date 2022/10/18
 **/
@Data
public class Event {
    // 触发事件的行数据
    public Row row;
}
