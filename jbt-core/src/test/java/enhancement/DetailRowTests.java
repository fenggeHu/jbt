package enhancement;

import jbt.plus.DetailRow;
import jbt.model.Row;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author max.hu  @date 2024/03/04
 **/
public class DetailRowTests {

    @Test
    public void testCreate() {
        Row row = new Row();
        Assert.assertNotNull(row.get_ext());
        row = Row.builder().build();
        Assert.assertNotNull(row.get_ext());

        row = new DetailRow();
        Assert.assertNotNull(row.get_ext());
        row = DetailRow.builder().build();
        Assert.assertNotNull(row.get_ext());
    }
}
