package com.sprint.common.converter;

import com.sprint.common.converter.exception.ConversionException;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title Converter
 * @desc 转换器
 * @since 2021年02月05日
 */
@FunctionalInterface
public interface Converter<S, T> {

    /**
     * 转换
     *
     * @param source 源
     * @return target T
     * @throws ConversionException 转换异常
     */
    T convert(S source) throws ConversionException;

    /**
     * 转换之前转换
     *
     * @param before 转换前转换器
     * @return 转换前转换器
     */
    default <V> Converter<V, T> compose(Converter<? super V, ? extends S> before) {
        Objects.requireNonNull(before);
        return (V v) -> convert(before.convert(v));
    }

    /**
     * 转换之后转换
     * 
     * @param after 转换后转换器
     * @return 转换后转换器
     */
    default <V> Converter<S, V> andThen(Converter<? super T, ? extends V> after) {
        Objects.requireNonNull(after);
        return (s) -> after.convert(convert(s));
    }

    /**
     * 转换之后转换
     *
     * @return 转换后转换器
     */
    default <S1, V1> Converter<S1, V1> enforce() {
        return (s1) -> (V1) convert((S) s1);
    }

    /**
     * 当转换发生异常
     *
     * @param handler
     * @return
     */
    default Converter<S, T> onError(ErrorHandler<S, T> handler) {
        Objects.requireNonNull(handler);
        return (s) -> {
            try {
                return convert(s);
            } catch (Throwable ex) {
                return handler.handle(ex, s);
            }
        };
    }

    /**
     * 当转换发生异常获取值
     *
     * @param supplier
     * @return
     */
    default Converter<S, T> onErrorGet(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return (s) -> {
            try {
                return convert(s);
            } catch (Throwable ex) {
                return supplier.get();
            }
        };
    }

    /**
     * 当转换发生异常获取值
     *
     * @return
     */
    default Converter<S, T> ignoreError(Consumer<Throwable> consumer) {
        return (s) -> {
            try {
                return convert(s);
            } catch (Throwable ex) {
                if (consumer != null) {
                    consumer.accept(ex);
                }
            }
            return null;
        };
    }

    /**
     * 当转换发生异常获取值
     *
     * @return
     */
    default Converter<S, T> ignoreError() {
        return (s) -> {
            try {
                return convert(s);
            } catch (Throwable ex) {
            }
            return null;
        };
    }

    /**
     * 当转换发生异常获取值
     *
     * @param supplier
     * @return
     */
    default Converter<S, T> onNullGet(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return (s) -> {
            if (s == null) {
                return supplier.get();
            } else {
                T res = convert(s);
                if (res == null) {
                    return supplier.get();
                }
                return res;
            }
        };
    }

    /**
     * 返回自身
     * 
     * @return 自身转换器
     */
    static <T> Converter<T, T> identity() {
        return t -> t;
    }

    /**
     * 强制转换
     *
     * @return 自身转换器
     */
    static <S, T> Converter<S, T> enforcer() {
        return s -> (T) s;
    }
}
