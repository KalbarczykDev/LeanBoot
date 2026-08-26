package lib.api;

import lib.annotation.LeanBootApplication;
import lib.context.ApplicationContext;
import lib.logging.LoggerFactory;
import lib.scanner.ControllerRouteScanner;
import lib.web.HttpServer;

import lib.logging.Logger;
import lib.web.RequestDispatcher;
import lib.web.WebRouter;

public class LeanBoot {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeanBoot.class);

    private LeanBoot() {
    }

    public static ApplicationContext run(Class<?> applicationClass) {
        LOGGER.info("Starting LeanBoot application: " + applicationClass.getName());
        if (!applicationClass.isAnnotationPresent(LeanBootApplication.class)) {
            throw new RuntimeException(
                    "Missing @LeanBootApplication on "
                            + applicationClass.getName()
            );
        }

        String basePackage = applicationClass.getPackageName();

        if (basePackage.isEmpty()) {
            throw new RuntimeException(
                    "@LeanBootApplication cannot be used "
                            + "from the default package"
            );
        }

        ApplicationContext context = new ApplicationContext();

        context.scan(basePackage);
        LOGGER.info("Component scanning completed for package: " + basePackage);
        context.init();
        LOGGER.info("Application Context initialized");

        WebRouter router = new WebRouter();

        ControllerRouteScanner routeScanner = new ControllerRouteScanner();
        LOGGER.info("Controller route scanner initialized");
        routeScanner.scan(context, router);
        LOGGER.info("Controller route scanner completed for package: " + basePackage);

        RequestDispatcher dispatcher = new RequestDispatcher(router);

        HttpServer server = new HttpServer(8080, dispatcher);

        Thread.ofPlatform()
                .name("leanboot-server")
                .start(server::start);

        return context;
    }
}
