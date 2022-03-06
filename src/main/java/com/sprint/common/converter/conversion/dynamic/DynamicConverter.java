package com.sprint.common.converter.conversion.dynamic;

import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title DynamicConverter
 * @desc 动态转换器
 * @since 2021年02月05日
 */
public interface DynamicConverter<T> {

    /**
     * 加载顺序
     *
     * @return
     */
    int sort();

    /**
     * 是否支持
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return
     */
    boolean support(Class<?> sourceType, Class<?> targetType);

    /**
     * 转换
     *
     * @param source
     * @param targetType
     * @return
     */
    T convert(Object source, Class<T> targetType) throws ConversionException;
}
