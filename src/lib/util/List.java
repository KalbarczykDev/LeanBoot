package lib.util;

public interface List<T> extends Iterable<T> {
    T get(int index);

    void add(T item);

    void add(int index, T item);

    boolean remove(int index);

    int size();

}
