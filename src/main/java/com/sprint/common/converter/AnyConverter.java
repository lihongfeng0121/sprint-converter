package com.sprint.common.converter;

import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.BeansException;
import com.sprint.common.converter.util.Assert;
import com.sprint.common.converter.util.TypeDescriptor;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * 万物互转
 *
 * @author hongfeng.li
 * @since 2021/1/21
 */
public final class AnyConverter {

    private AnyConverter() {
    }

    /**
     * 获取转换器
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param <S>        源类范型
     * @param <T>        目标类范型
     * @return target
     */
    public static <S, T> Converter<S, T> converter(Class<S> sourceType, Class<T> targetType) {
        return NestedConverters.getConverter(sourceType, targetType).enforce(sourceType, targetType);
    }

    /**
     * 获取转换器
     *
     * @param typePath 类型path
     * @param <S>      源类范型
     * @param <T>      目标类范型
     * @return target
     */
    public static <S, T> Converter<S, T> converter(Type... typePath) {
        Assert.isTrue(typePath.length > 0, "types should ge 1");
        Converter<?, ?> converter = Converter.identity();
        for (int i = 0, length = typePath.length; i < length - 1; i++) {
            Converter<Object, Object> c = NestedConverters.getConverter(TypeDescriptor.of(typePath[i]), TypeDescriptor.of(typePath[i + 1]));
            converter = converter.andThen(c);
        }
        return converter.enforce();
    }


    /**
     * 获取转换器
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param <S>        源类范型
     * @param <T>        目标类范型
     * @return target
     */
    public static <S, T> Converter<S, T> converter(TypeReference<S> sourceType, TypeReference<T> targetType) {
        return NestedConverters.getConverter(TypeDescriptor.of(sourceType.getType()), TypeDescriptor.of(targetType.getType()));
    }

    /**
     * 对象转换
     *
     * @param source 源对象
     * @param first  first
     * @param types  目标类
     * @param <S>    源类范型
     * @param <T>    目标类范型
     * @return 返回目标类对象
     * @throws BeansException bean exception
     */
    private static <S, T> T doConvert(S source, Type first, Type... types) {
        Object target = NestedConverters.convert(source, TypeDescriptor.of(first));
        for (Type type : types) {
            target = NestedConverters.convert(target, TypeDescriptor.of(type));
        }
        return Converter.enforce(target);
    }

    /**
     * 对象转换
     *
     * @param source 源对象
     * @param type   目标类
     * @param <T>    目标类范型
     * @return 返回目标类对象
     * @throws BeansException bean exception
     */
    public static <T> T convert(Object source, Class<T> type) {
        return doConvert(source, type);
    }


    /**
     * 对象转换
     *
     * @param source       源对象
     * @param type         目标类
     * @param defaultValue 默认值
     * @param <T>          目标类范型
     * @return 返回目标类对象
     * @throws BeansException bean exception
     */
    public static <T> T convert(Object source, Class<T> type, T defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        return doConvert(source, type);
    }


    /**
     * 对象转换
     *
     * @param source       源对象
     * @param type         目标类
     * @param defaultValue 默认值
     * @param <T>          目标类范型
     * @return 返回目标类对象
     * @throws BeansException bean exception
     */
    public static <T> T convert(Object source, Class<T> type, Supplier<T> defaultValue) {
        if (source == null) {
            return defaultValue.get();
        }
        return doConvert(source, type);
    }

    /**
     * 对象转换
     *
     * @param source   源对象
     * @param type     type
     * @param typePath 目标类
     * @param <T>      目标类范型
     * @return 返回目标类对象
     * @throws BeansException bean exception
     */
    public static <T> T convert(Object source, Type type, Type... typePath) {
        return doConvert(source, type, typePath);
    }

    /**
     * 对象转换
     *
     * @param source 源对象
     * @param clazz  目标类
     * @param <T>    目标类范型
     * @return 返回目标类对象
     * @throws BeansException bean exception
     */
    public static <T> T convert(Object source, TypeReference<T> clazz) throws BeansException {
        return doConvert(source, clazz.getType());
    }


    /**
     * 对象转换
     *
     * @param source       源对象
     * @param clazz        目标类
     * @param defaultValue 默认值
     * @param <T>          目标类范型
     * @return 返回目标类对象
     * @throws BeansException bean exception
     */
    public static <T> T convert(Object source, TypeReference<T> clazz, T defaultValue) throws BeansException {
        if (source == null) {
            return defaultValue;
        }
        return doConvert(source, clazz.getType());
    }

    /**
     * 对象转换
     *
     * @param source       源对象
     * @param clazz        目标类
     * @param defaultValue 默认值
     * @param <T>          目标类范型
     * @return 返回目标类对象
     * @throws BeansException bean exception
     */
    public static <T> T convert(Object source, TypeReference<T> clazz, Supplier<T> defaultValue) throws BeansException {
        if (source == null) {
            return defaultValue.get();
        }
        return doConvert(source, clazz.getType());
    }
}
