package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Dates;
import com.sprint.common.converter.util.Miscs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;

/**
 * 年月转化
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class YearMonthConverters implements SpecificConverterLoader {


    public static class LocalDateToYearMonth implements SpecificConverter<LocalDate, YearMonth> {

        @Override
        public YearMonth convert(LocalDate source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return YearMonth.from(source);
        }

        @Override
        public Class<LocalDate> getSourceClass() {
            return LocalDate.class;
        }

        @Override
        public Class<YearMonth> getTargetClass() {
            return YearMonth.class;
        }
    }

    public static class LocalDateTimeToYearMonth implements SpecificConverter<LocalDateTime, YearMonth> {

        @Override
        public YearMonth convert(LocalDateTime source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return YearMonth.from(source);
        }

        @Override
        public Class<LocalDateTime> getSourceClass() {
            return LocalDateTime.class;
        }

        @Override
        public Class<YearMonth> getTargetClass() {
            return YearMonth.class;
        }
    }

    public static class DateTimeToYearMonth implements SpecificConverter<Date, YearMonth> {

        @Override
        public YearMonth convert(Date source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Dates.toYearMonth(source);
        }

        @Override
        public Class<Date> getSourceClass() {
            return Date.class;
        }

        @Override
        public Class<YearMonth> getTargetClass() {
            return YearMonth.class;
        }

    }

    public static class DateToYearMonth implements SpecificConverter<java.sql.Date, YearMonth> {

        @Override
        public YearMonth convert(java.sql.Date source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Dates.toYearMonth(source);
        }

        @Override
        public Class<java.sql.Date> getSourceClass() {
            return java.sql.Date.class;
        }

        @Override
        public Class<YearMonth> getTargetClass() {
            return YearMonth.class;
        }
    }

    public static class TimestampToYearMonth implements SpecificConverter<java.sql.Timestamp, YearMonth> {

        @Override
        public YearMonth convert(java.sql.Timestamp source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Dates.toYearMonth(source);
        }

        @Override
        public Class<java.sql.Timestamp> getSourceClass() {
            return java.sql.Timestamp.class;
        }

        @Override
        public Class<YearMonth> getTargetClass() {
            return YearMonth.class;
        }
    }

    public static class LongToYearMonth implements SpecificConverter<Long, YearMonth> {

        @Override
        public YearMonth convert(Long source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return Dates.toYearMonth(new Date(source));
        }

        @Override
        public Class<Long> getSourceClass() {
            return Long.class;
        }

        @Override
        public Class<YearMonth> getTargetClass() {
            return YearMonth.class;
        }
    }

    public static class StringToYear implements SpecificConverter<String, YearMonth> {

        @Override
        public YearMonth convert(String source) throws ConversionException {
            if (Miscs.isBlank(source)) {
                return null;
            }
            return Dates.toYearMonth(Dates.toDate(source));
        }

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<YearMonth> getTargetClass() {
            return YearMonth.class;
        }
    }
}
