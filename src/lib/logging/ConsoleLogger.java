package lib.logging;

public class ConsoleLogger implements Logger {

    private final String name;

    public ConsoleLogger(String name) {
        this.name = name;
    }

    @Override
    public void debug(String message) {
        log(System.out, LogLevel.DEBUG, message);
    }

    @Override
    public void info(String message) {
        log(System.out, LogLevel.INFO, message);
    }

    @Override
    public void warn(String message) {
        log(System.out, LogLevel.WARN, message);
    }

    @Override
    public void error(String message) {
        log(System.err, LogLevel.ERROR, message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        error(message);
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    private void log(java.io.PrintStream output, LogLevel level, String message) {
        output.printf("[%s] [%s] %s%n", level, name, message);
    }

}
