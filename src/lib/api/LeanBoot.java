package lib.api;

import lib.annotation.LeanBootApplication;
import lib.context.ApplicationContext;
import lib.web.HttpServer;

public class LeanBoot {
    private LeanBoot() {
    }

    public static ApplicationContext run(Class<?> applicationClass) {
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
        context.init();

        try {
            new HttpServer(8080).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return context;
    }
}
