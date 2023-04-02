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
 * 时间戳转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class TimestampConverters implements SpecificConverterLoader {

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
            if (Strings.isBlank(obj)) {
                return null;
            } else {
                return Dates.toTimestamp(Dates.toDate(obj));
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
            return Dates.toTimestamp(obj);
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
            return Dates.toTimestamp(obj);
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
            return Dates.toTimestamp(obj);
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