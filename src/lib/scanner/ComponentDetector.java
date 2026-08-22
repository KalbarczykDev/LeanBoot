package lib.scanner;

import lib.annotation.Component;

import java.lang.annotation.Annotation;

public final class ComponentDetector {

    private ComponentDetector() {
    }

    public static boolean isComponent(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Component.class)) {
            return true;
        }

        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType =
                    annotation.annotationType();

            if (annotationType.isAnnotationPresent(Component.class)) {
                return true;
            }
        }

        return false;
    }
}