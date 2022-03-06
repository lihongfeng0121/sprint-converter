package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.util.Types;
import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 多个转一个转换器
 *
 * @author hongfeng.li
 * @since 2022/1/25
 */
public class Multi2SingleConverters implements NestedConverterLoader {

    /**
     * 单集合转单对象
     */
    public static class SingleCollection2Single implements NestedConverter {

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(Object sourceValue, Class<?> sourceClass, Class<?> targetClass) {
            if (sourceValue == null) {
                return Types.isCollection(sourceClass) && !Types.isMulti(targetClass);
            } else {
                return Types.isCollection(sourceClass) && ((Collection<?>) sourceValue).size() == 1
                        && !Types.isMulti(targetClass);
            }
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            Collection<?> collection = (Collection<?>) sourceValue;
            if (collection == null || collection.isEmpty()) {
                return null;
            }
            if (collection.size() != 1) {
                throw new ConversionException("not support multi value!");
            }
            return NestedConverters.convert(collection.stream().findFirst().get(), targetBeanType, targetFiledType);
        }
    }

    /**
     * 单集合转单对象
     */
    public static class SingleArray2Single implements NestedConverter {

        @Override
        public int sort() {
            return 6;
        }

        @Override
        public boolean support(Object sourceValue, Class<?> sourceClass, Class<?> targetClass) {
            if (sourceValue == null) {
                return Types.isArray(sourceClass) && !Types.isMulti(targetClass);
            } else {
                return Types.isArray(sourceClass) && Array.getLength(sourceValue) == 1 && !Types.isMulti(targetClass);
            }
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            int length = Array.getLength(sourceValue);

            if (length != 1) {
                throw new ConversionException("not support multi value!");
            }
            return NestedConverters.convert(Array.get(sourceValue, 0), targetBeanType, targetFiledType);
        }
    }
}
