import java.util.*;

/**
 * Created by brandon on 3/26/17.
 */

public class NestedSet<T> extends HashSet<NestedSetItem<T>> {

    private Collection<NestedSet<T>> parentSets;
    private Collection<NestedSet<T>> childSets;
    private Map<NestedSet<T>, NestedSet<T>> unions;
    private Map<NestedSet<T>, NestedSet<T>> intersections;

    /*
    Different constructors for different use cases
     */

    public NestedSet() {
        this.parentSets = new HashSet<>();
        this.childSets = new HashSet<>();
    }

    public NestedSet(Collection<? extends NestedSetItem<T>> c) {
        super(c);
        this.parentSets = new HashSet<>();
        this.childSets = new HashSet<>();
        this.unions = new HashMap<>();
        this.intersections = new HashMap<>();
    }

    public NestedSet(Collection<NestedSet<T>> parentSets, Collection<NestedSet<T>> childSets) {
        this.parentSets = parentSets;
        this.childSets = childSets;
    }

    /*
    Add and remove parent and child NestedSets
     */

    private void addParentSet(NestedSet<T> set) {
        this.parentSets.add(set);
    }

    private void addChildSet(NestedSet<T> set) {
        this.childSets.add(set);
    }

    /*
    Build a NestedSet that is the union of this with another similarly-typed NestedSet
    Updates the unions Map of both this NestedSet and the one passed as an argument, and also
        adds the newly-formed union as a parent of both NestedSets
     */

    public NestedSet<T> unionWith(NestedSet<T> other) {
        if (!this.unions.containsKey(other)) {
            NestedSet<T> newUnion = buildUnionWith(other);

            this.unions.put(other, newUnion);
            other.unions.put(this, newUnion);

            this.addParentSet(newUnion);
            other.addParentSet(newUnion);
        }
        return this.unions.get(other);
    }

    private NestedSet<T> buildUnionWith(NestedSet<T> other) {
        return new NestedSet<>(new HashSet<>(), Arrays.asList(this, other));
    }

    /*
    Build a NestedSet that is the intersection of this with another similarly-typed NestedSet
    Updates the intersections Map of both this NestedSet and the one passed as an argument, and also
        adds the newly-formed intersection as a child of both NestedSets
     */

    public NestedSet<T> intersectionWith(NestedSet<T> other) {
        if (!this.intersections.containsKey(other)) {
            NestedSet<T> newIntersection = buildIntersectionWith(other);

            this.intersections.put(other, newIntersection);
            other.intersections.put(this, newIntersection);

            this.addChildSet(newIntersection);
            other.addChildSet(newIntersection);
        }
        return this.intersections.get(other);
    }

    private NestedSet<T> buildIntersectionWith(NestedSet<T> other) {
        return new NestedSet<>(Arrays.asList(this, other), new HashSet<>());
    }

    /*
    Add item to this set
    Also recursively adds it to all parents, maintaining the subset relationships.
     */

    public void addItem(T value) {
        this.addSetItem(new NestedSetItem<>(value));
    }

    private void addSetItem(NestedSetItem<T> setItem) {
        // Attempt to put this setItem in this NestedSet.
        // Note that Collections.add() returns true if the collection was modified (i.e. the element
        //      was added)
        if(this.add(setItem)) {

            setItem.addContainingSet(this);
            addToAllParents(setItem);

        }
    }

    private void addToAllParents(NestedSetItem<T> setItem) {
        // Now we recursively add it to each of our parent NestedSets
        Iterator<NestedSet<T>> it = this.parentSets.iterator();
        while (it.hasNext()) {
            NestedSet<T> parent = it.next();
            parent.addSetItem(setItem);
        }
    }

    /*
    Remove an item from this NestedSet
    Also recursively removes it from all children sets, preserving the subset relationship
     */

    public void removeItem(T value) {
        this.removeSetItem(new NestedSetItem<>(value));
    }

    private void removeSetItem(NestedSetItem<T> setItem) {
        // Attempt to remove set item from this set.
        // Note that Collections.remove() returns true if the item was removed successfully
        if(this.remove(setItem)) {
            setItem.removeContainingSet(this);
            removeFromChildren(setItem);
        }
    }

    private void removeFromChildren(NestedSetItem<T> setItem) {
        // Recursively remove it from all children sets
        Iterator<NestedSet<T>> it = this.childSets.iterator();
        while (it.hasNext()) {
            NestedSet<T> parent = it.next();
            parent.removeSetItem(setItem);
        }
    }
}
