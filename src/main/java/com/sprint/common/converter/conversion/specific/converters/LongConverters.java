package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Miscs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * long转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class LongConverters implements SpecificConverterLoader {

    public static class LongToBaseLong implements SpecificConverter<Long, Long> {

        @Override
        public Long convert(Long obj) throws ConversionException {
            return obj == null ? 0L : obj;
        }

        @Override
        public Class<Long> getSourceClass() {
            return Long.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.TYPE;
        }
    }

    public static class NumberToLong implements SpecificConverter<Number, Long> {

        @Override
        public Long convert(Number obj) throws ConversionException {
            return obj == null ? null : obj.longValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.class;
        }
    }

    public static class NumberToBaseLong implements SpecificConverter<Number, Long> {

        @Override
        public Long convert(Number obj) throws ConversionException {
            return obj == null ? 0 : obj.longValue();
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.TYPE;
        }
    }

    public static class StringToBaseLong implements SpecificConverter<String, Long> {

        @Override
        public Long convert(String obj) throws ConversionException {
            return Miscs.isBlank(obj) ? 0L : Double.valueOf(obj).longValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.TYPE;
        }
    }

    public static class StringToLong implements SpecificConverter<String, Long> {

        @Override
        public Long convert(String obj) throws ConversionException {
            return Miscs.isBlank(obj) ? null : Double.valueOf(obj).longValue();
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.class;
        }
    }

    public static class DateTimeToLong implements SpecificConverter<Date, Long> {

        @Override
        public Long convert(Date source) throws ConversionException {
            return source == null ? null : source.getTime();
        }

        @Override
        public Class<Date> getSourceClass() {
            return Date.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.class;
        }
    }

    public static class DateTimeToBaseLong implements SpecificConverter<Date, Long> {

        @Override
        public Long convert(Date source) throws ConversionException {
            return source == null ? 0L : source.getTime();
        }

        @Override
        public Class<Date> getSourceClass() {
            return Date.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.TYPE;
        }
    }

    public static class LocalDateToLong implements SpecificConverter<LocalDate, Long> {

        @Override
        public Long convert(LocalDate source) throws ConversionException {
            return LocalDateConverters.toTime(source);
        }

        @Override
        public Class<LocalDate> getSourceClass() {
            return LocalDate.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.class;
        }
    }

    public static class LocalDateToBaseLong implements SpecificConverter<LocalDate, Long> {

        @Override
        public Long convert(LocalDate source) throws ConversionException {
            return Optional.ofNullable(LocalDateConverters.toTime(source)).orElse(0L);
        }

        @Override
        public Class<LocalDate> getSourceClass() {
            return LocalDate.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.TYPE;
        }
    }

    public static class LocalDateTimeToLong implements SpecificConverter<LocalDateTime, Long> {

        @Override
        public Long convert(LocalDateTime source) throws ConversionException {
            return LocalDateTimeConverters.toTime(source);
        }

        @Override
        public Class<LocalDateTime> getSourceClass() {
            return LocalDateTime.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.class;
        }
    }

    public static class LocalDateTimeToBaseLong implements SpecificConverter<LocalDateTime, Long> {

        @Override
        public Long convert(LocalDateTime source) throws ConversionException {
            return Optional.ofNullable(LocalDateTimeConverters.toTime(source)).orElse(0L);
        }

        @Override
        public Class<LocalDateTime> getSourceClass() {
            return LocalDateTime.class;
        }

        @Override
        public Class<Long> getTargetClass() {
            return Long.TYPE;
        }
    }
}
