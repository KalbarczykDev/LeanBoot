package lib.bean;

import lib.annotation.Primary;
import lib.annotation.Qualifier;
import lib.exception.DuplicateBeanException;
import lib.exception.MultipleBeansException;
import lib.exception.NotAComponentException;
import lib.logging.Logger;
import lib.logging.LoggerFactory;
import lib.scanner.ComponentDetector;
import lib.util.LinkedList;
import lib.util.Optional;

public class BeanRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanRegistry.class);

    private final LinkedList<BeanDefinition> definitions;

    public BeanRegistry() {
        this.definitions = new LinkedList<>();
    }

    public void register(Class<?> clazz) {
        if (!ComponentDetector.isComponent(clazz)) {
            throw new NotAComponentException(clazz.getName());
        }

        if (findExact(clazz).isPresent()) {
            throw new DuplicateBeanException(clazz.getName());
        }
        definitions.add(new BeanDefinition(clazz));
        LOGGER.debug("Registered bean " + clazz.getName());
    }

    public Optional<BeanDefinition> findByType(Class<?> type) {
        return resolve(type, null);
    }

    public Optional<BeanDefinition> resolve(
            Class<?> requiredType,
            String requestedQualifier
    ) {
        if (requestedQualifier != null) {
            return resolveQualified(
                    requiredType,
                    requestedQualifier
            );
        }

        return resolveUnqualified(requiredType);
    }

    private Optional<BeanDefinition> resolveQualified(
            Class<?> requiredType,
            String requestedQualifier
    ) {
        BeanDefinition match = null;

        for (BeanDefinition definition : definitions) {
            Class<?> beanClass = definition.getBeanClass();

            if (!requiredType.isAssignableFrom(beanClass)) {
                continue;
            }

            Qualifier beanQualifier =
                    beanClass.getAnnotation(Qualifier.class);

            if (beanQualifier == null) {
                continue;
            }

            if (!beanQualifier.value().equals(requestedQualifier)) {
                continue;
            }

            if (match != null) {
                throw new MultipleBeansException(
                        "Multiple beans found for "
                                + requiredType.getName()
                                + " with qualifier '"
                                + requestedQualifier
                                + "': "
                                + match.getBeanClass().getName()
                                + ", "
                                + beanClass.getName()
                );
            }

            match = definition;
        }

        if (match == null) {
            return Optional.empty();
        }

        return Optional.of(match);
    }

    private Optional<BeanDefinition> resolveUnqualified(
            Class<?> requiredType
    ) {
        BeanDefinition firstMatch = null;
        BeanDefinition primaryMatch = null;
        int matches = 0;

        for (BeanDefinition definition : definitions) {
            Class<?> beanClass = definition.getBeanClass();

            if (!requiredType.isAssignableFrom(beanClass)) {
                continue;
            }

            matches++;

            if (firstMatch == null) {
                firstMatch = definition;
            }

            if (beanClass.isAnnotationPresent(Primary.class)) {
                if (primaryMatch != null) {
                    throw new MultipleBeansException(
                            "Multiple @Primary beans found for "
                                    + requiredType.getName()
                                    + ": "
                                    + primaryMatch
                                    .getBeanClass()
                                    .getName()
                                    + ", "
                                    + beanClass.getName()
                    );
                }

                primaryMatch = definition;
            }
        }

        if (matches == 0) {
            return Optional.empty();
        }

        if (matches == 1) {
            return Optional.of(firstMatch);
        }

        if (primaryMatch != null) {
            return Optional.of(primaryMatch);
        }

        throw new MultipleBeansException(
                "Multiple beans found for "
                        + requiredType.getName()
                        + " and none is marked @Primary"
        );
    }

    private Optional<BeanDefinition> findExact(
            Class<?> beanClass
    ) {
        for (BeanDefinition definition : definitions) {
            if (definition.getBeanClass().equals(beanClass)) {
                return Optional.of(definition);
            }
        }

        return Optional.empty();
    }

    public int size() {
        return definitions.size();
    }

    public BeanDefinition get(int index) {
        return definitions.get(index);
    }
}