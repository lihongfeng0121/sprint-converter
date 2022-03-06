package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.exception.ConversionException;

import java.lang.reflect.Type;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title PropertyConverter
 * @desc bean属性转换器
 * @since 2021年02月05日
 */
public interface NestedConverter {

    /**
     * 排序
     *
     * @return
     */
    default int sort() {
        return 0;
    }

    /**
     * 是否支持
     *
     * @param sourceValue
     * @param targetClass
     * @return
     */
    boolean support(Object sourceValue, Class<?> sourceClass, Class<?> targetClass);

    /**
     * 是否支持
     *
     * @param sourceClass
     * @param targetClass
     * @return
     */
    default boolean support(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass != null && support(null, sourceClass, targetClass);
    }

    /**
     * 是否支持
     *
     * @param sourceValue
     * @param targetClass
     * @return
     */
    default boolean support(Object sourceValue, Class<?> targetClass) {
        return sourceValue != null && support(sourceValue, sourceValue.getClass(), targetClass);
    }

    /**
     * 转化
     *
     * @param sourceValue 原值
     * @param targetBeanType bean类型
     * @param targetFiledType 属性类型
     *
     * @return 目标值
     */
    Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType) throws ConversionException;
}
