package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Dates;
import com.sprint.common.converter.util.Miscs;
import com.sprint.common.converter.util.Strings;

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
public class LocalDateTimeConverters implements SpecificConverterLoader {

    public static class NumberToLocalDateTime implements SpecificConverter<Number, LocalDateTime> {

        @Override
        public LocalDateTime convert(Number obj) throws ConversionException {
            return obj == null ? null : Dates.toLocalDateTime(obj.longValue());
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<LocalDateTime> getTargetClass() {
            return LocalDateTime.class;
        }
    }

    public static class StringToLocalDateTime implements SpecificConverter<String, LocalDateTime> {

        @Override
        public LocalDateTime convert(String obj) throws ConversionException {
            if (Strings.isBlank(obj)) {
                return null;
            } else {
                return Dates.toLocalDateTime(Dates.toDate(obj));
            }
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<LocalDateTime> getTargetClass() {
            return LocalDateTime.class;
        }
    }

    public static class DateToLocalDateTime implements SpecificConverter<java.sql.Date, LocalDateTime> {

        @Override
        public LocalDateTime convert(java.sql.Date obj) throws ConversionException {
            return Dates.toLocalDateTime(obj);
        }

        @Override
        public Class<java.sql.Date> getSourceClass() {
            return java.sql.Date.class;
        }

        @Override
        public Class<LocalDateTime> getTargetClass() {
            return LocalDateTime.class;
        }
    }

    public static class DateTimeToLocalDateTime implements SpecificConverter<Date, LocalDateTime> {

        @Override
        public LocalDateTime convert(Date obj) throws ConversionException {
            return Dates.toLocalDateTime(obj);
        }

        @Override
        public Class<Date> getSourceClass() {
            return Date.class;
        }

        @Override
        public Class<LocalDateTime> getTargetClass() {
            return LocalDateTime.class;
        }
    }

    public static class TimestampToLocalDateTime implements SpecificConverter<Timestamp, LocalDateTime> {

        @Override
        public LocalDateTime convert(Timestamp obj) throws ConversionException {
            return Dates.toLocalDateTime(obj);
        }

        @Override
        public Class<Timestamp> getSourceClass() {
            return Timestamp.class;
        }

        @Override
        public Class<LocalDateTime> getTargetClass() {
            return LocalDateTime.class;
        }
    }

    public static class LocalDateToLocalDateTime implements SpecificConverter<LocalDate, LocalDateTime> {

        @Override
        public LocalDateTime convert(LocalDate obj) throws ConversionException {
            return Dates.toLocalDateTime(obj);
        }

        @Override
        public Class<LocalDate> getSourceClass() {
            return LocalDate.class;
        }

        @Override
        public Class<LocalDateTime> getTargetClass() {
            return LocalDateTime.class;
        }
    }
}
