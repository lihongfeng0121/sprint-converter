package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.exception.ConversionException;

import java.lang.reflect.Type;

/**
 * bean属性转换器
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public interface NestedConverter {

    /**
     * 排序
     *
     * @return 序号
     */
    default int sort() {
        return 0;
    }

    /**
     * 是否可放入缓存
     *
     * @return
     */
    default boolean specific() {
        return false;
    }

    /**
     * 是否支持
     *
     * @param sourceClass sourceClass
     * @param targetClass targetClass
     * @return 是否支持
     */
    boolean support(Class<?> sourceClass, Class<?> targetClass);

    /**
     * 转化
     *
     * @param sourceValue     原值
     * @param targetBeanType  bean类型
     * @param targetFiledType 属性类型
     * @return 目标值
     * @throws ConversionException e
     */
    Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType) throws ConversionException;
}
