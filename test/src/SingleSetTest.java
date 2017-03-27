import org.junit.Assert;
import org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by brandon on 3/27/17.
 */
public class SingleSetTest {
    /*
    Tests the simple operations on a single set
     */
    @Test
    public void TestInsertion() {
        NestedSet<Integer> set = new NestedSet<>();
        Assert.assertEquals(0, set.size());

        boolean b1 = set.addItem(1);
        Assert.assertEquals(1, set.size());

        boolean b2 = set.addItem(12);
        Assert.assertEquals(2, set.size());

        Assert.assertTrue(b1 && b2);
    }

    @Test
    public void TestDuplicateInsertion() {
        NestedSet<Integer> set = new NestedSet<>();
        boolean b1 = set.addItem(1);
        boolean b2 = set.addItem(1);

        Assert.assertEquals(1, set.size());
        Assert.assertTrue(b1);
        Assert.assertFalse(b2);
    }

    @Test
    public void TestDeletion() {
        NestedSet<Integer> set = new NestedSet<>();
        boolean b1 = set.addItem(1);
        boolean b2 = set.addItem(12);

        boolean c1 = set.removeItem(1);
        Assert.assertEquals(1, set.size());

        boolean c2 = set.removeItem(12);
        Assert.assertEquals(0, set.size());

        Assert.assertTrue(b1 && b2 && c1 && c2);
    }

    @Test
    public void TestEmptySetDeletion() {
        NestedSet<Integer> set = new NestedSet<>();
        boolean b = set.removeItem(1);

        Assert.assertFalse(b);
    }

    @Test
    public void TestMembership() {
        NestedSet<Integer> set = new NestedSet<>();
        set.addItem(10);

        Assert.assertTrue(set.containsItem(10));
        Assert.assertFalse(set.containsItem(666));
    }
}
