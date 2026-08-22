package lib.scanner;

import lib.bean.BeanRegistry;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

public class ComponentScanner {
    private final BeanRegistry registry;

    public ComponentScanner(BeanRegistry registry) {
        this.registry = registry;
    }

    public void scan(String basePackage) {
        try {
            String packagePath = basePackage.replace('.', '/');

            ClassLoader classLoader =
                    Thread.currentThread().getContextClassLoader();

            Enumeration<URL> resources =
                    classLoader.getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.toURI());
                    scanDirectory(directory, basePackage, classLoader);
                } else {
                    throw new RuntimeException(
                            "Unsupported classpath protocol: "
                                    + resource.getProtocol()
                    );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not scan package: " + basePackage,
                    e
            );
        }

    }

    private void scanDirectory(
            File directory,
            String packageName,
            ClassLoader classLoader
    ) {
        File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(
                        file,
                        packageName + "." + file.getName(),
                        classLoader
                );
                continue;
            }

            if (file.getName().endsWith(".class")) {
                String simpleName = file.getName().substring(
                        0,
                        file.getName().length() - ".class".length()
                );

                String className =
                        packageName + "." + simpleName;

                //ignore compiled classes filenames
                if (simpleName.contains("$")) {
                    continue;
                }

                processClass(className, classLoader);
            }
        }


    }

    private void processClass(
            String className,
            ClassLoader classLoader
    ) {
        try {
            Class<?> clazz = Class.forName(
                    className,
                    false,
                    classLoader
            );

            if (ComponentDetector.isComponent(clazz)) {
                registry.register(clazz);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Could not load class: " + className,
                    e
            );
        }

    }
}
