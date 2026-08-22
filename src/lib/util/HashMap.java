package lib.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class HashMap<K, V> implements Map<K, V> {

    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Entry<K, V>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public HashMap() {
        buckets = (Entry<K, V>[]) new Entry[INITIAL_CAPACITY];
    }

    @Override
    public V put(K key, V value) {
        int index = bucketIndex(key, buckets.length);
        Entry<K, V> current = buckets[index];

        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                V previousValue = current.getValue();
                current.setValue(value);
                return previousValue;
            }
            current = current.getNext();
        }

        if (size + 1 >= buckets.length * LOAD_FACTOR) {
            resize();
            index = bucketIndex(key, buckets.length);
        }

        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;

        return null;
    }

    @Override
    public V get(K key) {
        Entry<K, V> entry = findEntry(key);
        return entry == null ? null : entry.getValue();
    }

    @Override
    public V remove(K key) {
        int index = bucketIndex(key, buckets.length);
        Entry<K, V> current = buckets[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                if (prev == null) {
                    buckets[index] = current.getNext();
                } else {
                    prev.setNext(current.getNext());
                }
                size--;
                return current.getValue();
            }
            prev = current;
            current = current.getNext();
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return findEntry(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        buckets = (Entry<K, V>[]) new Entry[INITIAL_CAPACITY];
        size = 0;
    }

    @Override
    public Iterator<K> keyIterator() {
        return new Iterator<>() {

            private int bucketIndex;
            private Entry<K, V> current;

            {
                moveToNextBucket();
            }

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                K key = current.getKey();
                current = current.getNext();

                if (current == null) {
                    bucketIndex++;
                    moveToNextBucket();
                }

                return key;
            }

            private void moveToNextBucket() {
                while (bucketIndex < buckets.length
                        && buckets[bucketIndex] == null) {
                    bucketIndex++;
                }

                current = bucketIndex < buckets.length
                        ? buckets[bucketIndex]
                        : null;
            }
        };
    }

    private int bucketIndex(K key, int capacity) {
        int hash = key == null ? 0 : key.hashCode();
        hash ^= hash >>> 16;

        return (hash & 0x7FFFFFFF) % capacity;
    }

    private Entry<K, V> findEntry(K key) {
        int index = bucketIndex(key, buckets.length);
        Entry<K, V> current = buckets[index];

        while (current != null) {
            if (Objects.equals(current.getKey(), key)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;
        buckets = (Entry<K, V>[]) new Entry[oldBuckets.length * 2];

        for (Entry<K, V> bucket : oldBuckets) {
            Entry<K, V> current = bucket;

            while (current != null) {
                Entry<K, V> next = current.getNext();
                int index = bucketIndex(current.getKey(), buckets.length);

                current.setNext(buckets[index]);
                buckets[index] = current;

                current = next;
            }
        }
    }
}
