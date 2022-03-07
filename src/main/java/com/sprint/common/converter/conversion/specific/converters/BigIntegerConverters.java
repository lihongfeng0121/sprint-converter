package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * BigInteger转换器
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class BigIntegerConverters implements SpecificConverterLoader {

    public static class BigIntegerToBigInteger implements SpecificConverter<BigInteger, BigInteger> {

        @Override
        public BigInteger convert(BigInteger source) throws ConversionException {
            return source;
        }

        @Override
        public Class<BigInteger> getSourceClass() {
            return BigInteger.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

    public static class StringToBigInteger implements SpecificConverter<String, BigInteger> {

        @Override
        public BigInteger convert(String source) throws ConversionException {
            return source == null ? null
                    : source.contains(".") ? BigInteger.valueOf(Double.valueOf(source).longValue())
                    : new BigInteger(source);
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

    public static class ByteToBigInteger implements SpecificConverter<Byte, BigInteger> {

        @Override
        public BigInteger convert(Byte source) throws ConversionException {
            return source == null ? null : BigInteger.valueOf(source.longValue());
        }

        @Override
        public Class<Byte> getSourceClass() {
            return Byte.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

    public static class ShortToBigInteger implements SpecificConverter<Short, BigInteger> {

        @Override
        public BigInteger convert(Short source) throws ConversionException {
            return source == null ? null : BigInteger.valueOf(source.longValue());
        }

        @Override
        public Class<Short> getSourceClass() {
            return Short.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

    public static class IntegerToBigInteger implements SpecificConverter<Integer, BigInteger> {

        @Override
        public BigInteger convert(Integer source) throws ConversionException {
            return source == null ? null : BigInteger.valueOf(source.longValue());
        }

        @Override
        public Class<Integer> getSourceClass() {
            return Integer.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

    public static class LongToBigInteger implements SpecificConverter<Long, BigInteger> {

        @Override
        public BigInteger convert(Long source) throws ConversionException {
            return source == null ? null : BigInteger.valueOf(source);
        }

        @Override
        public Class<Long> getSourceClass() {
            return Long.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

    public static class FloatToBigInteger implements SpecificConverter<Float, BigInteger> {

        @Override
        public BigInteger convert(Float source) throws ConversionException {
            return source == null ? null : BigInteger.valueOf(source.longValue());
        }

        @Override
        public Class<Float> getSourceClass() {
            return Float.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

    public static class DoubleToBigInteger implements SpecificConverter<Double, BigInteger> {

        @Override
        public BigInteger convert(Double source) throws ConversionException {
            return source == null ? null : BigInteger.valueOf(source.longValue());
        }

        @Override
        public Class<Double> getSourceClass() {
            return Double.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

    public static class BigDecimalToBigInteger implements SpecificConverter<BigDecimal, BigInteger> {

        @Override
        public BigInteger convert(BigDecimal source) throws ConversionException {
            return source == null ? null : BigInteger.valueOf(source.longValue());
        }

        @Override
        public Class<BigDecimal> getSourceClass() {
            return BigDecimal.class;
        }

        @Override
        public Class<BigInteger> getTargetClass() {
            return BigInteger.class;
        }
    }

}
