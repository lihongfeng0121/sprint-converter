package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.Defaults;
import com.sprint.common.converter.util.TypeDescriptor;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * @author hongfeng.li
 * @since 2021/1/25
 */
public class CollectionConverters implements NestedConverterLoader {

    /**
     * 数组转集合
     */
    public static class Array2Collection implements NestedConverter {

        @Override
        public int sort() {
            return 0;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.isArray() && targetType.isCollection();
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            Class<?> extractClass = targetTypeDescriptor.getActualClass();
            Collection<Object> targetCValue = Beans.instanceCollection(extractClass);
            TypeDescriptor itemTypeDescriptor = targetTypeDescriptor.getCollectionItemTypeDescriptor();
            for (int i = 0, length = Array.getLength(sourceValue); i < length; i++) {
                Object item = Array.get(sourceValue, i);
                if (item != null) {
                    targetCValue.add(NestedConverters.convert(Array.get(sourceValue, i), itemTypeDescriptor));
                } else {
                    targetCValue.add(Defaults.defaultValue(itemTypeDescriptor.getActualClass()));
                }
            }
            return targetCValue;
        }
    }

    /**
     * 集合转集合
     */
    public static class Collection2Collection implements NestedConverter {

        @Override
        public int sort() {
            return 4;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.isCollection() && targetType.isCollection();
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor) throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            Collection<?> collection = (Collection<?>) sourceValue;
            Class<?> extractClass = targetTypeDescriptor.getActualClass();
            Collection<Object> targetCValue = extractClass.isAssignableFrom(collection.getClass())
                    ? Beans.instanceCollection(collection.getClass())
                    : Beans.instanceCollection(extractClass);
            if (Types.isBean(sourceValue.getClass()) && Types.isBean(targetCValue.getClass())) {
                Beans.copyProperties(collection, targetCValue, true, true, false, Types.COLLECTION_IGNORES);
            }
            if (!collection.isEmpty()) {
                TypeDescriptor itemTypeDescriptor = targetTypeDescriptor.getCollectionItemTypeDescriptor();
                for (Object item : collection) {
                    if (item == null) {
                        targetCValue.add(Defaults.defaultValue(itemTypeDescriptor.getActualClass()));
                    } else {
                        targetCValue.add(NestedConverters.convert(item, itemTypeDescriptor));
                    }
                }
            }
            return targetCValue;
        }
    }

    /**
     * 单一属性转集合
     */
    public static class Single2Collection implements NestedConverter {

        @Override
        public int sort() {
            return 11;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return !sourceType.isArray() && !sourceType.isCollection() && targetType.isCollection();
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            Class<?> extractClass = targetTypeDescriptor.getActualClass();
            Collection<Object> targetCValue = Beans.instanceCollection(extractClass);
            TypeDescriptor itemTypeDescriptor = targetTypeDescriptor.getCollectionItemTypeDescriptor();
            if (Types.isBean(sourceValue.getClass()) && targetTypeDescriptor.isBean()) {
                Beans.copyProperties(sourceValue, targetCValue, true, true, false, Types.COLLECTION_IGNORES);
            }
            targetCValue.add(NestedConverters.convert(sourceValue, itemTypeDescriptor));
            return targetCValue;
        }
    }
}
