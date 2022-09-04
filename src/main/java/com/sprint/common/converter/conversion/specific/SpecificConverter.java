package com.sprint.common.converter.conversion.specific;

import com.sprint.common.converter.Converter;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Types;

/**
 * 转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public interface SpecificConverter<S, T> {

    /**
     * 是否支持
     *
     * @param sourceType sourceType
     * @param targetType targetType
     * @return support
     */
    default boolean support(Class<?> sourceType, Class<?> targetType) {
        return getSourceClass().isAssignableFrom(sourceType) && targetType.isAssignableFrom(getTargetClass());
    }

    /**
     * 源类型
     *
     * @return sourceClass
     */
    default Class<S> getSourceClass() {
        return Converter.doEnforce(Types.getInterfaceSuperclass(getClass(), SpecificConverter.class, 0));
    }

    /**
     * 目标类型
     *
     * @return targetClass
     */
    default Class<T> getTargetClass() {
        return Converter.doEnforce(Types.getInterfaceSuperclass(getClass(), SpecificConverter.class, 1));
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
