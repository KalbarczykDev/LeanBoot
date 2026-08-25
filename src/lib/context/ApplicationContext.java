package lib.context;

import lib.annotation.Inject;
import lib.annotation.Qualifier;
import lib.bean.BeanDefinition;
import lib.bean.BeanRegistry;
import lib.bean.BeanState;
import lib.exception.CircularDependencyException;
import lib.logging.Logger;
import lib.logging.LoggerFactory;
import lib.scanner.ComponentScanner;
import lib.util.LinkedList;
import lib.util.List;
import lib.util.Optional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);
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

    public List<Object> getBeansAnnotatedWith(
            Class<? extends Annotation> annotationType
    ) {
        List<Object> beans = new LinkedList<>();

        for (int i = 0; i < registry.size(); i++) {
            BeanDefinition definition = registry.get(i);
            Class<?> beanClass = definition.getBeanClass();

            if (beanClass.isAnnotationPresent(annotationType)) {
                beans.add(createBean(definition));
            }
        }
        return beans;
    }

    private Object createBean(BeanDefinition definition) {
        LOGGER.debug("Creating bean " + definition.getBeanClass().getName());
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

            Parameter[] parameters =
                    constructor.getParameters();

            Object[] arguments =
                    new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];

                Class<?> requiredType = parameter.getType();
                String qualifier = null;

                if (parameter.isAnnotationPresent(Qualifier.class)) {
                    Qualifier qualifierAnnotation =
                            parameter.getAnnotation(Qualifier.class);

                    qualifier = qualifierAnnotation.value();
                }

                Optional<BeanDefinition> dependency =
                        registry.resolve(requiredType, qualifier);

                if (dependency.isEmpty()) {
                    String message =
                            "Dependency not found: "
                                    + requiredType.getName()
                                    + " required by "
                                    + beanClass.getName();

                    if (qualifier != null) {
                        message +=
                                " with qualifier '" + qualifier + "'";
                    }

                    throw new RuntimeException(message);
                }

                arguments[i] = createBean(dependency.get());
            }
            Object instance = constructor.newInstance(arguments);
            definition.setInstance(instance);
            definition.setState(BeanState.CREATED);
            LOGGER.debug("Created bean " + definition.getBeanClass().getName());
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
