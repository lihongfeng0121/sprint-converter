package com.sprint.common.converter;

import com.sprint.common.converter.util.TypeDescriptor;

/**
 * @author hongfeng.li
 * @since 2023/4/2
 */
@FunctionalInterface
public interface ConvertMatcher {

    /**
     * 是否支持
     *
     * @param sourceType sourceType
     * @param targetType targetType
     * @return 是否支持
     */
    boolean support(TypeDescriptor sourceType, TypeDescriptor targetType);

    default boolean support(Class<?> sourceType, Class<?> targetType) {
        return support(TypeDescriptor.of(sourceType), TypeDescriptor.of(targetType));
    }
}
