package com.sprint.common.converter.conversion.nested.bean.introspection;

import com.sprint.common.converter.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 属性
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class PropertyAccess implements Cloneable {

    private static final Logger logger = LoggerFactory.getLogger(PropertyAccess.class);

    private final Class<?> beanClass;

    private final String name;
    private final Type type;
    private final Method readMethod;
    private final Method writeMethod;
    private final Field field;
    private final Map<Class<?>, Annotation> annotations;

    public PropertyAccess(String name, Class<?> beanClass, Method readMethod, Method writeMethod) {
        this.name = name;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.beanClass = beanClass;
        this.field = getBeanPropertyField(name, beanClass);
        this.type = getBeanPropertyType(name, beanClass, readMethod, writeMethod);
        this.annotations = Collections.unmodifiableMap(getBeanPropertyAnnotations(name, beanClass, readMethod, writeMethod));
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Class<?> extractClass() {
        return Types.extractClass(type, beanClass);
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public boolean isArray() {
        return Types.isArray(type);
    }

    public boolean isAccessible() {
        return getField() != null && getField().isAccessible();
    }

    public <T> void setValue(Object instance, T value) {
        if (writeMethod != null) {
            doInvoke(instance, writeMethod, value);
        } else if (field != null) {
            doSet(instance, field, value);
        }
    }

    public boolean isReadAccessible() {
        return readMethod != null || isAccessible();
    }

    public boolean isWriteAccessible() {
        return writeMethod != null || isAccessible();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Object instance) {
        if (readMethod != null) {
            return (T) doInvoke(instance, readMethod);
        } else if (field != null) {
            return (T) doGet(instance, field);
        } else {
            return null;
        }
    }

    public Field getField() {
        return field;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }

    public Map<Class<?>, Annotation> getAnnotations() {
        return annotations;
    }

    public <T extends Annotation> T getAnnotation(Class<? extends Annotation> annotationType) {
        return (T) annotations.get(annotationType);
    }

    private Object doInvoke(Object instance, Method method, Object... args) {
        try {
            if (method.isAccessible()) {
                return method.invoke(instance, args);
            } else {
                method.setAccessible(true);
                return method.invoke(instance, args);
            }
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            logger.debug("doInvoke error", ignored);
        }
        return null;
    }

    private Object doGet(Object instance, Field field) {
        try {
            if (field.isAccessible()) {
                return field.get(instance);
            } else {
                field.setAccessible(true);
                return field.get(instance);
            }
        } catch (IllegalAccessException ignored) {
            logger.debug("doGet error", ignored);
        }
        return null;
    }

    private void doSet(Object instance, Field field, Object value) {
        try {
            if (field.isAccessible()) {
                field.set(instance, value);
            } else {
                field.setAccessible(true);
                field.set(instance, value);
            }
        } catch (IllegalAccessException ignored) {
            logger.debug("doSet error", ignored);
        }
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ignored) {
            logger.debug("clone error", ignored);
        }
        return null;
    }


    public static Field getBeanPropertyField(String name, Class<?> beanClass) {
        try {
            return beanClass.getField(name);
        } catch (NoSuchFieldException e) {
            logger.debug("PropertyAccess#doGetBeanPropertyField({}.{})not exit!", beanClass, name);
        }
        return null;
    }

    public static Type getBeanPropertyType(String name, Class<?> beanClass, Method readMethod, Method writeMethod) {
        if (readMethod != null) {
            return readMethod.getGenericReturnType();
        } else if (writeMethod != null) {
            return Optional.of(writeMethod.getGenericParameterTypes()).map(array -> array[0]).orElse(null);
        } else {
            try {
                return Types.getDeclaredField(beanClass, name).getGenericType();
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("property access name:{" + name + " not exit!}");
            }
        }
    }

    public static Map<Class<?>, Annotation> getBeanPropertyAnnotations(String name, Class<?> beanClass, Method readMethod,
                                                                       Method writeMethod) {
        Map<Class<?>, Annotation> annotations = new HashMap<>();
        if (readMethod != null) {
            annotations.putAll(Arrays.stream(readMethod.getAnnotations())
                    .collect(Collectors.toMap(Annotation::annotationType, Function.identity())));
        }

        if (writeMethod != null) {
            annotations.putAll(Arrays.stream(writeMethod.getAnnotations())
                    .collect(Collectors.toMap(Annotation::annotationType, Function.identity())));
        }

        try {
            Field field = Types.getDeclaredField(beanClass, name);
            annotations.putAll(Arrays.stream(field.getAnnotations())
                    .collect(Collectors.toMap(Annotation::annotationType, Function.identity())));
        } catch (NoSuchFieldException ignored) {
        }

        return annotations;
    }
}
