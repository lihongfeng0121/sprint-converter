package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.conversion.nested.bean.introspection.CachedIntrospectionResults;
import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;

import java.beans.Transient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongfeng.li
 * @since 2023/3/22
 */
public final class VirtualBean<T> {

    private final Map<String, Object> holder = new HashMap<>();

    private final CachedIntrospectionResults introspectionResults;

    private final Class<T> type;

    public VirtualBean(Class<T> type) {
        Assert.isTrue(type.isInterface(), "type not interface");
        this.type = type;
        this.introspectionResults = CachedIntrospectionResults.forClass(type);
    }

    public List<String> getProperties() {
        List<String> names = new ArrayList<>();
        for (PropertyAccess propertyAccess : introspectionResults.getPropertyAccesses()) {
            names.add(propertyAccess.getName());
        }
        return names;
    }

    public T getObject() {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new InterfaceBeanHandler());
    }

    public void setProperty(String name, Object value) {
        PropertyAccess propertyAccess = introspectionResults.getPropertyAccess(name);
        if (propertyAccess == null) {
            return;
        }
        Object targetValue = AnyConverter.convert(value, propertyAccess.extractClass());
        holder.put(name, targetValue);
    }

    public Object getProperty(String name) {
        return holder.get(name);
    }

    class InterfaceBeanHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            Parameter[] parameters = method.getParameters();
            Transient annotation = method.getAnnotation(Transient.class);
            Class<?> returnType = method.getReturnType();
            if (name.startsWith("get") && parameters.length == 0 && annotation == null && returnType != Void.class) {
                String p = Miscs.lowerFirst(name.replace("get", ""));
                if (!Miscs.isBlank(p)) {
                    return getProperty(p);
                }
            }

            if (name.startsWith("set") && parameters.length == 1 && annotation == null) {
                String p = Miscs.lowerFirst(name.replace("set", ""));
                if (!Miscs.isBlank(p)) {
                    setProperty(p, parameters[0]);
                    return null;
                }
            }
            if ("equals".equals(name)) {
                return (proxy == args[0]);
            } else if ("hashCode".equals(name)) {
                return hashCode();
            } else if ("toString".equals(name)) {
                return toString();
            } else {
                throw new IllegalArgumentException("Unknown method: " + method);
            }
        }
    }
}
