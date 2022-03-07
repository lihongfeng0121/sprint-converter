package com.sprint.common.converter.conversion.nested;

/**
 * @author hongfeng.li
 * @since 2021/1/25
 */
public interface NestedConverterLoader {

    /**
     * 加载转换器
     */
    default void loadConverters() {
        NestedConverters.loadContainedConverters(getClass());
    }

}
