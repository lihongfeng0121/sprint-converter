package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.TypeDescriptor;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author hongfeng.li
 * @since 2023/2/14
 */
public class OptionalConverters implements NestedConverterLoader {

    /**
     * Optional转对象
     */
    public static class Optional2Object implements NestedConverter {

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return Optional.class.isAssignableFrom(sourceType.getActualClass()) && !Optional.class.isAssignableFrom(targetType.getActualClass());
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            Optional<?> optional = (Optional<?>) sourceValue;
            if (optional == null || !optional.isPresent()) {
                return null;
            }
            return optional.map(item -> NestedConverters.convert(item, targetTypeDescriptor)).orElse(null);
        }
    }

    public static class Optional2Optional implements NestedConverter {

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return Optional.class.isAssignableFrom(sourceType.getActualClass()) && Optional.class.isAssignableFrom(targetType.getActualClass());
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            Optional<?> optional = (Optional<?>) sourceValue;
            if (optional == null) {
                return null;
            }
            if (!optional.isPresent()) {
                return Optional.empty();
            }
            Type actualType = targetTypeDescriptor.getGenericsTypes(Optional.class)[0];
            return optional.map(item -> NestedConverters.convert(item, TypeDescriptor.of(targetTypeDescriptor.getDeclaringType(), actualType)));
        }
    }

    public static class Object2Optional implements NestedConverter {

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return !Optional.class.isAssignableFrom(sourceType.getActualClass()) && Optional.class.isAssignableFrom(targetType.getActualClass());
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return Optional.empty();
            }
            Type actualType = targetTypeDescriptor.getGenericsTypes(Optional.class)[0];
            return Optional.ofNullable(NestedConverters.convert(sourceValue, TypeDescriptor.of(targetTypeDescriptor.getDeclaringType(), actualType)));
        }
    }
}
