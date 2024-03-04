package enhancement;

import jbt.enhancement.RealtimeRow;
import jbt.model.Row;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author max.hu  @date 2024/03/04
 **/
public class RealtimeRowTests {

    @Test
    public void testCreate() {
        Row row = new Row();
        Assert.assertNotNull(row.get_ext());
        row = Row.builder().build();
        Assert.assertNotNull(row.get_ext());

        row = new RealtimeRow();
        Assert.assertNotNull(row.get_ext());
        row = RealtimeRow.builder().build();
        Assert.assertNotNull(row.get_ext());
    }
}
