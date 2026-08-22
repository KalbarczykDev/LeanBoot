package example;

import lib.annotation.Component;
import lib.annotation.Inject;

@Component
public class ExampleService {

    private final ExampleComponent component;

    @Inject
    public ExampleService(ExampleComponent component) {
        this.component = component;
    }

    public void example() {
        component.example();
    }
}
