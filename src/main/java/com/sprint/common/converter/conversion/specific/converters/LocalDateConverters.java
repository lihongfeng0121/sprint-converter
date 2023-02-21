package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Dates;
import com.sprint.common.converter.util.Miscs;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 时间转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class LocalDateConverters implements SpecificConverterLoader {

    public static class NumberToLocalDate implements SpecificConverter<Number, LocalDate> {

        @Override
        public LocalDate convert(Number obj) throws ConversionException {
            return obj == null ? null : Dates.toLocalDate(obj.longValue());
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<LocalDate> getTargetClass() {
            return LocalDate.class;
        }
    }

    public static class StringToLocalDate implements SpecificConverter<String, LocalDate> {

        @Override
        public LocalDate convert(String obj) throws ConversionException {
            if (Miscs.isBlank(obj)) {
                return null;
            } else {
                return Dates.toLocalDate(Dates.toDate(obj));
            }
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<LocalDate> getTargetClass() {
            return LocalDate.class;
        }
    }

    public static class DateToLocalDate implements SpecificConverter<java.sql.Date, LocalDate> {

        @Override
        public LocalDate convert(java.sql.Date obj) throws ConversionException {
            return Dates.toLocalDate(obj);
        }

        @Override
        public Class<java.sql.Date> getSourceClass() {
            return java.sql.Date.class;
        }

        @Override
        public Class<LocalDate> getTargetClass() {
            return LocalDate.class;
        }
    }

    public static class DateTimeToLocalDate implements SpecificConverter<Date, LocalDate> {

        @Override
        public LocalDate convert(java.util.Date obj) throws ConversionException {
            return Dates.toLocalDate(obj);
        }

        @Override
        public Class<java.util.Date> getSourceClass() {
            return java.util.Date.class;
        }

        @Override
        public Class<LocalDate> getTargetClass() {
            return LocalDate.class;
        }
    }

    public static class TimestampToLocalDate implements SpecificConverter<Timestamp, LocalDate> {

        @Override
        public LocalDate convert(Timestamp obj) throws ConversionException {
            return Dates.toLocalDate(obj);
        }

        @Override
        public Class<Timestamp> getSourceClass() {
            return Timestamp.class;
        }

        @Override
        public Class<LocalDate> getTargetClass() {
            return LocalDate.class;
        }
    }

    public static class LocalDateTimeToLocalDate implements SpecificConverter<LocalDateTime, LocalDate> {

        @Override
        public LocalDate convert(LocalDateTime obj) throws ConversionException {
            return Dates.toLocalDate(obj);
        }

        @Override
        public Class<LocalDateTime> getSourceClass() {
            return LocalDateTime.class;
        }

        @Override
        public Class<LocalDate> getTargetClass() {
            return LocalDate.class;
        }
    }
}
