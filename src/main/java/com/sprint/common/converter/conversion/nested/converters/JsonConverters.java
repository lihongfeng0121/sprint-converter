package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.json.JsonException;
import com.sprint.common.converter.conversion.nested.json.Jsons;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return (Types.isMulti(sourceClass) || Types.isBean(sourceClass))
                    && String.class.isAssignableFrom(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            try {
                return Jsons.toJsonString(sourceValue);
            } catch (JsonException e) {
                throw new ConversionException(e);
            }
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return String.class.isAssignableFrom(sourceClass) && Types.isBean(targetClass);
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Types.isJsonObject((String) sourceValue);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            String jsonStr = (String) sourceValue;

            if (!Types.isJsonObject(jsonStr)) {
                throw new NotSupportConvertException(sourceValue.getClass(), Types.extractClass(targetFiledType, targetBeanType));
            }
            try {
                Object map = Jsons.toJavaObject(jsonStr, LinkedHashMap.class);
                return NestedConverters.convert(map, targetBeanType, targetFiledType);
            } catch (JsonException e) {
                throw new ConversionException(e);
            }
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return String.class.isAssignableFrom(sourceClass) && Types.isMap(targetClass);
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Types.isJsonObject((String) sourceValue);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            String jsonStr = (String) sourceValue;

            if (!Types.isJsonObject(jsonStr)) {
                throw new NotSupportConvertException(sourceValue.getClass(), Types.extractClass(targetFiledType, targetBeanType));
            }
            try {
                Object map = Jsons.toJavaObject(jsonStr, LinkedHashMap.class);
                return NestedConverters.convert(map, targetBeanType, targetFiledType);
            } catch (JsonException e) {
                throw new ConversionException(e);
            }
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return String.class.isAssignableFrom(sourceClass) && Types.isCollection(targetClass);
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Types.isJsonArray((String) sourceValue);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            String jsonStr = (String) sourceValue;

            if (!Types.isJsonArray(jsonStr)) {
                throw new NotSupportConvertException(sourceValue.getClass(), Types.extractClass(targetFiledType, targetBeanType));
            }

            try {
                Collection<?> list = Jsons.toJavaObjects(jsonStr, Types.OBJECT_CLASS, ArrayList.class);
                return NestedConverters.convert(list, targetBeanType, targetFiledType);
            } catch (JsonException e) {
                throw new ConversionException(e);
            }
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
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return String.class.isAssignableFrom(sourceClass) && Types.isArray(targetClass);
        }

        @Override
        public boolean preCheckSourceVal(Object sourceValue) {
            return sourceValue != null && Types.isJsonArray((String) sourceValue);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            String jsonStr = (String) sourceValue;

            if (!Types.isJsonArray(jsonStr)) {
                throw new NotSupportConvertException(sourceValue.getClass(), Types.extractClass(targetFiledType, targetBeanType));
            }

            try {
                Collection<?> list = Jsons.toJavaObjects(jsonStr, Types.OBJECT_CLASS, ArrayList.class);
                return NestedConverters.convert(list, targetBeanType, targetFiledType);
            } catch (JsonException e) {
                throw new ConversionException(e);
            }
        }
    }
}
