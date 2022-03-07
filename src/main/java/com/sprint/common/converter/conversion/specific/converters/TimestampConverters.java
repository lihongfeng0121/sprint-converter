package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 时间戳转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class TimestampConverters implements SpecificConverterLoader {

    public static Timestamp toTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    public static Timestamp toTimestamp(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Timestamp.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static class NumberToTimestamp implements SpecificConverter<Number, Timestamp> {

        @Override
        public Timestamp convert(Number obj) throws ConversionException {
            return obj == null ? null : new Timestamp(obj.longValue());
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<Timestamp> getTargetClass() {
            return Timestamp.class;
        }
    }

    public static class StringToTimestamp implements SpecificConverter<String, Timestamp> {

        @Override
        public Timestamp convert(String obj) throws ConversionException {
            if (obj == null) {
                return null;
            } else {
                try {
                    return toTimestamp(DateTimeConverters.toDate(obj));
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
        public Class<Timestamp> getTargetClass() {
            return Timestamp.class;
        }
    }

    public static class DateTimeToTimestamp implements SpecificConverter<Date, Timestamp> {

        @Override
        public Timestamp convert(Date obj) throws ConversionException {
            return toTimestamp(obj);
        }

        @Override
        public Class<Date> getSourceClass() {
            return Date.class;
        }

        @Override
        public Class<Timestamp> getTargetClass() {
            return Timestamp.class;
        }
    }

    public static class LocalDateToTimestamp implements SpecificConverter<LocalDate, Timestamp> {

        @Override
        public Timestamp convert(LocalDate obj) throws ConversionException {
            return toTimestamp(obj);
        }

        @Override
        public Class<LocalDate> getSourceClass() {
            return LocalDate.class;
        }

        @Override
        public Class<Timestamp> getTargetClass() {
            return Timestamp.class;
        }
    }

    public static class LocalDateTimeToTimestamp implements SpecificConverter<LocalDateTime, Timestamp> {

        @Override
        public Timestamp convert(LocalDateTime obj) throws ConversionException {
            return toTimestamp(obj);
        }

        @Override
        public Class<LocalDateTime> getSourceClass() {
            return LocalDateTime.class;
        }

        @Override
        public Class<Timestamp> getTargetClass() {
            return Timestamp.class;
        }
    }
}