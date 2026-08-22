package lib.bean;

import lib.annotation.Component;
import lib.exception.DuplicateBeanException;
import lib.exception.NotAComponentException;
import lib.util.LinkedList;
import lib.util.Optional;

public class BeanRegistry {
    //TODO: replace with HashMap after implementing custom version?
    private final LinkedList<BeanDefinition> definitions;

    public BeanRegistry() {
        this.definitions = new LinkedList<>();
    }

    public void register(Class<?> clazz) {

        if (!clazz.isAnnotationPresent(Component.class)) {
            throw new NotAComponentException(clazz.getName());
        }

        if (findByType(clazz).isPresent()) {
            throw new DuplicateBeanException(clazz.getName());
        }

        definitions.add(new BeanDefinition(clazz));
    }

    public Optional<BeanDefinition> findByType(Class<?> type) {
        for (int i = 0; i < definitions.size(); i++) {
            if (definitions.get(i).getBeanClass().equals(type)) {
                return Optional.of(definitions.get(i));
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
