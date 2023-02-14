package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.ObjectValue;

import java.lang.reflect.Type;

/**
 * @author hongfeng.li
 * @since 2023/2/14
 */
public class ObjectValueConverters implements NestedConverterLoader {

    /**
     * ObjectValue转对象
     */
    public static class ObjectValue2Object implements NestedConverter {

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return ObjectValue.class.isAssignableFrom(sourceClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            ObjectValue objectValue = (ObjectValue) sourceValue;
            if (objectValue == null) {
                return null;
            }
            return objectValue.map(item -> NestedConverters.convert(item, targetBeanType, targetFiledType)).orElse(null);
        }
    }

    public static class Object2ObjectValue implements NestedConverter {

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return !ObjectValue.class.isAssignableFrom(sourceClass) && ObjectValue.class.isAssignableFrom(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return ObjectValue.empty();
            }
            return ObjectValue.ofNullable(NestedConverters.convert(sourceValue, null, sourceValue.getClass()));
        }
    }

    public static class ObjectValue2ObjectValue implements NestedConverter {

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return ObjectValue.class.isAssignableFrom(sourceClass) && ObjectValue.class.isAssignableFrom(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            ObjectValue objectValue = (ObjectValue) sourceValue;
            return objectValue.map(item -> NestedConverters.convert(item, null, item.getClass()));
        }
    }
}
