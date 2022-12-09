package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.Converter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * 枚举转换
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class EnumConverters implements DynamicConverterLoader {

    public static class StringToEnum implements DynamicConverter<Enum<?>> {

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
        public Enum<?> convert(Object source, Class<Enum<?>> targetType) throws ConversionException {
            if (source == null) {
                return null;
            }
            if (targetType != null) {
                return Enum.valueOf(Converter.doEnforce(targetType), String.valueOf(source));
            }
            return null;
        }
    }
}
