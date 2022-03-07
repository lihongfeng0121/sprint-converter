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
 * 时间转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class LocalDateTimeConverters implements SpecificConverterLoader {

    /**
     * 转时间戳
     *
     * @param localDateTime LocalDateTime
     * @return ts
     */
    public static Long toTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    /**
     * 转时间
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 转时间
     *
     * @param localDate LocalDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay();
    }

    /**
     * 时间戳转时间
     *
     * @param ts 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Long ts) {
        if (ts == null) {
            return null;
        }
        return Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static class NumberToLocalDateTime implements SpecificConverter<Number, LocalDateTime> {

        @Override
        public LocalDateTime convert(Number obj) throws ConversionException {
            return obj == null ? null : toLocalDateTime(obj.longValue());
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
            if (obj == null) {
                return null;
            } else {
                try {
                    return toLocalDateTime(DateTimeConverters.toDate(obj));
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
        public Class<LocalDateTime> getTargetClass() {
            return LocalDateTime.class;
        }
    }

    public static class DateToLocalDateTime implements SpecificConverter<java.sql.Date, LocalDateTime> {

        @Override
        public LocalDateTime convert(java.sql.Date obj) throws ConversionException {
            return toLocalDateTime(obj);
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
            return toLocalDateTime(obj);
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
            return toLocalDateTime(obj);
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
            return toLocalDateTime(obj);
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
