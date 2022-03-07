package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 时间转换
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class LocalTimeConverters implements SpecificConverterLoader {

    public static LocalTime toLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault());
        return zonedDateTime.toLocalDateTime().toLocalTime();
    }

    public static LocalTime toLocalTime(Long date) {
        if (date == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault());
        return zonedDateTime.toLocalDateTime().toLocalTime();
    }

    public static class NumberToLocalTime implements SpecificConverter<Number, LocalTime> {

        @Override
        public LocalTime convert(Number source) throws ConversionException {
            if (source == null) {
                return null;
            }

            long ts = source.longValue();

            if (ts > 8640000000000L) {
                return LocalTime.ofNanoOfDay(ts);
            } else if (ts > 86400000L) {
                return toLocalTime(ts);
            } else if (ts > 86400L) {
                return LocalTime.ofNanoOfDay(ts * 1000000L);
            } else {
                return LocalTime.ofSecondOfDay(ts);
            }
        }

        @Override
        public Class<Number> getSourceClass() {
            return Number.class;
        }

        @Override
        public Class<LocalTime> getTargetClass() {
            return LocalTime.class;
        }
    }

    public static class TemporalAccessorToLocalTime implements SpecificConverter<TemporalAccessor, LocalTime> {

        @Override
        public LocalTime convert(TemporalAccessor source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return LocalTime.from(source);
        }

        @Override
        public Class<TemporalAccessor> getSourceClass() {
            return TemporalAccessor.class;
        }

        @Override
        public Class<LocalTime> getTargetClass() {
            return LocalTime.class;
        }
    }

    public static class DateTimeToLocalTime implements SpecificConverter<Date, LocalTime> {

        @Override
        public LocalTime convert(Date source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return toLocalTime(source);
        }

        @Override
        public Class<Date> getSourceClass() {
            return Date.class;
        }

        @Override
        public Class<LocalTime> getTargetClass() {
            return LocalTime.class;
        }
    }

    public static class StringToLocalTime implements SpecificConverter<String, LocalTime> {

        @Override
        public LocalTime convert(String source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return LocalTime.parse(source);
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<LocalTime> getTargetClass() {
            return LocalTime.class;
        }
    }
}
