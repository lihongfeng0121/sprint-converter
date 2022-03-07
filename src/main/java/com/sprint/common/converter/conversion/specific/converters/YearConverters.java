package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.text.ParseException;
import java.time.*;
import java.util.Date;

/**
 * 年转换
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class YearConverters implements SpecificConverterLoader {

    public static Year toYear(Date date) {
        if (date == null) {
            return null;
        }
        return Year.of(Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).getYear());
    }

    public static class LocalDateToYear implements SpecificConverter<LocalDate, Year> {

        @Override
        public Year convert(LocalDate source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Year.from(source);
        }

        @Override
        public Class<LocalDate> getSourceClass() {
            return LocalDate.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }

    public static class LocalDateTimeToYear implements SpecificConverter<LocalDateTime, Year> {

        @Override
        public Year convert(LocalDateTime source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Year.from(source);
        }

        @Override
        public Class<LocalDateTime> getSourceClass() {
            return LocalDateTime.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }

    public static class YearMonthToYear implements SpecificConverter<YearMonth, Year> {

        @Override
        public Year convert(YearMonth source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Year.from(source);
        }

        @Override
        public Class<YearMonth> getSourceClass() {
            return YearMonth.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }

    public static class DateTimeToYear implements SpecificConverter<Date, Year> {

        @Override
        public Year convert(Date source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return toYear(source);
        }

        @Override
        public Class<Date> getSourceClass() {
            return Date.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }

    }

    public static class DateToYear implements SpecificConverter<java.sql.Date, Year> {

        @Override
        public Year convert(java.sql.Date source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return toYear(source);
        }

        @Override
        public Class<java.sql.Date> getSourceClass() {
            return java.sql.Date.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }

    public static class TimestampToYear implements SpecificConverter<java.sql.Timestamp, Year> {

        @Override
        public Year convert(java.sql.Timestamp source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return toYear(source);
        }

        @Override
        public Class<java.sql.Timestamp> getSourceClass() {
            return java.sql.Timestamp.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }

    public static class ShortToYear implements SpecificConverter<Short, Year> {

        @Override
        public Year convert(Short source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Year.of(source);
        }

        @Override
        public Class<Short> getSourceClass() {
            return Short.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }

    public static class IntegerToYear implements SpecificConverter<Integer, Year> {

        @Override
        public Year convert(Integer source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Year.of(source);
        }

        @Override
        public Class<Integer> getSourceClass() {
            return Integer.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }

    public static class LongToYear implements SpecificConverter<Long, Year> {

        @Override
        public Year convert(Long source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Year.of(source.intValue());
        }

        @Override
        public Class<Long> getSourceClass() {
            return Long.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }

    public static class StringToYear implements SpecificConverter<String, Year> {

        @Override
        public Year convert(String source) throws ConversionException {
            if (source == null) {
                return null;
            }
            try {
                return toYear(DateTimeConverters.toDate(source));
            } catch (ParseException ex) {
                throw new ConversionException(ex);
            }
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Year> getTargetClass() {
            return Year.class;
        }
    }
}
