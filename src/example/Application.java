package example;

import lib.annotation.LeanBootApplication;
import lib.api.LeanBoot;
import lib.context.ApplicationContext;

@LeanBootApplication
public class Application {
    void main() {
        ApplicationContext context = LeanBoot.run(Application.class);

        NotificationService service = context.getBean(NotificationService.class);
        service.send();
    }
}
