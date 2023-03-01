package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.TypeDescriptor;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * 数组转换器
 *
 * @author hongfeng.li
 * @since 2021/1/25
 */
public class ArrayConverters implements NestedConverterLoader {

    /**
     * 数组转数组
     */
    public static class Array2Array implements NestedConverter {

        @Override
        public int sort() {
            return 1;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.isArray() && targetType.isArray();
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor typeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            TypeDescriptor arrayComponentType = typeDescriptor.getArrayComponentTypeDescriptor();
            int length = Array.getLength(sourceValue);
            Object targetCValue = Array.newInstance(arrayComponentType.getActualClass(), length);
            for (int i = 0; i < length; i++) {
                Object item = Array.get(sourceValue, i);
                if (item != null) {
                    item = NestedConverters.convert(Array.get(sourceValue, i), arrayComponentType);
                }
                Array.set(targetCValue, i, item);
            }
            return targetCValue;
        }
    }

    /**
     * 单一属性转数组
     */
    public static class Single2Array implements NestedConverter {

        @Override
        public int sort() {
            return 12;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return !sourceType.isArray() && !sourceType.isCollection() && targetType.isArray();
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor typeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            TypeDescriptor arrayComponentType = typeDescriptor.getArrayComponentTypeDescriptor();
            Object targetCValue = Array.newInstance(arrayComponentType.getActualClass(), 1);
            Array.set(targetCValue, 0, NestedConverters.convert(sourceValue, arrayComponentType));
            return targetCValue;
        }
    }

    /**
     * 集合转数组
     */
    public static class Collection2Array implements NestedConverter {

        @Override
        public int sort() {
            return 3;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.isCollection() && targetType.isArray();
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            Collection<?> cValue = (Collection<?>) sourceValue;
            TypeDescriptor arrayComponentType = targetTypeDescriptor.getArrayComponentTypeDescriptor();
            Object targetCValue = Array.newInstance(arrayComponentType.getActualClass(), cValue.size());
            int i = 0;
            for (Object item : cValue) {
                Array.set(targetCValue, i++, NestedConverters.convert(item, arrayComponentType));
            }
            return targetCValue;
        }
    }
}
