package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author hongfeng-li
 * @version 1.0
 * @title LocalDateConverters
 * @desc 时间转换器
 * @date 2019年12月25日
 */
public class LocalDateConverters implements SpecificConverterLoader {

    public static Long toTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate toLocalDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }

    public static LocalDate toLocalDate(Long ts) {
        if (ts == null) {
            return null;
        }
        return Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static class NumberToLocalDate implements SpecificConverter<Number, LocalDate> {

        @Override
        public LocalDate convert(Number obj) throws ConversionException {
            return obj == null ? null : toLocalDate(obj.longValue());
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
            if (obj == null) {
                return null;
            } else {
                try {
                    return toLocalDate(DateTimeConverters.toDate(obj));
                } catch (ParseException ex) {
                    throw new ConversionException(ex);
                }
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
            return toLocalDate(obj);
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
            return toLocalDate(obj);
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
            return toLocalDate(obj);
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
            return toLocalDate(obj);
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
