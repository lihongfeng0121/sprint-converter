package com.sprint.common.converter;

import com.sprint.common.converter.util.Types;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * 转换上下文
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class ConverterContext {

    private static final ThreadLocal<Context<?, ?>> THREAD_LOCAL = new ThreadLocal<>();

    public static Context<?, ?> getContext() {
        return THREAD_LOCAL.get();
    }

    public static Type getSourceBeanType() {
        return Optional.ofNullable(THREAD_LOCAL.get()).map(Context::getSourceBeanType).orElse(null);
    }

    public static Type getTargetBeanType() {
        return Optional.ofNullable(THREAD_LOCAL.get()).map(Context::getTargetBeanType).orElse(null);
    }

    public static Class<?> getSourceClass() {
        return Optional.ofNullable(THREAD_LOCAL.get()).map(context -> Types.extractClass(context.getSourceType(), context.getSourceBeanType())).orElse(null);
    }

    public static Class<?> getTargetClass() {
        return Optional.ofNullable(THREAD_LOCAL.get()).map(context -> Types.extractClass(context.getTargetType(), context.getTargetBeanType())).orElse(null);
    }

    public static Type getSourceType() {
        return Optional.ofNullable(THREAD_LOCAL.get()).map(Context::getSourceType).orElse(null);
    }

    public static Type getTargetType() {
        return Optional.ofNullable(THREAD_LOCAL.get()).map(Context::getTargetType).orElse(null);
    }

    public static void initContext(Context<?, ?> context) {
        THREAD_LOCAL.set(context);
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

    public static <S, T> Converter<S, T> whitContext(Converter<S, T> converter, Type sourceType, Type targetType) {
        Objects.requireNonNull(converter);
        Objects.requireNonNull(sourceType);
        Objects.requireNonNull(targetType);
        return converter.around(new Context<>(sourceType, targetType));
    }

    public static <S, T> Converter<S, T> whitContext(Converter<S, T> converter, Type sourceBeanType, Type sourceType,
                                                     Type targetBeanType, Type targetType) {
        Objects.requireNonNull(converter);
        Objects.requireNonNull(sourceType);
        Objects.requireNonNull(targetType);
        return converter.around(new Context<>(sourceBeanType, sourceType, targetBeanType, targetType));
    }

    public static class Context<S, T> implements AroundHandler<S, T> {

        private final Type sourceBeanType;
        private final Type sourceType;
        private final Type targetBeanType;
        private final Type targetType;

        public Context(Type sourceBeanType, Type sourceType, Type targetBeanType, Type targetType) {
            this.sourceBeanType = sourceBeanType;
            this.sourceType = sourceType;
            this.targetBeanType = targetBeanType;
            this.targetType = targetType;
        }

        public Context(Type sourceType, Type targetType) {
            this(null, sourceType, null, targetType);
        }

        public Type getSourceBeanType() {
            return sourceBeanType;
        }

        public Type getSourceType() {
            return sourceType;
        }

        public Type getTargetBeanType() {
            return targetBeanType;
        }

        public Type getTargetType() {
            return targetType;
        }

        @Override
        public S before(S source) {
            ConverterContext.initContext(this);
            return AroundHandler.super.before(source);
        }

        @Override
        public T after(S source, T target) {
            ConverterContext.clear();
            return AroundHandler.super.after(source, target);
        }
    }
}
