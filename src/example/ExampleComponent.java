package example;

import lib.annotation.Component;
import lib.annotation.Inject;

import static java.lang.IO.println;

@Component
public class ExampleComponent {

    @Inject
    public ExampleComponent() {
    }

    public void example(){
        println("Example Component");
    }
}
