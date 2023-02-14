package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.GenericsResolver;
import com.sprint.common.converter.util.Types;

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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Optional.class.isAssignableFrom(sourceClass) && !Optional.class.isAssignableFrom(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            Optional<?> optional = (Optional<?>) sourceValue;
            if (optional == null || !optional.isPresent()) {
                return null;
            }
            return optional.map(item -> NestedConverters.convert(item, targetBeanType, targetFiledType)).orElse(null);
        }
    }

    public static class Optional2Optional implements NestedConverter {

        public static final GenericsResolver OPTIONAL_TYPE_RESOLVER = GenericsResolver.of(Optional.class);

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Optional.class.isAssignableFrom(sourceClass) && Optional.class.isAssignableFrom(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            Optional<?> optional = (Optional<?>) sourceValue;
            if (optional == null) {
                return null;
            }
            if (!optional.isPresent()) {
                return Optional.empty();
            }
            Type actualType = OPTIONAL_TYPE_RESOLVER.resolve(targetBeanType, targetFiledType)[0];
            return optional.map(item -> NestedConverters.convert(item, targetBeanType, actualType == null || Types.isObjectType(actualType) ? item.getClass() : actualType));
        }
    }

    public static class Object2Optional implements NestedConverter {
        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return !Optional.class.isAssignableFrom(sourceClass) && Optional.class.isAssignableFrom(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return Optional.empty();
            }
            Type actualType = GenericsResolver.of(Optional.class).resolve(targetBeanType, targetFiledType)[0];
            return Optional.ofNullable(NestedConverters.convert(sourceValue, targetBeanType, actualType == null || Types.isObjectType(actualType) ? sourceValue.getClass() : actualType));
        }
    }
}
