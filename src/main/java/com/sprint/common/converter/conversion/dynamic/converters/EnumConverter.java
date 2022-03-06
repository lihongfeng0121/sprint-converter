package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title EnumConverter
 * @desc 枚举转换
 * @since 2021年02月05日
 */
public class EnumConverter implements DynamicConverterLoader {

    public static class StringToEnum implements DynamicConverter<Enum> {

        @Override
        public int sort() {
            return 1 << 10;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return (String.class.isAssignableFrom(sourceType) || Enum.class.isAssignableFrom(sourceType))
                    && Enum.class.isAssignableFrom(targetType);
        }

        @Override
        public Enum<?> convert(Object source, Class<Enum> targetType) throws ConversionException {
            if (source == null) {
                return null;
            }
            if (targetType != null) {
                return Enum.valueOf(targetType, String.valueOf(source));
            }
            return null;
        }
    }
}
