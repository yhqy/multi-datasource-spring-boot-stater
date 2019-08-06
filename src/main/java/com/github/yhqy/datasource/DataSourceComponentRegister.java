package com.github.yhqy.datasource;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xchen
 * @date 2019/8/6 11:05
 */
public class DataSourceComponentRegister implements ImportBeanDefinitionRegistrar {

    private final static String DEFAULT_BASE_PACKAGE = "com.github.yhqy.datasource";
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner beanDefinitionScanner = new ClassPathBeanDefinitionScanner(registry);
        beanDefinitionScanner.scan(DEFAULT_BASE_PACKAGE);
    }
}
