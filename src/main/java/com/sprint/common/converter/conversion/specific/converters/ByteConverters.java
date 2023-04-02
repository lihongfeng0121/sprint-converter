package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Miscs;
import com.sprint.common.converter.util.Strings;

/**
 * Byte转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class ByteConverters implements SpecificConverterLoader {

    public static class ByteToBaseByte implements SpecificConverter<Byte, Byte> {

        @Override
        public Byte convert(Byte obj) throws ConversionException {
            return obj == null ? 0 : obj;
        }

        @Override
        public Class<Byte> getSourceClass() {
            return Byte.class;
        }

        @Override
        public Class<Byte> getTargetClass() {
            return Byte.TYPE;
        }
    }

    public static class NumberToByte implements SpecificConverter<Number, Byte> {

        @Override
        public Byte convert(Number obj) throws ConversionException {
            return obj == null ? null : obj.byteValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Byte> getTargetClass() {
            return Byte.class;
        }
    }

    public static class NumberToBaseByte implements SpecificConverter<Number, Byte> {

        @Override
        public Byte convert(Number obj) throws ConversionException {
            return obj == null ? 0 : obj.byteValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Byte> getTargetClass() {
            return Byte.TYPE;
        }
    }

    public static class StringToBaseByte implements SpecificConverter<String, Byte> {

        @Override
        public Byte convert(String obj) throws ConversionException {
            return Strings.isBlank(obj) ? 0 : Double.valueOf(obj).byteValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Byte> getTargetClass() {
            return Byte.TYPE;
        }
    }

    public static class StringToByte implements SpecificConverter<String, Byte> {

        @Override
        public Byte convert(String obj) throws ConversionException {
            return Strings.isBlank(obj) ? null : Double.valueOf(obj).byteValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Byte> getTargetClass() {
            return Byte.class;
        }
    }
}
