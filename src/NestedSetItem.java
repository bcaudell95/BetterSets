import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by brandon on 3/26/17.
 */
public class NestedSetItem<T> {
    private T value;
    private Collection<NestedSet<T>> sets;

    NestedSetItem(T value) {
        this.value = value;
        this.sets = new HashSet<>();
    }

    void addContainingSet(NestedSet<T> set) {
        this.sets.add(set);
    }

    void removeContainingSet(NestedSet<T> set) {
        this.sets.remove(set);
    }

    public void removeFromAllSets() {
        for (NestedSet<T> containingSet : this.sets) {
            containingSet.remove(this);
        }
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof NestedSetItem) && (this.value == ((NestedSetItem) o).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
