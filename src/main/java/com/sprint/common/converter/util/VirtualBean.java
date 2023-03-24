package com.sprint.common.converter.util;

import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.bean.introspection.CachedIntrospectionResults;
import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;

import java.beans.Transient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new VirtualInvocationHandler());
    }

    public VirtualBean<T> setProperties(Object bean) {
        for (PropertyAccess propertyAccess : getProperties()) {
            Object property = Beans.getProperty(bean, propertyAccess.getName());
            if (property != null) {
                Object targetVal = NestedConverters.convert(property, TypeDescriptor.of(propertyAccess.extractClass()));
                setProperty(propertyAccess.getName(), targetVal);
            }
        }
        return this;
    }

    public void setProperty(String name, Object value) {
        PropertyAccess propertyAccess = introspectionResults.getPropertyAccess(name);
        if (propertyAccess == null) {
            throw new IllegalArgumentException(String.format("not exit name[%s]", name));
        }
        Class<?> aClass = propertyAccess.extractClass();
        if (!(aClass.isAssignableFrom(value.getClass()) || Objects.equals(Types.getPrimitiveWrapTypeClass(aClass), value.getClass()))) {
            throw new IllegalArgumentException("type not match");
        }
        holder.put(name, value);
    }

    public Object getProperty(String name) {
        return holder.get(name);
    }

    class VirtualInvocationHandler implements InvocationHandler {

        final Object[] NO_ARGS = {};

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (args == null) {
                args = NO_ARGS;
            }
            String name = method.getName();
            if (args.length == 1 && "equals".equals(name)) {
                Object arg = args[0];
                return equals(arg);
            } else if (args.length == 0 && "hashCode".equals(name)) {
                return hashCode();
            } else if (args.length == 0 && "toString".equals(name)) {
                return toString();
            } else {
                Transient annotation = method.getAnnotation(Transient.class);
                Class<?> returnType = method.getReturnType();
                if (name.startsWith("get") && args.length == 0 && annotation == null && returnType != Void.class) {
                    String p = Miscs.lowerFirst(name.replace("get", ""));
                    if (!Miscs.isBlank(p)) {
                        return getProperty(p);
                    }
                }
                if (name.startsWith("set") && args.length == 1 && annotation == null && (returnType == Void.class || returnType == Void.TYPE)) {
                    String p = Miscs.lowerFirst(name.replace("set", ""));
                    if (!Miscs.isBlank(p)) {
                        setProperty(p, args[0]);
                        return null;
                    }
                }
                throw new IllegalArgumentException("Unknown method: " + method);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(holder, type);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Proxy)) {
                return false;
            }
            InvocationHandler h = Proxy.getInvocationHandler(o);
            if (!(h instanceof VirtualBean.VirtualInvocationHandler)) {
                return false;
            }
            VirtualBean<?>.VirtualInvocationHandler that = (VirtualBean<?>.VirtualInvocationHandler) h;
            return Objects.equals(holder, that.getHolder()) && Objects.equals(type, that.getType());
        }

        @Override
        public String toString() {
            return "Virtual$" + type.getSimpleName() + holder;
        }

        Map<String, Object> getHolder() {
            return VirtualBean.this.holder;
        }

        Class<?> getType() {
            return VirtualBean.this.type;
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
        return "VirtualBean{" + "type=" + type + ", holder=" + holder + '}';
    }

    public static <T> VirtualBean<T> of(Class<T> type) {
        return new VirtualBean<>(type);
    }
}
