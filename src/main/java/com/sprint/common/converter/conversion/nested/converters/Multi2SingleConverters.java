package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.bean.Beans;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

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
            return Types.isCollection(sourceClass) && !Types.isArray(targetClass) && !Types.isCollection(targetClass);
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && ((Collection<?>) sourceValue).size() <= 1;
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            Collection<?> collection = (Collection<?>) sourceValue;
            if (collection == null) {
                return null;
            }

            if (collection.isEmpty()) {
                Class<?> extractClass = Types.extractClass(targetFiledType, targetBeanType);
                Object target = Beans.instance(extractClass);
                Beans.copyProperties(collection, target, true, true, false, Types.COLLECTION_IGNORES);
                return target;
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
            return Types.isArray(sourceClass) && !Types.isArray(targetClass) && Types.isCollection(targetClass);
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Array.getLength(sourceValue) <= 1;
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            int length = Array.getLength(sourceValue);

            if (length == 0) {
                return null;
            }

            if (length > 1) {
                throw new NotSupportConvertException(sourceValue.getClass(), Types.extractClass(targetFiledType, targetBeanType));
            }

            return NestedConverters.convert(Array.get(sourceValue, 0), targetBeanType, targetFiledType);
        }
    }
}
