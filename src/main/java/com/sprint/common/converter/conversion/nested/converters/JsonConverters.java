package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.util.Types;
import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.json.JsonException;
import com.sprint.common.converter.conversion.nested.json.Jsons;
import com.sprint.common.converter.exception.ConversionException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

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
        public boolean support(Object sourceValue, Class<?> sourceClass, Class<?> targetClass) {
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
        public boolean support(Object sourceValue, Class<?> sourceClass, Class<?> targetClass) {
            if (sourceValue == null) {
                return String.class.isAssignableFrom(sourceClass)
                        && (Types.isBean(targetClass) || Types.isMap(targetClass));
            } else {
                return String.class.isAssignableFrom(sourceClass)
                        && (Types.isBean(targetClass) || Types.isMap(targetClass))
                        && Optional.of(((String) sourceValue).trim())
                                .map(Types::isJsonObject).orElse(false);
            }
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            try {
                Object map = Jsons.toJavaObject((String) sourceValue, LinkedHashMap.class);
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
        public boolean support(Object sourceValue, Class<?> sourceClass, Class<?> targetClass) {
            if (sourceValue == null) {
                return String.class.isAssignableFrom(sourceClass) && Types.isCollection(targetClass);
            } else {
                return String.class.isAssignableFrom(sourceValue.getClass()) && Types.isCollection(targetClass)
                        && Optional.of(((String) sourceValue).trim())
                                .map(Types::isJsonArray).orElse(false);
            }
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            try {
                Collection<?> list = Jsons.toJavaObjects((String) sourceValue, Object.class, ArrayList.class);
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
        public boolean support(Object sourceValue, Class<?> sourceClass, Class<?> targetClass) {
            if (sourceValue == null) {
                return String.class.isAssignableFrom(sourceClass) && Types.isArray(targetClass);
            } else {
                return String.class.isAssignableFrom(sourceValue.getClass()) && Types.isArray(targetClass)
                        && Optional.of(((String) sourceValue).trim())
                                .map(Types::isJsonArray).orElse(false);
            }
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }
            try {
                Collection<?> list = Jsons.toJavaObjects((String) sourceValue, Object.class, ArrayList.class);
                return NestedConverters.convert(list, targetBeanType, targetFiledType);
            } catch (JsonException e) {
                throw new ConversionException(e);
            }
        }
    }
}
