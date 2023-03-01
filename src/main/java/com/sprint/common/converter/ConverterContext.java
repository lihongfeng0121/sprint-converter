package com.sprint.common.converter;

import com.sprint.common.converter.util.TypeDescriptor;

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

    public static Class<?> getSourceClass() {
        return Optional.ofNullable(THREAD_LOCAL.get()).map(context -> context.getSourceType().getActualClass()).orElse(null);
    }

    public static Class<?> getTargetClass() {
        return Optional.ofNullable(THREAD_LOCAL.get()).map(context -> context.getTargetType().getActualClass()).orElse(null);
    }

    public static void initContext(Context<?, ?> context) {
        THREAD_LOCAL.set(context);
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

    public static <S, T> Converter<S, T> whitContext(Converter<S, T> converter, Class<S> sourceType, Class<T> targetType) {
        Objects.requireNonNull(converter);
        Objects.requireNonNull(sourceType);
        Objects.requireNonNull(targetType);
        return converter.around(new Context<>(sourceType, targetType));
    }

    public static <S, T> Converter<S, T> whitContext(Converter<S, T> converter, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Objects.requireNonNull(converter);
        Objects.requireNonNull(sourceType);
        Objects.requireNonNull(targetType);
        return converter.around(new Context<>(sourceType, targetType));
    }


    public static class Context<S, T> implements AroundHandler<S, T> {
        private final TypeDescriptor sourceType;
        private final TypeDescriptor targetType;

        public Context(TypeDescriptor sourceType, TypeDescriptor targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public Context(Class<S> sourceType, Class<T> targetType) {
            this(TypeDescriptor.of(sourceType), TypeDescriptor.of(targetType));
        }

        public TypeDescriptor getSourceType() {
            return sourceType;
        }

        public TypeDescriptor getTargetType() {
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
