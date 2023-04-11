package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Dates;
import com.sprint.common.converter.util.Strings;

import java.math.BigInteger;
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
public class DateTimeConverters implements SpecificConverterLoader {

    public static class LongToDateTime implements SpecificConverter<Long, Date> {

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

    public static class BigIntegerToDateTime implements SpecificConverter<BigInteger, Date> {

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

    public static class StringToDateTime implements SpecificConverter<String, Date> {

        @Override
        public Date convert(String obj) throws ConversionException {
            if (Strings.isBlank(obj)) {
                return null;
            } else {
                return Dates.toDate(obj);
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

    public static class LocalDateToDateTime implements SpecificConverter<LocalDate, Date> {

        @Override
        public Date convert(LocalDate obj) throws ConversionException {
            return Dates.toDate(obj);
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

    public static class LocalDateTimeToDateTime implements SpecificConverter<LocalDateTime, Date> {

        @Override
        public Date convert(LocalDateTime obj) throws ConversionException {
            return Dates.toDate(obj);
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
