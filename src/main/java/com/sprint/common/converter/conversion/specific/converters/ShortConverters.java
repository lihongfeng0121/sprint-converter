package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * Short转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class ShortConverters implements SpecificConverterLoader {

    public static class ShortToBaseShort implements SpecificConverter<Short, Short> {

        @Override
        public Short convert(Short obj) throws ConversionException {
            return obj == null ? 0 : obj;
        }

        @Override
        public Class<Short> getSourceClass() {
            return Short.class;
        }

        @Override
        public Class<Short> getTargetClass() {
            return Short.TYPE;
        }
    }

    public static class StringToBaseShort implements SpecificConverter<String, Short> {

        @Override
        public Short convert(String obj) throws ConversionException {
            return obj == null ? 0 : Double.valueOf(obj).shortValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Short> getTargetClass() {
            return Short.TYPE;
        }
    }

    public static class StringToShort implements SpecificConverter<String, Short> {

        @Override
        public Short convert(String obj) throws ConversionException {
            return obj == null ? null : Double.valueOf(obj).shortValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Short> getTargetClass() {
            return Short.class;
        }
    }

    public static class NumberToShort implements SpecificConverter<Number, Short> {

        @Override
        public Short convert(Number obj) throws ConversionException {
            return obj == null ? null : obj.shortValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Short> getTargetClass() {
            return Short.class;
        }
    }

    public static class NumberToBaseShort implements SpecificConverter<Number, Short> {

        @Override
        public Short convert(Number obj) throws ConversionException {
            return obj == null ? 0 : obj.shortValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Short> getTargetClass() {
            return Short.TYPE;
        }
    }
}
