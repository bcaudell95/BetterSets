import java.util.Collection;
import java.util.Iterator;

/**
 * Created by brandon on 3/26/17.
 */
public class NestedSetItem<T> {
    private T value;
    private Collection<NestedSet<T>> sets;

    public NestedSetItem(T value) {
        this.value = value;
    }

    public void addContainingSet(NestedSet<T> set) {
        this.sets.add(set);
    }

    public void removeContainingSet(NestedSet<T> set) {
        this.sets.remove(set);
    }

    public void removeFromAllSets() {
        Iterator<NestedSet<T>> it = this.sets.iterator();
        while(it.hasNext()) {
            NestedSet<T> next = it.next();
            next.remove(this);
        }
    }

    public T getValue() {
        return value;
    }
}
