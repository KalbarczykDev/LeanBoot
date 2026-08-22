package lib.exception;

public class DuplicateBeanException extends RuntimeException {
    public DuplicateBeanException(String message) {
        super(message);
    }
}
