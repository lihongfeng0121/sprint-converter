package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.ConvertErrorException;
import com.sprint.common.converter.util.Miscs;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时间转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class DateTimeConverters implements SpecificConverterLoader {

    public final static String YEAR_PATTERN_1 = "yyyy";

    public final static String MONTH_PATTERN_2 = "yyyyMM";

    public final static String DATE_PATTERN_1 = "yyyyMMdd";

    public final static String DATE_PATTERN_HOUR_2 = "yyyyMMddHH";

    public final static String DATE_PATTERN_HOUR_MM_2 = "yyyyMMddHHmm";

    public final static String DATETIME_PATTERN_1 = "yyyyMMddHHmmss";

    private static final Map<String, ThreadLocal<SimpleDateFormat>> dateFormats = new ConcurrentHashMap<>();

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(String dateString) throws ParseException {
        if (dateString == null || dateString.length() == 0) {
            return null;
        }

        dateString = removeDateStringSplit(dateString);

        return getDateFormat(getSimplePattern(dateString)).parse(dateString);
    }

    public static SimpleDateFormat getDateFormat(String pattern) {
        ThreadLocal<SimpleDateFormat> local = dateFormats.get(pattern);
        if (local == null) {
            local = new ThreadLocal<>();
            dateFormats.put(pattern, local);
        }
        SimpleDateFormat format = local.get();
        if (format == null) {
            format = new SimpleDateFormat(pattern);
            local.set(format);
        }
        return format;
    }

    private static String removeDateStringSplit(String dateString) {
        return dateString.trim().replace(" ", "").replace("-", "").replace("/", "").replace(":", "").replace("年", "")
                .replace("月", "").replace("日", "").replace("时", "").replace("分", "").replace("秒", "").replace("T", "");
    }

    private static String getSimplePattern(String dateString) {
        int length = dateString.length();
        String pattern = length < 6 ? YEAR_PATTERN_1
                : length < 8 ? MONTH_PATTERN_2
                : length < 10 ? DATE_PATTERN_1
                : length < 12 ? DATE_PATTERN_HOUR_2
                : length < 14 ? DATE_PATTERN_HOUR_MM_2 : DATETIME_PATTERN_1;
        return pattern;
    }

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
            if (Miscs.isBlank(obj)) {
                return null;
            } else {
                try {
                    return toDate(obj);
                } catch (ParseException ex) {
                    throw new ConvertErrorException(obj, getTargetClass(), ex);
                }
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
            return toDate(obj);
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
            return toDate(obj);
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
