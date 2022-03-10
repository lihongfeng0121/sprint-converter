package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.bean.Beans;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
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
        public boolean specific() {
            return true;
        }

        @Override
        public int sort() {
            return 0;
        }

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Types.isArray(sourceClass) && Types.isCollection(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            Class<?> extractClass = Types.extractClass(targetFiledType, targetBeanType);
            Collection<Object> targetCValue = Beans.instanceCollection(extractClass);
            Type actualType = Types.getCollectionItemType(targetBeanType, targetFiledType);
            for (int i = 0, length = Array.getLength(sourceValue); i < length; i++) {
                Object item = Array.get(sourceValue, i);
                if (item != null) {
                    Type thisActualType = actualType == null ? item.getClass() : actualType;
                    targetCValue
                            .add(NestedConverters.convert(Array.get(sourceValue, i), targetBeanType, thisActualType));
                } else {
                    targetCValue.add(null);
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
        public boolean specific() {
            return true;
        }

        @Override
        public int sort() {
            return 4;
        }

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Types.isCollection(sourceClass) && Types.isCollection(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            Collection<?> collection = (Collection<?>) sourceValue;
            Class<?> extractClass = Types.extractClass(targetFiledType, targetBeanType);
            Collection<Object> targetCValue = extractClass.isAssignableFrom(collection.getClass())
                    ? Beans.instanceCollection(collection.getClass())
                    : Beans.instanceCollection(extractClass);
            if (Types.isBean(targetCValue.getClass())) {
                Beans.copyProperties(collection, targetCValue, true, true, false);
            }
            if (!collection.isEmpty()) {
                Type actualType = Types.getCollectionItemType(targetBeanType, targetFiledType);
                for (Object item : collection) {
                    if (item == null) {
                        targetCValue.add(null);
                    } else {
                        targetCValue.add(NestedConverters.convert(item, targetBeanType,
                                actualType == null ? item.getClass() : actualType));
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return !Types.isMulti(sourceClass) && Types.isCollection(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            Class<?> extractClass = Types.extractClass(targetFiledType, targetBeanType);
            Collection<Object> targetCValue = Beans.instanceCollection(extractClass);
            Type actualType = Types.getCollectionItemType(targetBeanType, targetFiledType);
            Class<?> actualTypeClass = Types.extractClass(actualType);
            if (Types.isBean(actualTypeClass) && Types.isBean(extractClass)) {
                Beans.copyProperties(sourceValue, targetCValue);
            }
            targetCValue.add(NestedConverters.convert(sourceValue, targetBeanType, actualType));
            return targetCValue;
        }
    }
}
