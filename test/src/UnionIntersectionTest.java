import org.junit.Assert;
import org.junit.Test;

/**
 * Created by brandon on 3/27/17.
 */
public class UnionIntersectionTest {
    /*
    Tests the functionality of creating and using unions and intersections in NestedSets
     */
    @Test
    public void TestSimpleUnion() {
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();

        NestedSet<Integer> union = setA.unionWith(setB);

        Assert.assertTrue(union.isParentOf(setA));
        Assert.assertTrue(union.isParentOf(setB));
    }

    @Test
    public void TestSimpleIntersection() {
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();

        NestedSet<Integer> intersection = setA.intersectionWith(setB);

        Assert.assertTrue(intersection.isChildOf(setA));
        Assert.assertTrue(intersection.isChildOf(setB));
    }

    @Test
    public void TestDiamondOne() {
        // Forms a diamond by taking two disjoint sets and creating both their union and intersection
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();

        NestedSet<Integer> union = setA.unionWith(setB);
        NestedSet<Integer> intersection = setA.intersectionWith(setB);

        Assert.assertTrue(union.isParentOf(intersection));
        Assert.assertTrue(intersection.isChildOf(union));
    }

    @Test
    public void TestDiamondTwo() {
        // Same as the previous diamond test, but builds the intersection first, then the union
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();

        NestedSet<Integer> intersection = setA.intersectionWith(setB);
        NestedSet<Integer> union = setA.unionWith(setB);

        Assert.assertTrue(union.isParentOf(intersection));
        Assert.assertTrue(intersection.isChildOf(union));
    }

    @Test
    public void TestRepeatedUnion() {
        // The unionWith operation should be referentially transparent with respect to the object being returned
        // It also shouldn't matter through which set the union operation was called
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();
        NestedSet<Integer> union1 = setA.unionWith(setB);
        NestedSet<Integer> union2 = setA.unionWith(setB);
        NestedSet<Integer> union3 = setB.unionWith(setA);

        Assert.assertEquals(union1, union2);
        Assert.assertEquals(union1, union3);
    }

    @Test
    public void TestRepeatedIntersection() {
        // The unionWith operation should be referentially transparent with respect to the object being returned
        // It also shouldn't matter through which set the union operation was called
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();
        NestedSet<Integer> intersection1 = setA.intersectionWith(setB);
        NestedSet<Integer> intersection2 = setA.intersectionWith(setB);
        NestedSet<Integer> intersection3 = setB.intersectionWith(setA);

        Assert.assertEquals(intersection1, intersection2);
        Assert.assertEquals(intersection1, intersection3);
    }

    @Test
    public void TestSelfUnionAndIntersection() {
        NestedSet<Integer> set = new NestedSet<>();

        Assert.assertEquals(set, set.unionWith(set));
        Assert.assertEquals(set, set.intersectionWith(set));
    }

    @Test
    public void TestUnionContents() {
        NestedSet<Integer> setA = new NestedSet<>();
        setA.addItem(10);

        NestedSet<Integer> setB = new NestedSet<>();
        setB.addItem(20);

        NestedSet<Integer> union = setA.unionWith(setB);

        Assert.assertEquals(2, union.size());
        Assert.assertEquals(1, setA.size());
        Assert.assertEquals(1, setB.size());
    }

    @Test
    public void TestIntersectionContents() {
        NestedSet<Integer> setA = new NestedSet<>();
        setA.addItem(10);
        setA.addItem(20);

        NestedSet<Integer> setB = new NestedSet<>();
        setB.addItem(20);
        setB.addItem(30);

        NestedSet<Integer> intersection = setA.intersectionWith(setB);

        Assert.assertEquals(1, intersection.size());
        Assert.assertEquals(2, setA.size());
        Assert.assertEquals(2, setB.size());

        Assert.assertTrue(intersection.containsItem(20));
    }

    @Test
    public void TestInsertIntoUnion() {
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();
        NestedSet<Integer> union = setA.unionWith(setB);

        // As a parent, inserting into the union should not propagate down, but inserting below should propagate up.
        // Also, inserting to either child should propagate up, but not be reflected in the sibling set.
        union.addItem(10);
        union.addItem(20);

        Assert.assertEquals(2, union.size());
        Assert.assertEquals(0, setA.size());
        Assert.assertEquals(0, setB.size());

        setA.addItem(10); // Note that 10 already exists in the union
        setB.addItem(30);

        Assert.assertEquals(3, union.size());
        Assert.assertEquals(1, setA.size());
        Assert.assertEquals(1, setB.size());
    }

    @Test
    public void TestDeleteFromUnion() {
        NestedSet<Integer> setA = new NestedSet<>();
        setA.addItem(10);

        NestedSet<Integer> setB = new NestedSet<>();
        setB.addItem(20);

        NestedSet<Integer> union = setA.unionWith(setB);
        union.removeItem(10);

        Assert.assertEquals(1, union.size());
        Assert.assertEquals(0, setA.size());
        Assert.assertEquals(1, setB.size());
    }

    @Test
    public void TestInsertIntoIntersection() {
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();
        NestedSet<Integer> intersection = setA.intersectionWith(setB);

        // As a child, inserting into the intersection should propagate up, but not the reverse.
        intersection.addItem(10);
        intersection.addItem(20);

        Assert.assertEquals(2, intersection.size());
        Assert.assertEquals(2, setA.size());
        Assert.assertEquals(2, setB.size());

        setA.addItem(10); // Note that 10 already exists in the intersection
        setB.addItem(30);

        Assert.assertEquals(2, intersection.size());
        Assert.assertEquals(2, setA.size());
        Assert.assertEquals(3, setB.size());
    }

    @Test
    public void TestDeleteFromIntersection() {
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> setB = new NestedSet<>();

        NestedSet<Integer> intersection = setA.intersectionWith(setB);
        intersection.addItem(10);
        intersection.addItem(20);
        intersection.removeItem(20);

        Assert.assertEquals(1, intersection.size());
        Assert.assertEquals(2, setA.size());
        Assert.assertEquals(2, setB.size());
    }

    @Test
    public void TestUnionInheritsChildren() {
        // Taking the union should inherit all of the children of the two sets
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> childOfA = setA.spawnChild();

        NestedSet<Integer> setB = new NestedSet<>();
        NestedSet<Integer> childOfB = setB.spawnChild();

        NestedSet<Integer> union = setA.unionWith(setB);

        Assert.assertEquals(4, union.numberOfChildrenSets());
        Assert.assertEquals(1, setA.numberOfChildrenSets());
        Assert.assertEquals(1, setA.numberOfChildrenSets());
        Assert.assertEquals(0, childOfA.numberOfChildrenSets());
        Assert.assertEquals(0, childOfB.numberOfChildrenSets());

        Assert.assertEquals(0, union.numberOfParentSets());
        Assert.assertEquals(1, setA.numberOfParentSets());
        Assert.assertEquals(1, setA.numberOfParentSets());
        Assert.assertEquals(2, childOfA.numberOfParentSets());
        Assert.assertEquals(2, childOfB.numberOfParentSets());
    }

    @Test
    public void TestIntersectionInheritsParents() {
        // Likewise, taking the intersection should inherit all parents
        NestedSet<Integer> setA = new NestedSet<>();
        NestedSet<Integer> parentOfA = setA.spawnParent();

        NestedSet<Integer> setB = new NestedSet<>();
        NestedSet<Integer> parentOfB = setB.spawnParent();

        NestedSet<Integer> intersection = setA.intersectionWith(setB);

        Assert.assertEquals(0, intersection.numberOfChildrenSets());
        Assert.assertEquals(1, setA.numberOfChildrenSets());
        Assert.assertEquals(1, setA.numberOfChildrenSets());
        Assert.assertEquals(2, parentOfA.numberOfChildrenSets());
        Assert.assertEquals(2, parentOfB.numberOfChildrenSets());

        Assert.assertEquals(4, intersection.numberOfParentSets());
        Assert.assertEquals(1, setA.numberOfParentSets());
        Assert.assertEquals(1, setA.numberOfParentSets());
        Assert.assertEquals(0, parentOfA.numberOfParentSets());
        Assert.assertEquals(0, parentOfA.numberOfParentSets());
    }

    @Test
    public void TestLotsOfUnions() {
        // Here we form a complete binary tree structure out of unions, built from the leaves up
        // There will be 32 total sets, the last 16 of which are the leaves.
        // We use an array-based heap numbering to keep things straight.  set i has as children 2i+1 and 2i+2.
        NestedSet[] sets = new NestedSet[31];
        for(int i=30; i >= 0; i--) {
            if(i > (30 - 16)) {
                sets[i] = new NestedSet<Integer>();
            } else {
                NestedSet<Integer> setA = sets[(2 * i) + 1];
                NestedSet<Integer> setB = sets[(2 * i) + 2];
                sets[i] = setA.unionWith(setB);
            }
        }

        // Now adding to any of the leaves should propagate all the way up to the root
        sets[24].addItem(10);
        sets[27].addItem(31);
        sets[30].addItem(402);

        Assert.assertEquals(3, sets[0].size());
        Assert.assertEquals(30, sets[0].numberOfChildrenSets());
        Assert.assertEquals(0, sets[0].numberOfParentSets());

        Assert.assertEquals(14, sets[1].numberOfChildrenSets());
        Assert.assertEquals(1, sets[1].numberOfParentSets());
    }

}
