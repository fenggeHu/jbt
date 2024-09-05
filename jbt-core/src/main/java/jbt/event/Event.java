package jbt.event;

import jbt.model.Row;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 事件
 * @author jinfeng.hu  @date 2022/10/18
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    // 触发事件的行数据
    public Row row;
    // 触发时的信息
    public String message;
}
