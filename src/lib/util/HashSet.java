package lib.util;

import java.util.Iterator;

public class HashSet<T> implements Set<T> {

    private static final Object PRESENT = new Object();
    private final HashMap<T, Object> map;

    public HashSet() {
        map = new HashMap<>();
    }

    @Override
    public boolean add(T item) {
        return map.put(item, PRESENT) == null;
    }

    @Override
    public boolean remove(T item) {
        return map.remove(item) != null;
    }

    @Override
    public boolean contains(T item) {
        return map.containsKey(item);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return map.keyIterator();
    }
}
