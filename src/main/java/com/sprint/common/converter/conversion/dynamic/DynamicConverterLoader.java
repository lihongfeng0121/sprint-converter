package com.sprint.common.converter.conversion.dynamic;

/**
 * @author hongfeng-li
 * @version 1.0
 * @title ConverterLoader
 * @desc 转换器加载
 * @date 2019年12月25日
 */
public interface DynamicConverterLoader {

    /**
     * 加载转换器
     */
    default void loadConverters() {
        DynamicConverters.loadContainedConverters(getClass());
    }
}
