package com.sprint.common.converter;

import com.sprint.common.converter.exception.ConversionException;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 转换器
 *
 * @author hongfeng.li
 * @version 1.0
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
     * @param <V>    v
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
     * @param <V>   v
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
     * @param <S1> s1
     * @param <V1> v1
     * @return 转换后转换器
     */
    default <S1, V1> Converter<S1, V1> enforce() {
        return (s1) -> enforce(convert(enforce(s1)));
    }

    /**
     * 转换之后转换
     *
     * @param <S1> s1
     * @param sc1  sc1
     * @param <V1> v1
     * @param vc1  vc1
     * @return 转换后转换器
     */
    default <S1, V1> Converter<S1, V1> enforce(Class<S1> sc1, Class<V1> vc1) {
        return (s1) -> enforce(convert(enforce(s1)));
    }

    /**
     * 当转换发生异常
     *
     * @param handler handler
     * @return Converter
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
     * @param supplier supplier
     * @return Converter
     */
    default Converter<S, T> onErrorGet(Supplier<? extends T> supplier) {
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
     * @param consumer consumer
     * @return Converter
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
     * @return Converter
     */
    default Converter<S, T> ignoreError() {
        return (s) -> {
            try {
                return convert(s);
            } catch (Throwable ignored) {
            }
            return null;
        };
    }

    /**
     * 当原值为空时获取值
     *
     * @param supplier supplier
     * @return Converter
     */
    default Converter<S, T> onNullGet(Supplier<? extends T> supplier) {
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
     * 环绕处理
     *
     * @param handler 环绕处理器
     * @return target
     */
    default Converter<S, T> around(AroundHandler<S, T> handler) {
        return (s) -> {
            S s1 = handler.before(s);
            T t = null;
            try {
                t = convert(s1);
            } finally {
                t = handler.after(s1, t);
            }
            return t;
        };
    }

    /**
     * 转为function
     *
     * @return func
     */
    default Function<S, T> asfunc() {
        return this::convert;
    }

    /**
     * 返回自身
     *
     * @param <T> t
     * @return 自身转换器
     */
    static <T> Converter<T, T> identity() {
        return t -> t;
    }

    /**
     * 强制转换
     *
     * @param <S> s
     * @param <T> t
     * @return 自身转换器
     */
    static <S, T> Converter<S, T> enforcer() {
        return Converter::enforce;
    }

    /**
     * 强制转换
     *
     * @param s   s
     * @param <S> S
     * @param <T> T
     * @return t
     */
    static <S, T> T enforce(S s) {
        return (T) s;
    }
}
