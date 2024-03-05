package jbt.enhancement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一组 量+价
 * @author max.hu  @date 2024/03/05
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumePrice {
    long volume;
    double price;
}
