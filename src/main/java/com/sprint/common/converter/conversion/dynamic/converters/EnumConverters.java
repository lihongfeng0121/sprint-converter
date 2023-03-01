package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.Converter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Types;

import java.util.Objects;

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
                return Enum.valueOf(Converter.enforce(targetType), String.valueOf(source));
            }
            return null;
        }
    }

    public static class NumberToEnum implements DynamicConverter<Enum<?>> {

        @Override
        public int sort() {
            return 1 << 10;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return Enum.class.isAssignableFrom(targetType) && ((sourceType.isPrimitive() && Number.class.isAssignableFrom(Types.getPrimitiveWrapTypeClass(sourceType)) || Number.class.isAssignableFrom(sourceType)));
        }

        @Override
        public Enum<?> convert(Object source, Class<Enum<?>> targetType) throws ConversionException {
            if (source == null) {
                return null;
            }
            if (targetType != null) {
                Number number = (Number) source;
                return targetType.getEnumConstants()[number.intValue()];
            }
            return null;
        }
    }


    public static class EnumToInteger implements DynamicConverter<Integer> {

        @Override
        public int sort() {
            return 1 << 10;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return Enum.class.isAssignableFrom(sourceType) && (Objects.equals(targetType, Integer.class) || Objects.equals(targetType, Integer.TYPE));
        }

        @Override
        public Integer convert(Object source, Class<Integer> targetType) throws ConversionException {
            if (source == null) {
                return null;
            }
            Enum enumValue = (Enum) source;
            return enumValue.ordinal();
        }
    }
}
