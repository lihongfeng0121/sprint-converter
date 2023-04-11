package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Dates;
import com.sprint.common.converter.util.Strings;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 时间转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class DateConverters implements SpecificConverterLoader {

    public DateConverters() {
    }

    public static class LongToDate implements SpecificConverter<Long, Date> {

        @Override
        public Date convert(Long obj) throws ConversionException {
            return obj == null ? null : new Date(obj);
        }

        @Override
        public Class<Long> getSourceClass() {
            return Long.class;
        }

        @Override
        public Class<Date> getTargetClass() {
            return Date.class;
        }
    }

    public static class BigIntegerToDate implements SpecificConverter<BigInteger, Date> {

        @Override
        public Date convert(BigInteger obj) throws ConversionException {
            return obj == null ? null : new Date(obj.longValue());
        }

        @Override
        public Class<BigInteger> getSourceClass() {
            return BigInteger.class;
        }

        @Override
        public Class<Date> getTargetClass() {
            return Date.class;
        }
    }

    public static class StringToDate implements SpecificConverter<String, Date> {

        @Override
        public Date convert(String obj) throws ConversionException {
            if (Strings.isBlank(obj)) {
                return null;
            } else {
                return Dates.toSqlDate(Dates.toDate(obj));
            }
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Date> getTargetClass() {
            return Date.class;
        }
    }

    public static class DateTimeToDate implements SpecificConverter<java.util.Date, Date> {

        @Override
        public Date convert(java.util.Date obj) throws ConversionException {
            if (obj == null) {
                return null;
            }
            return Dates.toSqlDate(obj);
        }

        @Override
        public Class<java.util.Date> getSourceClass() {
            return java.util.Date.class;
        }

        @Override
        public Class<Date> getTargetClass() {
            return Date.class;
        }
    }

    public static class TimestampToDate implements SpecificConverter<Timestamp, Date> {

        @Override
        public Date convert(Timestamp obj) throws ConversionException {
            return Dates.toSqlDate(obj);
        }

        @Override
        public Class<Timestamp> getSourceClass() {
            return Timestamp.class;
        }

        @Override
        public Class<Date> getTargetClass() {
            return Date.class;
        }
    }

    public static class LocalDateToDate implements SpecificConverter<LocalDate, Date> {

        @Override
        public Date convert(LocalDate obj) throws ConversionException {
            return Dates.toSqlDate(obj);
        }

        @Override
        public Class<LocalDate> getSourceClass() {
            return LocalDate.class;
        }

        @Override
        public Class<Date> getTargetClass() {
            return Date.class;
        }
    }

    public static class LocalDateTimeToDate implements SpecificConverter<LocalDateTime, Date> {

        @Override
        public Date convert(LocalDateTime obj) throws ConversionException {
            return Dates.toSqlDate(obj);
        }

        @Override
        public Class<LocalDateTime> getSourceClass() {
            return LocalDateTime.class;
        }

        @Override
        public Class<Date> getTargetClass() {
            return Date.class;
        }
    }
}
