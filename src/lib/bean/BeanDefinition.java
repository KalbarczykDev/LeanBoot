package lib.bean;

public class BeanDefinition {
    private final Class<?> beanClass;
    private Object instance;
    private BeanState state;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        state = BeanState.NOT_CREATED;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public BeanState getState() {
        return state;
    }

    public void setState(BeanState state) {
        this.state = state;
    }
}
