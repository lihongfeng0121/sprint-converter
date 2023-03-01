package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.TypeDescriptor;

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
     * 是否支持
     *
     * @param sourceType sourceType
     * @param targetType targetType
     * @return 是否支持
     */
    boolean support(TypeDescriptor sourceType, TypeDescriptor targetType);


    /**
     * 是否支持源对象
     *
     * @param sourceValue 值
     * @return 对象
     */
    default boolean preCheckSourceVal(Object sourceValue) {
        return true;
    }

    /**
     * 转化
     *
     * @param sourceValue          原值
     * @param targetTypeDescriptor 目标值类型
     * @return 目标值
     * @throws ConversionException e
     */
    Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor) throws ConversionException;
}
