package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * Boolean转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class BooleanConverters implements SpecificConverterLoader {

    /**
     * 将对象转化为Boolean
     *
     * @param obj 对象
     * @return boolean
     */
    public static boolean toBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof Number) {
            return !Integer.valueOf(0).equals(((Number) obj).intValue());
        } else {
            String str = String.valueOf(obj).trim();
            if ("1".equals(str)) {
                return true;
            } else if ("true".equalsIgnoreCase(str)) {
                return true;
            } else if ("是".equals(str)) {
                return true;
            } else if ("ok".equalsIgnoreCase(str)) {
                return true;
            } else if ("yes".equalsIgnoreCase(str)) {
                return true;
            } else {
                return "Y".equalsIgnoreCase(str);
            }
        }
    }

    public static class BooleanToBaseBoolean implements SpecificConverter<Boolean, Boolean> {

        @Override
        public Boolean convert(Boolean source) throws ConversionException {
            return source == null ? Boolean.FALSE : source;
        }

        @Override
        public Class<Boolean> getSourceClass() {
            return Boolean.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.TYPE;
        }
    }

    public static class StringToBaseBoolean implements SpecificConverter<String, Boolean> {

        @Override
        public Boolean convert(String source) throws ConversionException {
            if (source == null) {
                return Boolean.FALSE;
            } else {
                return toBoolean(source);
            }
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.TYPE;
        }
    }

    public static class StringToBoolean implements SpecificConverter<String, Boolean> {

        @Override
        public Boolean convert(String source) throws ConversionException {
            if (source == null) {
                return null;
            } else {
                return toBoolean(source);
            }
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.class;
        }
    }

    public static class NumberToBoolean implements SpecificConverter<Number, Boolean> {

        @Override
        public Boolean convert(Number source) throws ConversionException {
            if (source == null) {
                return null;
            } else {
                return Integer.valueOf(1).equals(source.intValue());
            }
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.class;
        }
    }

    public static class NumberToBaseBoolean implements SpecificConverter<Number, Boolean> {

        @Override
        public Boolean convert(Number source) throws ConversionException {
            if (source == null) {
                return Boolean.FALSE;
            } else {
                return Integer.valueOf(1).equals(source.intValue());
            }
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.TYPE;
        }
    }
}