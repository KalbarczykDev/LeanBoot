package lib.logging;

public class LoggerFactory {

    private LoggerFactory() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return new ConsoleLogger(
                clazz.getSimpleName()
        );
    }
}
