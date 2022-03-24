package com.sprint.common.converter;

import com.sprint.common.converter.conversion.nested.bean.Beans;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.ConversionExceptionWrapper;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Types;

/**
 * 对象转换器
 *
 * @author hongfeng.li
 * @since 2022/3/24
 */
public class BeanConverter {

    /**
     * 获取转换器
     *
     * @param sourceClass 源类型
     * @param targetClass 目标类型
     * @param <S>         s
     * @param <T>         t
     * @return Converter
     */
    public static <S, T> Converter<S, T> getShallowConverter(Class<S> sourceClass, Class<T> targetClass, String... ignoreProperties) {
        return getConverter(sourceClass, targetClass, false, ignoreProperties);
    }

    /**
     * 获取转换器
     *
     * @param sourceClass 源类型
     * @param targetClass 目标类型
     * @param <S>         s
     * @param <T>         t
     * @return Converter
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Class<T> targetClass, String... ignoreProperties) {
        return getConverter(sourceClass, targetClass, true, ignoreProperties);
    }


    /**
     * 获取转换器
     *
     * @param sourceClass 源类型
     * @param targetClass 目标类型
     * @param convert     是否进行属性转换
     * @param <S>         s
     * @param <T>         t
     * @return Converter
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Class<T> targetClass, boolean convert, String... ignoreProperties) {
        return (source) -> {
            if (source == null) {
                return null;
            }
            Class<?> sourceType = source.getClass();
            Class<?> finalTargetClass = Types.isObjectType(targetClass) ? sourceType : targetClass;
            if (!Types.isMap(sourceClass) || !Types.isBean(sourceClass) || !Types.isMap(finalTargetClass) || !Types.isBean(finalTargetClass)) {
                throw new NotSupportConvertException(sourceClass, targetClass);
            }
            if (sourceClass.isAssignableFrom(sourceType)) {
                throw new ConversionException("source type[" + sourceType + "] not from " + sourceClass);
            }
            return (T) Beans.cast(source, finalTargetClass, convert, ignoreProperties);
        };
    }

    /**
     * 转换
     *
     * @param source           源
     * @param targetClass      目标类型
     * @param ignoreProperties 忽略属性
     * @param <T>              目标范型
     * @return target
     */
    public static <T> T convert(Object source, Class<T> targetClass, String... ignoreProperties) {
        if (source == null) {
            return null;
        }
        Class<?> sourceType = source.getClass();
        Class<?> finalTargetClass = Types.isObjectType(targetClass) ? sourceType : targetClass;
        if (!Types.isMap(sourceType) || !Types.isBean(sourceType) || !Types.isMap(finalTargetClass) || !Types.isBean(finalTargetClass)) {
            throw ConversionExceptionWrapper.wrapper(new NotSupportConvertException(sourceType, targetClass));
        }
        return (T) Beans.cast(source, finalTargetClass, ignoreProperties);
    }
}
