package com.sprint.common.converter.conversion.dynamic;

/**
 * 转换器加载
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public interface DynamicConverterLoader {

    /**
     * 加载转换器
     */
    default void loadConverters() {
        DynamicConverters.loadContainedConverters(getClass());
    }
}
