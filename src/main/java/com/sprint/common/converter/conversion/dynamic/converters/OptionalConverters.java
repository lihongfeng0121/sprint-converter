package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.ObjectValue;

import java.util.Optional;

/**
 * @author hongfeng.li
 * @since 2023/2/13
 */
public class OptionalConverters implements DynamicConverterLoader {

    public static class ObjectToOptionalConverter implements DynamicConverter<Optional<?>> {

        @Override
        public int sort() {
            return 1 << 2;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return Optional.class.equals(targetType);
        }

        @Override
        public Optional<?> convert(Object source, Class<Optional<?>> targetType) throws ConversionException {
            return Optional.ofNullable(source);
        }
    }

    public static class OptionalToObjectConverter implements DynamicConverter<Object> {

        @Override
        public int sort() {
            return 1 << 2;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return Optional.class.isAssignableFrom(sourceType) && !Optional.class.isAssignableFrom(targetType);
        }

        @Override
        public Object convert(Object source, Class<Object> targetType) throws ConversionException {
            if (source == null) {
                return null;
            }
            Optional<?> optional = (Optional<?>) source;
            return optional.map(ObjectValue::ofNullable).map(obj -> obj.getValue(targetType)).orElse(null);
        }
    }
}
