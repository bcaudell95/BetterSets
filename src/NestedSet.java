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
        this.unions = new HashMap<>();
        this.intersections = new HashMap<>();
    }

    public NestedSet(Collection<? extends NestedSetItem<T>> c) {
        super(c);
        this.parentSets = new HashSet<>();
        this.childSets = new HashSet<>();
        this.unions = new HashMap<>();
        this.intersections = new HashMap<>();
    }

    // This constructor is private because initialization of parent and children sets needs to be handled
    //      with care.  
    private NestedSet(Collection<NestedSet<T>> parentSets, Collection<NestedSet<T>> childSets) {
        this.parentSets = parentSets;
        this.childSets = childSets;
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
        for (NestedSet<T> parent : this.parentSets) {
            parent.add(setItem);
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
        for (NestedSet<T> parent : this.childSets) {
            parent.remove(setItem);
        }
    }

    /*
    Add and remove parent and child NestedSets
    Note that when a child is added, it is inherited by all parents, and when a parent is added, it inherits all children
     */

    private void addParentSet(NestedSet<T> parent) {
        this.parentSets.add(parent);
        for (NestedSet<T> child : this.childSets) {
            child.parentSets.add(parent);
        }
    }

    private void addChildSet(NestedSet<T> child) {
        this.childSets.add(child);
        for(NestedSet<T> parent : this.parentSets) {
            parent.addChildSet(child);
        }
    }

    /*
    Build a NestedSet that is the union of this with another similarly-typed NestedSet
    Updates the unions Map of both this NestedSet and the one passed as an argument, and also
        adds the newly-formed union as a parent of both NestedSets

    Note that if either of these two sets is a child of the other, then the parent set IS the union
     */

    public NestedSet<T> unionWith(NestedSet<T> other) {
        if(this.childSets.contains(other)) {
            return this;
        } else if(this.parentSets.contains(other)) {
            return other;
        } else {
            return lookupUnionWith(other);
        }
    }

    private NestedSet<T> lookupUnionWith(NestedSet<T> other) {
        // Perform a lookup in the HashMap of unions
        if (!this.unions.containsKey(other)) {
            NestedSet<T> newUnion = buildSimpleUnionWith(other);

            this.unions.put(other, newUnion);
            other.unions.put(this, newUnion);

            this.addParentSet(newUnion);
            other.addParentSet(newUnion);
        }
        return this.unions.get(other);
    }

    private NestedSet<T> buildSimpleUnionWith(NestedSet<T> other) {
        // We need to accumulate all the children of this union
        // Those would be these two sets, as well as all children of each
        HashSet<NestedSet<T>> childrenOfUnion = new HashSet<>(Arrays.asList(this, other));
        childrenOfUnion.addAll(this.childSets);
        childrenOfUnion.addAll(other.childSets);

        return new NestedSet<>(new HashSet<>(), childrenOfUnion);
    }

    /*
    Gets a NestedSet that is the intersection of this with another similarly-typed NestedSet
    Updates the intersections Map of both this NestedSet and the one passed as an argument, and also
        adds the newly-formed intersection as a child of both NestedSets

    Note that if either of these two sets is a child of the other, then the child set IS the intersection
     */

    public NestedSet<T> intersectionWith(NestedSet<T> other) {
        if(this.childSets.contains(other)) {
            return other;
        } else if(this.parentSets.contains(other)) {
            return this;
        } else {
            return lookupIntersectionWith(other);
        }
    }

    private NestedSet<T> lookupIntersectionWith(NestedSet<T> other) {
        if (!this.intersections.containsKey(other)) {
            NestedSet<T> newIntersection = buildSimpleIntersectionWith(other);

            this.intersections.put(other, newIntersection);
            other.intersections.put(this, newIntersection);

            this.addChildSet(newIntersection);
            other.addChildSet(newIntersection);
        }
        return this.intersections.get(other);
    }

    private NestedSet<T> buildSimpleIntersectionWith(NestedSet<T> other) {
        // Accumulate all the parents of the intersection, which is these two sets plus all their respective parents
        HashSet<NestedSet<T>> parentsOfIntersection = new HashSet<>(Arrays.asList(this, other));
        parentsOfIntersection.addAll(this.parentSets);
        parentsOfIntersection.addAll(other.parentSets);

        return new NestedSet<>(parentsOfIntersection, new HashSet<>());
    }
}
