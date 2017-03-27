import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static sun.misc.Version.println;

/**
 * Created by brandon on 3/27/17.
 */
public class ParentChildTest {
    @Test
    public void TestSingleSetHasNoRelatives() {
        NestedSet<Integer> set = new NestedSet<>();

        Assert.assertEquals(0, set.numberOfChildrenSets());
        Assert.assertEquals(0, set.numberOfParentSets());
    }

    @Test
    public void TestSpawnChild() {
        NestedSet<Integer> parent = new NestedSet<>();
        NestedSet<Integer> child = parent.spawnChild();

        Assert.assertEquals(parent.numberOfChildrenSets(), 1);
        Assert.assertEquals(parent.numberOfParentSets(), 0);

        Assert.assertEquals(child.numberOfParentSets(), 1);
        Assert.assertEquals(child.numberOfChildrenSets(), 0);

        Assert.assertTrue(parent.isParentOf(child));
        Assert.assertFalse(parent.isChildOf(child));

        Assert.assertTrue(child.isChildOf(parent));
        Assert.assertFalse(child.isParentOf(parent));
    }

    @Test
    public void TestSpawnParent() {
        NestedSet<Integer> child = new NestedSet<>();
        NestedSet<Integer> parent = child.spawnParent();

        Assert.assertEquals(parent.numberOfChildrenSets(), 1);
        Assert.assertEquals(parent.numberOfParentSets(), 0);

        Assert.assertEquals(child.numberOfParentSets(), 1);
        Assert.assertEquals(child.numberOfChildrenSets(), 0);

        Assert.assertTrue(parent.isParentOf(child));
        Assert.assertFalse(parent.isChildOf(child));

        Assert.assertTrue(child.isChildOf(parent));
        Assert.assertFalse(child.isParentOf(parent));
    }

    @Test
    public void TestInsertToParentAndChild() {
        NestedSet<Integer> parent = new NestedSet<>();
        NestedSet<Integer> child = parent.spawnChild();

        Assert.assertEquals(0, parent.size());
        Assert.assertEquals(0, child.size());

        // Inserting to a child should insert into the parent, but not vise-versa
        child.addItem(1);

        Assert.assertEquals(1, parent.size());
        Assert.assertEquals(1, child.size());

        parent.addItem(12);

        Assert.assertEquals(2, parent.size());
        Assert.assertEquals(1, child.size());
    }

    @Test
    public void TestRemoveFromParentAndChild() {
        NestedSet<Integer> parent = new NestedSet<>();
        NestedSet<Integer> child = parent.spawnChild();

        child.addItem(1);
        child.addItem(42);

        Assert.assertEquals(2, parent.size());
        Assert.assertEquals(2, child.size());

        // Removing from a parent should remove from the child, but not vise-versa
        parent.removeItem(1);

        Assert.assertEquals(1, parent.size());
        Assert.assertEquals(1, child.size());

        child.removeItem(42);

        Assert.assertEquals(1, parent.size());
        Assert.assertEquals(0, child.size());
    }

    @Test
    public void TestThreeGenerationsOfSetsTopDown() {
        NestedSet<Integer> grandparent = new NestedSet<>();
        NestedSet<Integer> parent = grandparent.spawnChild();
        NestedSet<Integer> child = parent.spawnChild();

        validateThreeGenerationRelationships(grandparent, parent, child);
    }

    @Test
    public void TestThreeGenerationsOfSetsBottomUp() {
        NestedSet<Integer> child = new NestedSet<>();
        NestedSet<Integer> parent = child.spawnParent();
        NestedSet<Integer> grandparent = parent.spawnParent();

        validateThreeGenerationRelationships(grandparent, parent, child);
    }

    @Test
    public void TestThreeGenerationsOfSetsMiddleOut() {
        NestedSet<Integer> parent = new NestedSet<>();
        NestedSet<Integer> grandparent = parent.spawnParent();
        NestedSet<Integer> child = parent.spawnChild();

        validateThreeGenerationRelationships(grandparent, parent, child);
    }

    private void validateThreeGenerationRelationships(NestedSet<Integer> grandparent,
                                                      NestedSet<Integer> parent,
                                                      NestedSet<Integer> child) {
        Assert.assertTrue(grandparent.isParentOf(parent));
        Assert.assertTrue(grandparent.isParentOf(child));
        Assert.assertFalse(grandparent.isChildOf(parent));
        Assert.assertFalse(grandparent.isChildOf(child));

        Assert.assertFalse(parent.isParentOf(grandparent));
        Assert.assertTrue(parent.isParentOf(child));
        Assert.assertTrue(parent.isChildOf(grandparent));
        Assert.assertFalse(parent.isChildOf(child));

        Assert.assertFalse(child.isParentOf(grandparent));
        Assert.assertFalse(child.isParentOf(parent));
        Assert.assertTrue(child.isChildOf(grandparent));
        Assert.assertTrue(child.isChildOf(parent));

        Assert.assertEquals(0, grandparent.numberOfParentSets());
        Assert.assertEquals(2, grandparent.numberOfChildrenSets());

        Assert.assertEquals(1, parent.numberOfParentSets());
        Assert.assertEquals(1, parent.numberOfChildrenSets());

        Assert.assertEquals(2, child.numberOfParentSets());
        Assert.assertEquals(0, child.numberOfChildrenSets());
    }

    @Test
    public void TestInheritance() {
        // newly-spawned children and parents should start with the same contents as the source
        NestedSet<Integer> parent = new NestedSet<>();
        parent.addItem(21);
        parent.addItem(45);

        NestedSet<Integer> grandparent = parent.spawnParent();
        NestedSet<Integer> child = parent.spawnChild();

        Integer[] parentContents = extractContents(parent);
        Integer[] grandparentContents = extractContents(grandparent);
        Integer[] childContents = extractContents(child);

        Assert.assertArrayEquals(parentContents, grandparentContents);
        Assert.assertArrayEquals(parentContents, childContents);
    }

    private Integer[] extractContents(NestedSet<Integer> set) {
        Integer[] output = new Integer[set.size()];

        int i = 0;
        for(NestedSetItem<Integer> item : set) {
            output[i++] = item.getValue();
        }

        Arrays.sort(output);

        return output;
    }

    @Test
    public void TestInsertsIntoThreeGenerations() {
        NestedSet<Integer> grandparent = new NestedSet<>();
        NestedSet<Integer> parent = grandparent.spawnChild();
        NestedSet<Integer> child = parent.spawnChild();

        // As before, inserting into a child should insert into all parents, but not the reverse
        grandparent.addItem(30);
        parent.addItem(20);
        child.addItem(10);

        Assert.assertEquals(3, grandparent.size());
        Assert.assertEquals(2, parent.size());
        Assert.assertEquals(1, child.size());
    }

    @Test
    public void TestDeletesFromThreeGenerations() {
        NestedSet<Integer> grandparent = new NestedSet<>();

        grandparent.addItem(10);
        grandparent.addItem(20);
        grandparent.addItem(30);

        NestedSet<Integer> parent = grandparent.spawnChild();
        NestedSet<Integer> child = parent.spawnChild();

        // As before, deletes should propagate to children, but not parents
        parent.removeItem(20);
        child.removeItem(10);

        Assert.assertEquals(3, grandparent.size());
        Assert.assertEquals(2, parent.size());
        Assert.assertEquals(1, child.size());
    }
}
