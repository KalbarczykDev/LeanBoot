package example;

import lib.annotation.Component;

@Component
public class ExampleService {

    private final ExampleComponent component;

    public ExampleService(ExampleComponent component) {
        this.component = component;
    }

    public void example() {
        component.example();
    }
}
