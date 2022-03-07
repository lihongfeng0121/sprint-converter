package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * Double转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class DoubleConverters implements SpecificConverterLoader {

    public static class DoubleToBaseDouble implements SpecificConverter<Double, Double> {

        @Override
        public Double convert(Double obj) throws ConversionException {
            return obj == null ? 0D : obj;
        }

        @Override
        public Class<Double> getSourceClass() {
            return Double.class;
        }

        @Override
        public Class<Double> getTargetClass() {
            return Double.TYPE;
        }
    }

    public static class StringToBaseDouble implements SpecificConverter<String, Double> {

        @Override
        public Double convert(String obj) throws ConversionException {
            return obj == null ? 0D : Double.parseDouble(obj);
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Double> getTargetClass() {
            return Double.TYPE;
        }
    }

    public static class StringToDouble implements SpecificConverter<String, Double> {

        @Override
        public Double convert(String obj) throws ConversionException {
            return obj == null ? null : Double.valueOf(obj);
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Double> getTargetClass() {
            return Double.class;
        }
    }

    public static class NumberToDouble implements SpecificConverter<Number, Double> {

        @Override
        public Double convert(Number obj) throws ConversionException {
            return obj == null ? null : obj.doubleValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Double> getTargetClass() {
            return Double.class;
        }
    }
}
