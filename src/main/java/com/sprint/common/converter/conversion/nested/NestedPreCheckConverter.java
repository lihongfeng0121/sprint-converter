package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.util.TypeDescriptor;

/**
 * @author hongfeng.li
 * @since 2023/4/2
 */
public interface NestedPreCheckConverter extends NestedConverter {

    /**
     * 是否支持源对象
     *
     * @param sourceValue 值
     * @param sourceType  源类型
     * @param targetType  目标类型
     * @return 对象
     */
    boolean preCheckSourceVal(Object sourceValue, TypeDescriptor sourceType, TypeDescriptor targetType);
}
