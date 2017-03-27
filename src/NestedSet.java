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
        createEmptyUnionAndIntersectionSets();
    }

    public NestedSet(Collection<? extends NestedSetItem<T>> c) {
        super(c);
        this.parentSets = new HashSet<>();
        this.childSets = new HashSet<>();
        createEmptyUnionAndIntersectionSets();
    }

    // This constructor is private because initialization of parent and children sets needs to be handled
    //      with care.
    private NestedSet(Collection<NestedSet<T>> parentSets, Collection<NestedSet<T>> childSets) {
        this.parentSets = parentSets;
        this.childSets = childSets;
        this.createEmptyUnionAndIntersectionSets();
    }

    private void createEmptyUnionAndIntersectionSets() {
        this.unions = new HashMap<>();
        this.intersections = new HashMap<>();
    }

    /*
    Add item to this set
    Also adds it to all parents, maintaining the subset relationships.
     */

    public boolean addItem(T value) {
        return this.addSetItem(new NestedSetItem<>(value));
    }

    private boolean addSetItem(NestedSetItem<T> setItem) {
        // Attempt to put this setItem in this NestedSet.
        // Note that Collections.add() returns true if the collection was modified (i.e. the element
        //      was added)
        boolean result = super.add(setItem);
        if(result) {

            setItem.addContainingSet(this);
            addToAllParents(setItem);

        }
        return result;
    }

    private void addToAllParents(NestedSetItem<T> setItem) {
        for (NestedSet<T> parent : this.parentSets) {
            setItem.addContainingSet(parent);
            parent.add(setItem);
        }
    }

    /*
    Remove an item from this NestedSet
    Also removes it from all children sets, preserving the subset relationship
     */

    public boolean removeItem(T value) {
        return this.removeSetItem(new NestedSetItem<>(value));
    }

    private boolean removeSetItem(NestedSetItem<T> setItem) {
        // Attempt to remove set item from this set.
        // Note that Collections.remove() returns true if the item was removed successfully
        boolean result = this.remove(setItem);
        if(result) {
            setItem.removeContainingSet(this);
            removeFromChildren(setItem);
        }
        return result;
    }

    private void removeFromChildren(NestedSetItem<T> setItem) {
        for (NestedSet<T> parent : this.childSets) {
            setItem.removeContainingSet(parent);
            parent.remove(setItem);
        }
    }

    /*
    Creates an immediate child set below this one.
    That child inherits all of this set's children, and is inherited by all of this set's parents
     */

    public NestedSet<T> spawnChild() {
        // Get a set of all its parents
        Collection<NestedSet<T>> itsParents = new ArrayList<>(this.parentSets);
        itsParents.add(this);

        // Likewise for its children
        Collection<NestedSet<T>> itsChildren = new ArrayList<>(this.childSets);

        // Spawn the new child
        NestedSet<T> newChild = new NestedSet<>(itsParents, itsChildren);

        // Populate the child with all the elements from this set
        newChild.addAll(this);

        // Add the new child as a containing set for all the items in this set
        for (NestedSetItem<T> item: this) {
            item.addContainingSet(newChild);
        }

        // Assign it as a child to all its parents (will be done via loop in this method)
        this.addChildSet(newChild);

        // Assign it as a parent to all its children (grandchildren of this)
        for(NestedSet<T> grandchild : itsChildren) {
            grandchild.parentSets.add(newChild);
        }

        return newChild;
    }

    /*
    Creates an immediate parent set above this one.
    That parent inherits all of this set's children, and is inherited by all of this set's parents
     */

    public NestedSet<T> spawnParent() {
        // Get a set of all its parents
        HashSet<NestedSet<T>> itsParents = new HashSet<>(this.parentSets);

        // Likewise for its children
        HashSet<NestedSet<T>> itsChildren = new HashSet<>(this.childSets);
        itsChildren.add(this);

        // Spawn the new parent
        NestedSet<T> newParent = new NestedSet<>(itsParents, itsChildren);

        // Populate the parent
        newParent.addAll(this);

        // Add the new parent as a containing set for all the items in this set
        for (NestedSetItem<T> item: this) {
            item.addContainingSet(newParent);
        }

        // Assign it as a parent to all its children (will be done via loop in this method)
        this.addParentSet(newParent);

        // Assign it as a child to all its parents (grandparents of this)
        for(NestedSet<T> grandparent : itsParents) {
            grandparent.childSets.add(newParent);
        }

        return newParent;
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

        // instantiate the new union
        NestedSet<T> newUnion = new NestedSet<>(new HashSet<>(), childrenOfUnion);

        // populate it with everything from the union of these sets
        HashSet<NestedSetItem<T>> itemsToInsert = new HashSet<>(this);
        itemsToInsert.addAll(other);
        newUnion.addAll(itemsToInsert);

        // add the new union as a containing set for all the inserted items
        for(NestedSetItem<T> item : itemsToInsert) {
            item.addContainingSet(newUnion);
        }

        return newUnion;
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

        // Instantiate the new intersection set
        NestedSet<T> newIntersection = new NestedSet<>(parentsOfIntersection, new HashSet<>());

        // Populate it with everything in the intersection of these two sets
        HashSet<NestedSetItem<T>> itemsToInsert = new HashSet<>(this);
        itemsToInsert.retainAll(other);
        newIntersection.addAll(itemsToInsert);

        // add the new union as a containing set for all the inserted items
        for(NestedSetItem<T> item : itemsToInsert) {
            item.addContainingSet(newIntersection);
        }

        return newIntersection;
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
            parent.childSets.add(child);
        }
    }

    /*
    Methods to determine the relationship between two NestedSets
     */

    public boolean isChildOf(NestedSet<T> other) {
        return this.parentSets.contains(other);
    }

    public boolean isParentOf(NestedSet<T> other) {
        return this.childSets.contains(other);
    }

    /*
    Small methods mostly used for testing
     */
    public int numberOfChildrenSets() {
        return this.childSets.size();
    }

    public int numberOfParentSets() {
        return this.parentSets.size();
    }

    /*
    AbstractSet has some goofy implementations of equals and hashCode that cause lots of problems with this class.
    I need to go back and understand why they do what they do, but in the meantime, using the Object implementation of
        these methods here seems to work just fine.
     */

    @Override
    public boolean equals(Object o) {
        NestedSet<T> other = (NestedSet<T>) o;
        return this.hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
