package com.sprint.common.converter.util;

import com.sprint.common.converter.conversion.nested.bean.introspection.CachedIntrospectionResults;
import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;

import java.beans.Transient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.*;

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

    public List<PropertyAccess> getProperties() {
        return new ArrayList<>(Arrays.asList(introspectionResults.getPropertyAccesses()));
    }

    public T getObject() {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new InterfaceBeanHandler());
    }

    public void setProperty(String name, Object value) {
        PropertyAccess propertyAccess = introspectionResults.getPropertyAccess(name);
        if (propertyAccess == null) {
            throw new IllegalArgumentException(String.format("not exit name[%s]", name));
        }
        Class<?> aClass = propertyAccess.extractClass();
        if (!aClass.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("type not match");
        }
        holder.put(name, value);
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
                return VirtualBean.this.equals(args[0]);
            } else if ("hashCode".equals(name)) {
                return VirtualBean.this.hashCode();
            } else if ("toString".equals(name)) {
                return VirtualBean.this.toString();
            } else {
                throw new IllegalArgumentException("Unknown method: " + method);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VirtualBean)) return false;
        VirtualBean<?> that = (VirtualBean<?>) o;
        return Objects.equals(holder, that.holder) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(holder, type);
    }

    @Override
    public String toString() {
        return "$Virtual." + type.getSimpleName() + "{holder=" + holder + '}';
    }
}
