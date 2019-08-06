package com.github.yhqy.datasource;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.yhqy.datasource.annotation.DataSourceType;
import com.github.yhqy.datasource.annotation.Did;
import com.github.yhqy.datasource.utils.ClassUtils;
import com.github.yhqy.datasource.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Component
@Aspect
@Order(1)
@Slf4j
public class DataSourceAspect {

    private final static String STRING_CLASS_NAME = String.class.getName();
    private static String getDidMethodName;

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Pointcut("target(com.github.yhqy.datasource.DAO)")
    public void aspect() {
    }

    @Before("aspect()")
    public void before(JoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataSourceType dataSourceType = getDataSourceType(method);
        if (DataSourceType.DEFAULT.equals(dataSourceType)) {
            DataSourceHolder.set(null);
            return;
        }
        String dataSourceDid = getDataSourceDid(joinPoint, signature, method);
        DataSourceHolder.set(dataSourceDid);
    }

    @After("aspect()")
    public void after() {
        DataSourceHolder.clear();
    }


    private String getDataSourceDid(JoinPoint joinPoint, MethodSignature signature, Method method) throws InvocationTargetException, IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        Class[] paramTypeArr = signature.getParameterTypes();
        if (null != method) {
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                if (null != parameters[i].getAnnotation(Did.class)) {
                    return getCid(args[i], paramTypeArr[i]);
                }
            }
        }
        return null;
    }

    private String getCid(Object obj, Class paramType) throws InvocationTargetException, IllegalAccessException {
        if (null == obj) {
            return null;
        }
        Object result = null;
        if (ObjectUtil.isBasicType(obj) || STRING_CLASS_NAME.equals(paramType.getName())) {
            result = obj;
        } else if (Map.class.isAssignableFrom(paramType)) {
            Map paramMap = (Map) obj;
            result = MapUtils.getIgnoreCase(paramMap, dataSourceFactory.key());
        } else if (Collection.class.isAssignableFrom(paramType)) {
            Collection collection = (Collection) obj;
            Iterator iterator = collection.iterator();
            if (iterator.hasNext()) {
                Object param = iterator.next();
                if (null != param) {
                    return getCid(param, param.getClass());
                }
            }
        } else {
            Method didGetMethod = ClassUtils.getMethod(paramType, getGetDidMethodName(), new Class[0]);
            if (null != didGetMethod) {
                didGetMethod.setAccessible(true);
                result = didGetMethod.invoke(obj);
            }
        }
        return null == result ? null : String.valueOf(result);
    }

    private DataSourceType getDataSourceType(Method method) {
        return ClassUtils.hasAnnotationOnParam(method, Did.class) ? DataSourceType.ASSIGNATION : DataSourceType.DEFAULT;
    }

    private String getGetDidMethodName() {
        if (null == getDidMethodName) {
            getDidMethodName = StrUtil.genGetter(dataSourceFactory.key());
        }
        return getDidMethodName;
    }

}
