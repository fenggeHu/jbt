package enhancement;

import jbt.model.Bar;
import jbt.model.plus.DetailBar;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author max.hu  @date 2024/03/04
 **/
public class DetailBarTests {

    @Test
    public void testCreate() {
        Bar bar = new Bar();
        Assert.assertNotNull(bar.get_ext());
        bar = Bar.builder().build();
        Assert.assertNotNull(bar.get_ext());

        bar = new DetailBar();
        Assert.assertNotNull(bar.get_ext());
        bar = DetailBar.builder().build();
        Assert.assertNotNull(bar.get_ext());
    }
}
