import example.ExampleService;
import example.ExampleComponent;
import lib.context.ApplicationContext;


void main() {
    ApplicationContext context = new ApplicationContext();

    context.register(ExampleComponent.class);
    context.register(ExampleService.class);
    context.init();

    ExampleService service = (ExampleService) context.getBean(ExampleService.class);
    service.example();
}
