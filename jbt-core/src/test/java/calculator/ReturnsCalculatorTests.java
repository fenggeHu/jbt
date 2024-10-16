package calculator;

import jbt.model.Bar;
import jbt.performance.Returns;
import org.junit.Test;

import java.util.List;

/**
 * @author max.hu  @date 2024/02/27
 **/
public class ReturnsCalculatorTests {

    @Test
    public void testCalculator() throws Exception {
        // 示例数据
        Bar[] bars = {
                new Bar("2022-01-01", 100.0, 105.0, 110.0, 98.0, 10000),
                new Bar("2022-01-02", 105.0, 110.0, 112.0, 100.0, 12000),
                new Bar("2022-01-03", 110.0, 108.0, 115.0, 105.0, 15000)
        };

        // 计算收益率序列
        List<Double> returns = Returns.getReturns(bars);

        // 打印收益率序列
        System.out.println("Daily Returns: " + returns);
    }
}
