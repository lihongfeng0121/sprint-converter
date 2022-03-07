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
            return !Integer.valueOf(0).equals(obj);
        } else {
            String str = String.valueOf(obj).trim();
            if ("1".equals(str)) {
                return true;
            } else if ("true".equalsIgnoreCase(str)) {
                return true;
            } else if ("是".equals(str)) {
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

    public static class ByteToBoolean implements SpecificConverter<Byte, Boolean> {

        @Override
        public Boolean convert(Byte source) throws ConversionException {
            if (source == null) {
                return null;
            } else {
                return Byte.valueOf((byte) 1).equals(source);
            }
        }

        @Override
        public Class<Byte> getSourceClass() {
            return Byte.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.class;
        }
    }

    public static class ByteToBaseBoolean implements SpecificConverter<Byte, Boolean> {

        @Override
        public Boolean convert(Byte source) throws ConversionException {
            if (source == null) {
                return Boolean.FALSE;
            } else {
                return Byte.valueOf((byte) 1).equals(source);
            }
        }

        @Override
        public Class<Byte> getSourceClass() {
            return Byte.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.TYPE;
        }
    }

    public static class ShortToBoolean implements SpecificConverter<Short, Boolean> {

        @Override
        public Boolean convert(Short source) throws ConversionException {
            if (source == null) {
                return null;
            } else {
                return Short.valueOf((short) 1).equals(source);
            }
        }

        @Override
        public Class<Short> getSourceClass() {
            return Short.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.class;
        }
    }

    public static class ShortToBaseBoolean implements SpecificConverter<Short, Boolean> {

        @Override
        public Boolean convert(Short source) throws ConversionException {
            if (source == null) {
                return Boolean.FALSE;
            } else {
                return Short.valueOf((short) 1).equals(source);
            }
        }

        @Override
        public Class<Short> getSourceClass() {
            return Short.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.TYPE;
        }
    }

    public static class IntegerToBoolean implements SpecificConverter<Integer, Boolean> {

        @Override
        public Boolean convert(Integer source) throws ConversionException {
            if (source == null) {
                return null;
            } else {
                return Integer.valueOf(1).equals(source);
            }
        }

        @Override
        public Class<Integer> getSourceClass() {
            return Integer.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.class;
        }
    }

    public static class IntegerToBaseBoolean implements SpecificConverter<Integer, Boolean> {

        @Override
        public Boolean convert(Integer source) throws ConversionException {
            if (source == null) {
                return Boolean.FALSE;
            } else {
                return Integer.valueOf(1).equals(source);
            }
        }

        @Override
        public Class<Integer> getSourceClass() {
            return Integer.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.TYPE;
        }
    }

    public static class LongToBoolean implements SpecificConverter<Long, Boolean> {

        @Override
        public Boolean convert(Long source) throws ConversionException {
            if (source == null) {
                return null;
            } else {
                return Long.valueOf(1).equals(source);
            }
        }

        @Override
        public Class<Long> getSourceClass() {
            return Long.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.class;
        }
    }

    public static class LongToBaseBoolean implements SpecificConverter<Long, Boolean> {

        @Override
        public Boolean convert(Long source) throws ConversionException {
            if (source == null) {
                return Boolean.FALSE;
            } else {
                return Long.valueOf(1).equals(source);
            }
        }

        @Override
        public Class<Long> getSourceClass() {
            return Long.class;
        }

        @Override
        public Class<Boolean> getTargetClass() {
            return Boolean.TYPE;
        }
    }
}