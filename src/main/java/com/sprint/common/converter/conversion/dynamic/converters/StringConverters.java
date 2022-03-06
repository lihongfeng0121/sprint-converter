package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title StringConverters
 * @desc 字符串转换器
 * @since 2021年02月05日
 */
public class StringConverters implements DynamicConverterLoader {

    public static class StringConverter implements DynamicConverter<String> {

        @Override
        public int sort() {
            return 1 << 4;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return String.class.equals(targetType);
        }

        @Override
        public String convert(Object source, Class<String> targetType) throws ConversionException {
            return source == null ? null : String.valueOf(source);
        }
    }
}
