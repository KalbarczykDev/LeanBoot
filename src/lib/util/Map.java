package lib.util;

import java.util.Iterator;

public interface Map<K, V> {

    V put(K key, V value);

    V get(K key);

    V remove(K key);

    boolean containsKey(K key);

    int size();

    boolean isEmpty();

    void clear();

    Iterator<K> keyIterator();
}
