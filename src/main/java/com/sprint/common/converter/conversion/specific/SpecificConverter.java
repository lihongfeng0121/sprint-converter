package com.sprint.common.converter.conversion.specific;

import com.sprint.common.converter.util.Types;
import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng-li
 * @version 1.0
 * @title Converter
 * @desc 转换器
 * @date 2019年12月25日
 */
public interface SpecificConverter<S, T> {

    /**
     * 是否支持
     *
     * @param sourceType
     * @param targetType
     * @return
     */
    default boolean support(Class<?> sourceType, Class<?> targetType) {
        return getSourceClass().isAssignableFrom(sourceType) && targetType.isAssignableFrom(getTargetClass());
    }

    /**
     * 源类型
     *
     * @return
     */
    default Class<S> getSourceClass() {
        return (Class<S>) Types.getInterfaceSuperclass(getClass(), SpecificConverter.class, 0);
    }

    /**
     * 目标类型
     *
     * @return
     */
    default Class<T> getTargetClass() {
        return (Class<T>) Types.getInterfaceSuperclass(getClass(), SpecificConverter.class, 1);
    }

    /**
     * 转换
     *
     * @param source 源
     * @return target T
     * @throws ConversionException 转换异常
     */
    T convert(S source) throws ConversionException;
}
