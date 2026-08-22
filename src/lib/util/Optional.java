package lib.util;

public class Optional <T>{

    private final T data;

    private Optional(T data) {
        this.data = data;
    }

    public static <T> Optional<T> of(T data) {
        return new Optional<T>(data);
    }

    public static <T> Optional<T> empty(){
        return new Optional<T>(null);
    }

    public T get() {
        return data;
    }

    public boolean isPresent() {
        return data != null;
    }

    public boolean isEmpty() {
        return data == null;
    }
}
