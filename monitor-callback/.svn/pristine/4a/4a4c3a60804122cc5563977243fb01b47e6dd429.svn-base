package com.soholy.cb.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring容器对象
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext ctx;

    private ApplicationContextProvider() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static <T> T getBean(Class<T> aClass) {
        return ctx.getBean(aClass);
    }

    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }
}