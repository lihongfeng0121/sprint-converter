package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.NestedPreCheckConverter;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.TypeDescriptor;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Array;
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
    public static class SingleCollection2Single implements NestedPreCheckConverter {

        @Override
        public int sort() {
            return 5;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.isCollection() && !targetType.isArray() && !targetType.isCollection();
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue, TypeDescriptor sourceType, TypeDescriptor targetTypeDescriptor) {
            return sourceValue != null && ((Collection<?>) sourceValue).size() <= 1;
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            Collection<?> collection = (Collection<?>) sourceValue;
            if (collection == null) {
                return null;
            }

            if (collection.isEmpty()) {
                if (targetTypeDescriptor.isBean()) {
                    Class<?> extractClass = targetTypeDescriptor.getActualClass();
                    Object target = Beans.instance(extractClass);
                    Beans.copyProperties(collection, target, true, true, false, Types.COLLECTION_IGNORES);
                    return target;
                } else {
                    return null;
                }
            }

            return NestedConverters.convert(collection.stream().findFirst().get(), targetTypeDescriptor);
        }
    }

    /**
     * 单集合转单对象
     */
    public static class SingleArray2Single implements NestedPreCheckConverter {

        @Override
        public int sort() {
            return 6;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.isArray() && !targetType.isArray() && targetType.isCollection();
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue, TypeDescriptor sourceType, TypeDescriptor targetTypeDescriptor) {
            return sourceValue != null && Array.getLength(sourceValue) <= 1;
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            int length = Array.getLength(sourceValue);

            if (length == 0) {
                return null;
            }

            if (length > 1) {
                throw new NotSupportConvertException(sourceValue.getClass(), targetTypeDescriptor.getActualClass());
            }

            return NestedConverters.convert(Array.get(sourceValue, 0), targetTypeDescriptor);
        }
    }
}
