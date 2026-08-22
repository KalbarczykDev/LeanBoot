package example;

import lib.annotation.Inject;
import lib.annotation.Service;

@Service
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
