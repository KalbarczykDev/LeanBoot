package lib.scanner;

import lib.annotation.Controller;
import lib.annotation.web.GetMapping;
import lib.context.ApplicationContext;
import lib.logging.Logger;
import lib.logging.LoggerFactory;
import lib.util.List;
import lib.web.HttpMethod;
import lib.web.Route;
import lib.web.WebRouter;

import java.lang.reflect.Method;

public class ControllerRouteScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerRouteScanner.class);

    public void scan(
            ApplicationContext context,
            WebRouter router
    ) {
        List<Object> controllers =
                context.getBeansAnnotatedWith(Controller.class);

        for (Object controller : controllers) {
            Method[] methods =
                    controller.getClass().getDeclaredMethods();

            for (Method method : methods) {
                if (!method.isAnnotationPresent(GetMapping.class)) {
                    continue;
                }

                GetMapping mapping =
                        method.getAnnotation(GetMapping.class);

                Route route = new Route(
                        HttpMethod.GET,
                        mapping.value(),
                        controller,
                        method
                );

                router.registerRoute(route);
                LOGGER.debug(
                        "Registered route "
                                + route.httpMethod()
                                + " "
                                + route.path()
                );
            }
        }
    }
}
