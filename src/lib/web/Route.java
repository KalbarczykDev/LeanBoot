package lib.web;


import java.lang.reflect.Method;

public record Route(
        HttpMethod httpMethod,
        String path,
        Object controller,
        Method handlerMethod //class method discovered at runtime
) {
}