package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title AssignableConverters
 * @desc 继承属性转换器
 * @since 2021年02月05日
 */
public class AssignableConverters implements DynamicConverterLoader {

    public static class AssignableFrom implements DynamicConverter<Object> {

        @Override
        public int sort() {
            return 1 << 8;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return targetType.isAssignableFrom(sourceType);
        }

        @Override
        public Object convert(Object source, Class<Object> targetType) throws ConversionException {
            return source;
        }
    }
}
