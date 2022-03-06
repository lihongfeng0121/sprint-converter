package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.math.BigDecimal;

/**
 * @author hongfeng-li
 * @version 1.0
 * @title FloatConverters
 * @desc Float转换器
 * @date 2019年12月25日
 */
public class FloatConverters implements SpecificConverterLoader {

    public static class NumberToBaseFloat implements SpecificConverter<Number, Float> {

        @Override
        public Float convert(Number obj) throws ConversionException {
            return obj == null ? 0F : obj.floatValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Float> getTargetClass() {
            return Float.TYPE;
        }
    }

    public static class NumberToFloat implements SpecificConverter<Number, Float> {

        @Override
        public Float convert(Number obj) throws ConversionException {
            return obj == null ? null : obj.floatValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Float> getTargetClass() {
            return Float.class;
        }
    }

    public static class StringToBaseFloat implements SpecificConverter<String, Float> {

        @Override
        public Float convert(String obj) throws ConversionException {
            return obj == null ? 0F : new BigDecimal(obj).floatValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Float> getTargetClass() {
            return Float.TYPE;
        }
    }

    public static class StringToFloat implements SpecificConverter<String, Float> {
        public StringToFloat() {
        }

        @Override
        public Float convert(String obj) throws ConversionException {
            return obj == null ? null : new BigDecimal(obj).floatValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Float> getTargetClass() {
            return Float.class;
        }
    }
}
