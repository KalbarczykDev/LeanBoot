package example;

import lib.annotation.LeanBootApplication;
import lib.api.LeanBoot;

@LeanBootApplication
public class Application {
    void main() {
        LeanBoot.run(Application.class);
    }
}
