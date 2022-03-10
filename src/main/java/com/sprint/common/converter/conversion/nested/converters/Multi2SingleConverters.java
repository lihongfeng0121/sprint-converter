package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 多个转一个转换器
 *
 * @author hongfeng.li
 * @since 2021/1/25
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Types.isCollection(sourceClass) && !Types.isMulti(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            Collection<?> collection = (Collection<?>) sourceValue;
            if (collection == null || collection.isEmpty()) {
                return null;
            }
            if (collection.size() != 1) {
                throw new NotSupportConvertException(sourceValue.getClass(), Types.extractClass(targetFiledType, targetBeanType));
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Types.isArray(sourceClass) && !Types.isMulti(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            int length = Array.getLength(sourceValue);

            if (length != 1) {
                throw new NotSupportConvertException(sourceValue.getClass(), Types.extractClass(targetFiledType, targetBeanType));
            }
            return NestedConverters.convert(Array.get(sourceValue, 0), targetBeanType, targetFiledType);
        }
    }
}
