package com.spring.annotation;

import com.spring.context.AnnotationConfigApplicationContext;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @author rkc
 * @date 2021/3/12 17:54
 */
public class ClassPathBeanDefinitionScanner {

    /**
     * 根据传入的配置类（被@ComponentScan标注的配置类）
     *
     * @param configClass 被@ComponentScan标注的类字节码
     * @return 对应路径下所有的字节码
     */
    public Set<Class<?>> scan(Class<?> configClass) {
        Set<Class<?>> classSet = new HashSet<>();
        //扫描，得到字节码
        if (!configClass.isAnnotationPresent(ComponentScan.class)) {
            throw new RuntimeException("没有配置指定扫描包路径");
        }
        ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
        if (componentScan.value().isEmpty()) {
            throw new RuntimeException("没有指定包路径！");
        }
        doScan(classSet, componentScan.value().replace(".", "/"));
        return classSet;
    }

    private void doScan(Set<Class<?>> classSet, String path) {
        ClassLoader classLoader = ClassPathBeanDefinitionScanner.class.getClassLoader();
        URL url = classLoader.getResource(path);
        File directory = new File(Objects.requireNonNull(url).getFile());
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                //递归进行读取
                doScan(classSet, path + "/" + file.getName());
            } else {
                String absolutePath = file.getAbsolutePath();
                absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + ("classes".length() + 1), absolutePath.indexOf(".class"))
                        .replace("\\", ".");
                try {
                    Class<?> clazz = classLoader.loadClass(absolutePath);
                    if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class)
                            || clazz.isAnnotationPresent(Repository.class) || clazz.isAnnotationPresent(Controller.class)) {
                        classSet.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
