package lib.context;

import lib.annotation.Inject;
import lib.bean.BeanDefinition;
import lib.bean.BeanRegistry;
import lib.bean.BeanState;
import lib.exception.CircularDependencyException;
import lib.scanner.ComponentScanner;
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


    public <T> T getBean(Class<T> clazz) {

        Optional<BeanDefinition> beanOpt = registry.findByType(clazz);

        if (beanOpt.isEmpty()) {
            throw new RuntimeException("bean not found");
        }

        return clazz.cast(createBean(beanOpt.get()));
    }

    private Object createBean(BeanDefinition definition) {

        if (definition.getState() == BeanState.CREATED) {
            return definition.getInstance();
        }

        if (definition.getState() == BeanState.CREATING) {
            throw new CircularDependencyException(
                    "circular dependency when creating bean " +
                            definition.getBeanClass().getName()
            );
        }

        definition.setState(BeanState.CREATING);

        try {

            Class<?> beanClass = definition.getBeanClass();
            Constructor<?> constructor = resolveConstructor(beanClass);

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
            definition.setState(BeanState.CREATED);
            return instance;
        } catch (Exception e) {
            definition.setState(BeanState.NOT_CREATED);
            throw new RuntimeException(
                    "Could not create bean: " + definition.getBeanClass().getName(),
                    e
            );
        }
    }

    private Constructor<?> resolveConstructor(Class<?> clazz) {

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> injectConstructor = null;

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                if (injectConstructor != null) {
                    throw new RuntimeException(
                            "Multiple @Inject annotations found on "
                                    + clazz.getName()
                    );
                }
                injectConstructor = constructor;
            }
        }

        if (injectConstructor != null) {
            return injectConstructor;
        }

        if (constructors.length == 1) {
            return constructors[0];
        }

        throw new RuntimeException(
                "No @Inject constructor found: "
                        + clazz.getName()
        );
    }

    public void scan(String basePackage) {
        ComponentScanner scanner =
                new ComponentScanner(registry);

        scanner.scan(basePackage);
    }
}
