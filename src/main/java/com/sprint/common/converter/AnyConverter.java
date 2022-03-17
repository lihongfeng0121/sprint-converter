package com.sprint.common.converter;

import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.bean.BeansException;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.ConversionExceptionWrapper;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Assert;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Type;

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
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceType, Class<T> targetType) {
        Converter<S, T> converter = NestedConverters.getConverter(sourceType, targetType);
        return converter.onError((ex, s) -> {
            if (BaseConverters.isSupport(sourceType, targetType)) {
                return BaseConverters.convert(s, targetType);
            } else {
                throw new NotSupportConvertException(sourceType, targetType, ex);
            }
        });
    }

    /**
     * 获取转换器
     *
     * @param typePath 类型path
     * @param <S>      源类范型
     * @param <T>      目标类范型
     * @return target
     */
    public static <S, T> Converter<S, T> getConverter(Type... typePath) {
        Assert.isTrue(typePath.length > 0, "types should ge 1");
        Converter<?, ?> converter = Converter.identity();
        for (int i = 0, length = typePath.length; i < length - 1; i++) {
            Class<?> sourceType = Types.extractClass(typePath[i]);
            Class<?> targetType = Types.extractClass(typePath[i + 1]);
            Converter<Object, Object> c = NestedConverters.getConverter(sourceType, null, typePath[i + 1]);
            converter = converter.andThen(c.onError((ex, s) -> {
                if (BaseConverters.isSupport(sourceType, targetType)) {
                    return BaseConverters.convert(s, targetType);
                } else {
                    throw new NotSupportConvertException(sourceType, targetType, ex);
                }
            }));
        }
        return converter.enforce();
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
    public static <S, T> T doConvert(S source, Type first, Type... types) {
        try {
            Object target = NestedConverters.convert(source, null, first, (ex, s) -> {
                Class<?> sourceType = source.getClass();
                Class<?> targetType = Types.extractClass(first);
                if (BaseConverters.isSupport(sourceType, targetType)) {
                    return BaseConverters.convert(s, targetType);
                } else {
                    throw new NotSupportConvertException(sourceType, targetType, ex);
                }
            });
            for (Type type : types) {
                Class<?> sourceType = target.getClass();
                target = NestedConverters.convert(target, null, type, (ex, s) -> {
                    Class<?> targetType = Types.extractClass(type);
                    if (BaseConverters.isSupport(sourceType, targetType)) {
                        return BaseConverters.convert(s, targetType);
                    } else {
                        throw new NotSupportConvertException(sourceType, targetType, ex);
                    }
                });
            }
            return (T) target;
        } catch (ConversionException e) {
            throw ConversionExceptionWrapper.wrapper(e);
        }
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
}
