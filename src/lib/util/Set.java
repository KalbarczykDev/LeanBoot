package lib.util;

public interface Set<T> extends Iterable<T> {
    boolean add(T item);

    boolean remove(T item);

    boolean contains(T item);

    int size();

    boolean isEmpty();

    void clear();

    @SafeVarargs
    static <T> Set<T> of(T... items) {
        Set<T> set = new HashSet<>();

        for (T item : items) {
            set.add(item);
        }

        return set;
    }

}
