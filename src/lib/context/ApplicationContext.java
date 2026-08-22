package lib.context;

import lib.bean.BeanDefinition;
import lib.bean.BeanRegistry;
import lib.util.Optional;

import java.lang.reflect.Constructor;

public class ApplicationContext {
    private final BeanRegistry registry;

    public ApplicationContext() {
        this.registry = new BeanRegistry();
    }

    public void init() {
        for (int i = 0; i < registry.size(); i++) {
            createBean(registry.get(i));
        }
    }


    public void register(Class<?> clazz) {
        registry.register(clazz);
    }

    public <T> T getBean(Class<T> clazz){

        Optional<BeanDefinition> beanOpt = registry.findByType(clazz);

        if (beanOpt.isEmpty()) {
            throw new RuntimeException("bean not found");
        }

        return clazz.cast(beanOpt.get().getInstance());
    }

    private Object createBean(BeanDefinition definition) {

        if (definition.getInstance() != null) {
            return definition.getInstance();
        }

        try {

            Class<?> beanClass = definition.getBeanClass();
            Constructor<?> constructor =
                    beanClass.getDeclaredConstructors()[0];

            Class<?>[] parameters =
                    constructor.getParameterTypes();

            Object[] arguments =
                    new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Optional<BeanDefinition> dependency =
                        registry.findByType(parameters[i]);

                if (dependency.isEmpty()) {
                    throw new RuntimeException(
                            "Dependency not found: "
                                    + parameters[i].getName()
                                    + " required by "
                                    + beanClass.getName()
                    );
                }

                arguments[i] = createBean(dependency.get());
            }
            Object instance = constructor.newInstance(arguments);
            definition.setInstance(instance);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not create bean: " + definition.getBeanClass().getName(),
                    e
            );
        }
    }
}
