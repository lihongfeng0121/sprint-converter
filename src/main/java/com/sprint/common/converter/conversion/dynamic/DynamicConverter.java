package com.sprint.common.converter.conversion.dynamic;

import com.sprint.common.converter.exception.ConversionException;

/**
 * 动态转换器
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public interface DynamicConverter<T> {

    /**
     * 加载顺序
     *
     * @return 序号
     */
    int sort();

    /**
     * 是否支持
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 是否支持
     */
    boolean support(Class<?> sourceType, Class<?> targetType);

    /**
     * 转换
     *
     * @param source     source
     * @param targetType targetType
     * @return target
     * @throws ConversionException e
     */
    T convert(Object source, Class<T> targetType) throws ConversionException;
}
