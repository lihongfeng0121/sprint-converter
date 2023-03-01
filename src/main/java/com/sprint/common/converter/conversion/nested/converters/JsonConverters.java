package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Jsons;
import com.sprint.common.converter.util.TypeDescriptor;
import com.sprint.common.converter.util.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * @author hongfeng.li
 * @since 2021/1/25
 */
public class JsonConverters implements NestedConverterLoader {
    /**
     * 集合或者Bean 转json
     */
    public static class MultiOrBean2JsonStr implements NestedConverter {

        @Override
        public int sort() {
            return 10;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return (sourceType.isMulti() || sourceType.isBean())
                    && String.class.isAssignableFrom(targetType.getActualClass());
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            return Jsons.toJsonString(sourceValue);
        }
    }

    /**
     * Json转bean
     */
    public static class JsonStr2Bean implements NestedConverter {

        @Override
        public int sort() {
            return 9;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return String.class.isAssignableFrom(sourceType.getActualClass()) && targetType.isBean();
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Types.isJsonObject((String) sourceValue);
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            String jsonStr = (String) sourceValue;

            if (!Types.isJsonObject(jsonStr)) {
                throw new NotSupportConvertException(sourceValue.getClass(), targetTypeDescriptor.getActualClass());
            }
            Object map = Jsons.toJavaObject(jsonStr, LinkedHashMap.class);
            return NestedConverters.convert(map, targetTypeDescriptor);
        }
    }

    /**
     * Json转bean
     */
    public static class JsonStr2Map implements NestedConverter {

        @Override
        public int sort() {
            return 9;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return String.class.isAssignableFrom(sourceType.getActualClass()) && targetType.isMap();
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Types.isJsonObject((String) sourceValue);
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            String jsonStr = (String) sourceValue;

            if (!Types.isJsonObject(jsonStr)) {
                throw new NotSupportConvertException(sourceValue.getClass(), targetTypeDescriptor.getActualClass());
            }
            Object map = Jsons.toJavaObject(jsonStr, LinkedHashMap.class);
            return NestedConverters.convert(map, targetTypeDescriptor);
        }
    }

    /**
     * json 转集合
     */
    public static class JsonStr2Collection implements NestedConverter {

        @Override
        public int sort() {
            return 8;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return String.class.isAssignableFrom(sourceType.getActualClass()) && targetType.isCollection();
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Types.isJsonArray((String) sourceValue);
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            String jsonStr = (String) sourceValue;

            if (!Types.isJsonArray(jsonStr)) {
                throw new NotSupportConvertException(sourceValue.getClass(), targetTypeDescriptor.getActualClass());
            }

            Collection<?> list = Jsons.toJavaObjects(jsonStr, Types.OBJECT_CLASS, ArrayList.class);
            return NestedConverters.convert(list, targetTypeDescriptor);
        }
    }

    /**
     * json 转集合
     */
    public static class JsonStr2Array implements NestedConverter {

        @Override
        public int sort() {
            return 8;
        }

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return String.class.isAssignableFrom(sourceType.getActualClass()) && targetType.isArray();
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Types.isJsonArray((String) sourceValue);
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            String jsonStr = (String) sourceValue;

            if (!Types.isJsonArray(jsonStr)) {
                throw new NotSupportConvertException(sourceValue.getClass(), targetTypeDescriptor.getActualClass());
            }

            Collection<?> list = Jsons.toJavaObjects(jsonStr, Types.OBJECT_CLASS, ArrayList.class);
            return NestedConverters.convert(list, targetTypeDescriptor);
        }
    }
}
