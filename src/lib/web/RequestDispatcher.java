package lib.web;

import lib.annotation.web.RequestParam;
import lib.logging.Logger;
import lib.logging.LoggerFactory;
import lib.util.Optional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

public class RequestDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDispatcher.class);

    private final WebRouter router;

    public RequestDispatcher(WebRouter router) {
        this.router = router;
    }

    public HttpResponse dispatch(HttpRequest request) {
        LOGGER.debug("Received request: " + request);

        Optional<Route> routeOpt = router.findRoute(
                request.method(),
                request.path()
        );

        if (routeOpt.isEmpty()) {
            return HttpResponseFactory.createHttpResponse(
                    HttpStatus.NOT_FOUND,
                    "Route not found"
            );
        }

        Route route = routeOpt.get();
        Object[] arguments = resolveArguments(route, request);

        if (arguments == null) {
            throw new IllegalArgumentException("No arguments provided");
        }

        try {

            Object result = route.handlerMethod().invoke(
                    route.controller(),
                    arguments
            );

            if (!(result instanceof String body)) {
                return HttpResponseFactory.createHttpResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Controller method must return String"
                );
            }

            return HttpResponseFactory.createHttpResponse(
                    HttpStatus.OK,
                    body
            );
        } catch (InvocationTargetException exception) {
            LOGGER.error(
                    "Controller method failed: "
                            + route.handlerMethod().getName(),
                    exception.getCause()
            );

            return HttpResponseFactory.createHttpResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal Server Error"
            );
        } catch (IllegalAccessException exception) {
            LOGGER.error(
                    "Cannot access controller method: "
                            + route.handlerMethod().getName(),
                    exception
            );

            return HttpResponseFactory.createHttpResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal Server Error"
            );
        }
    }


    private Object[] resolveArguments(Route route, HttpRequest request) {
        Parameter[] parameters =
                route.handlerMethod().getParameters();

        Object[] arguments =
                new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String parameterName = getParameterName(route, parameters[i]);

            String value = request.getQueryParameter(parameterName);

            if (value == null) {
                return null;
            }
            arguments[i] = value;
        }
        return arguments;
    }

    private static String getParameterName(Route route, Parameter parameter) {

        RequestParam annotation = parameter.getAnnotation(RequestParam.class);

        if (annotation == null) {
            throw new RuntimeException(
                    "Controller parameter requires @RequestParam: "
                            + route.handlerMethod().getName()
            );
        }

        if (!parameter.getType().equals(String.class)) {
            throw new RuntimeException(
                    "Only String @RequestParam parameter are supported"
            );
        }

        return annotation.value();
    }
}
