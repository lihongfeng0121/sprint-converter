package com.sprint.common.converter.conversion.specific;

/**
 * @author hongfeng-li
 * @version 1.0
 * @title ConverterLoader
 * @desc 转换器加载
 * @date 2019年12月25日
 */
public interface SpecificConverterLoader {

    /**
     * 加载转换器
     */
    default void loadConverters() {
        SpecificConverters.loadContainedConverters(getClass());
    }
}
