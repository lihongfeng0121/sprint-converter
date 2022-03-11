package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Types.isArray(sourceClass) && Types.isArray(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            Type actualType = Types.getArrayComponentType(targetFiledType, targetBeanType);
            int length = Array.getLength(sourceValue);
            Object targetCValue = Array.newInstance(Types.extractClass(actualType, targetBeanType), length);
            for (int i = 0; i < length; i++) {
                Object item = Array.get(sourceValue, i);
                if (item != null) {
                    item = NestedConverters.convert(Array.get(sourceValue, i), targetBeanType,
                            Types.isObjectType(actualType) ? item.getClass() : actualType);
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return !Types.isArray(sourceClass) && !Types.isCollection(sourceClass) && Types.isArray(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            Type actualType = Types.getArrayComponentType(targetFiledType, targetBeanType);
            Object targetCValue = Array.newInstance(Types.extractClass(actualType, targetBeanType), 1);
            Array.set(targetCValue, 0, NestedConverters.convert(sourceValue, targetBeanType, actualType));
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Types.isCollection(sourceClass) && Types.isArray(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            Collection<?> cValue = (Collection<?>) sourceValue;
            Type actualType = Types.getArrayComponentType(targetFiledType, targetBeanType);
            Object targetCValue = Array.newInstance(Types.extractClass(actualType, targetBeanType), cValue.size());
            int i = 0;
            for (Object item : cValue) {
                Array.set(targetCValue, i++, NestedConverters.convert(item, targetBeanType,
                        Types.isObjectType(actualType) ? item.getClass() : actualType));
            }
            return targetCValue;
        }
    }
}
