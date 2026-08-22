package lib.context;

import lib.bean.BeanDefinition;
import lib.bean.BeanRegistry;
import lib.util.Optional;

public class ApplicationContext {
    private final BeanRegistry registry;

    public ApplicationContext() {
        this.registry = new BeanRegistry();
    }

    public void init() {
        // ?
    }

    public void register(Class<?> clazz) {
        registry.register(clazz);
    }

    public Object getBean(Class<?> clazz) {

        Optional<BeanDefinition> beanOpt = registry.findByType(clazz);

        if (beanOpt.isEmpty()) {
            throw new RuntimeException("bean not found");
        }
        return beanOpt.get().getInstance();

    }
}
