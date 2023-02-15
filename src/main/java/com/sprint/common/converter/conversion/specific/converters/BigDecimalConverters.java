package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Miscs;

import java.math.BigDecimal;

/**
 * 数字转换
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class BigDecimalConverters implements SpecificConverterLoader {

    public static class BigDecimalToBigDecimal implements SpecificConverter<BigDecimal, BigDecimal> {

        @Override
        public BigDecimal convert(BigDecimal source) throws ConversionException {
            return source;
        }

        @Override
        public Class<BigDecimal> getSourceClass() {
            return BigDecimal.class;
        }

        @Override
        public Class<BigDecimal> getTargetClass() {
            return BigDecimal.class;
        }
    }

    public static class StringToBigDecimal implements SpecificConverter<String, BigDecimal> {

        @Override
        public BigDecimal convert(String source) throws ConversionException {
            return Miscs.isBlank(source) ? null : new BigDecimal(source);
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<BigDecimal> getTargetClass() {
            return BigDecimal.class;
        }
    }

    public static class NumberToBigDecimal implements SpecificConverter<Number, BigDecimal> {

        @Override
        public BigDecimal convert(Number source) throws ConversionException {
            return source == null ? null : BigDecimal.valueOf(source.doubleValue());
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<BigDecimal> getTargetClass() {
            return BigDecimal.class;
        }
    }
}
