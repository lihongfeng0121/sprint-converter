package com.sprint.common.converter;

import com.sprint.common.converter.util.Types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title ConverterContext
 * @desc 转换上下文
 * @since 2021年02月05日
 */
public class ConverterContext {
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);
    private static final String SOURCE_BEAN_TYPE_KEY = "sourceBeanTypeKey";
    private static final String TARGET_BEAN_TYPE_KEY = "targetBeanTypeKey";
    private static final String SOURCE_TYPE_KEY = "sourceTypeKey";
    private static final String TARGET_TYPE_KEY = "targetTypeKey";

    public static Type getSourceBeanType() {
        return (Type) THREAD_LOCAL.get().get(SOURCE_BEAN_TYPE_KEY);
    }

    public static Type getTargetBeanType() {
        return (Type) THREAD_LOCAL.get().get(TARGET_BEAN_TYPE_KEY);
    }

    public static Class<?> getSourceClass() {
        return Types.extractClass(getSourceType(), getSourceBeanType());
    }

    public static Class<?> getTargetClass() {
        return Types.extractClass(getTargetType(), getTargetBeanType());
    }

    public static Type getSourceType() {
        return (Type) THREAD_LOCAL.get().get(SOURCE_TYPE_KEY);
    }

    public static Type getTargetType() {
        return (Type) THREAD_LOCAL.get().get(TARGET_TYPE_KEY);
    }

    public static void initContext(Type sourceBeanType, Type sourceType, Type targetBeanType, Type targetType) {
        THREAD_LOCAL.get().put(SOURCE_BEAN_TYPE_KEY, sourceBeanType);
        THREAD_LOCAL.get().put(TARGET_BEAN_TYPE_KEY, targetBeanType);
        THREAD_LOCAL.get().put(SOURCE_TYPE_KEY, sourceType);
        THREAD_LOCAL.get().put(TARGET_TYPE_KEY, targetType);
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

    /**
     * 设置上下文
     *
     * @return
     */
    public static <S, T> Converter<S, T> whitContext(Converter<S, T> converter, Type sourceType, Type targetType) {
        Objects.requireNonNull(converter);
        Objects.requireNonNull(sourceType);
        Objects.requireNonNull(targetType);
        return (source) -> {
            try {
                ConverterContext.initContext(null, sourceType, null, targetType);
                return converter.convert(source);
            } finally {
                ConverterContext.clear();
            }
        };
    }

    /**
     * 设置上下文
     *
     * @return
     */
    public static <S, T> Converter<S, T> whitContext(Converter<S, T> converter, Type sourceBeanType, Type sourceType,
            Type targetBeanType, Type targetType) {
        Objects.requireNonNull(converter);
        Objects.requireNonNull(sourceType);
        Objects.requireNonNull(targetType);
        return (source) -> {
            try {
                ConverterContext.initContext(sourceBeanType, sourceType, targetBeanType, targetType);
                return converter.convert(source);
            } finally {
                ConverterContext.clear();
            }
        };
    }
}
