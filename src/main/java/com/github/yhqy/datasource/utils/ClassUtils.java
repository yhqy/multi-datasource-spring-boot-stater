package com.github.yhqy.datasource.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ClassUtils {

    public static boolean haveAnnotation(Class clazz, Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    public static boolean haveAnnotation(Method method, Class<? extends Annotation> annotation) {
        return method.isAnnotationPresent(annotation);
    }

    public static boolean hasAnnotationOnParam(Method method, Class<? extends Annotation> annotationClass) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return false;
        }
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotationClass.getName().equals(annotation.annotationType().getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Method getMethod(Class clazz, String methodName, Class[] paramTypeArr) {
        try {
            return clazz.getMethod(methodName, paramTypeArr);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
