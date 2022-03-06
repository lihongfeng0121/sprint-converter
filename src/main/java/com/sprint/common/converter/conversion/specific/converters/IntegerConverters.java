package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng-li
 * @version 1.0
 * @title IntegerConverters
 * @desc Integer转换器
 * @date 2019年12月25日
 */
public class IntegerConverters implements SpecificConverterLoader {

    public static class StringToInt implements SpecificConverter<String, Integer> {

        @Override
        public Integer convert(String obj) throws ConversionException {
            return obj == null ? 0 : Double.valueOf(obj).intValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Integer> getTargetClass() {
            return Integer.TYPE;
        }
    }

    public static class StringToInteger implements SpecificConverter<String, Integer> {

        @Override
        public Integer convert(String obj) throws ConversionException {
            return obj == null ? null : Double.valueOf(obj).intValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Integer> getTargetClass() {
            return Integer.class;
        }
    }

    public static class IntegerToInt implements SpecificConverter<Integer, Integer> {

        @Override
        public Integer convert(Integer obj) throws ConversionException {
            return obj;
        }

        @Override
        public Class<Integer> getSourceClass() {
            return Integer.class;
        }

        @Override
        public Class<Integer> getTargetClass() {
            return Integer.TYPE;
        }
    }

    public static class NumberToInteger implements SpecificConverter<Number, Integer> {

        @Override
        public Integer convert(Number obj) throws ConversionException {
            return obj == null ? null : obj.intValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Integer> getTargetClass() {
            return Integer.class;
        }
    }

    public static class NumberToInt implements SpecificConverter<Number, Integer> {

        @Override
        public Integer convert(Number obj) throws ConversionException {
            return obj == null ? 0 : obj.intValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Integer> getTargetClass() {
            return Integer.TYPE;
        }
    }
}
