package com.sprint.common.converter.util;

import com.sprint.common.converter.exception.ConvertErrorException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hongfeng.li
 * @since 2023/2/21
 */
public class Dates {

    public final static long ONE_DAY_NANO = 8640000000000L;
    public final static long ONE_DAY_MILLI = 86400000L;
    public final static long ONE_DAY_SECOND = 86400L;
    public final static String YEAR_PATTERN_1 = "yyyy";
    public final static String MONTH_PATTERN_2 = "yyyyMM";
    public final static String DATE_PATTERN_1 = "yyyyMMdd";
    public final static String DATE_PATTERN_HOUR_2 = "yyyyMMddHH";
    public final static String DATE_PATTERN_HOUR_MM_2 = "yyyyMMddHHmm";
    public final static String DATETIME_PATTERN_1 = "yyyyMMddHHmmss";

    private static final Map<String, ThreadLocal<SimpleDateFormat>> dateFormats = new ConcurrentHashMap<>();

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

    private static String getSimplePattern(String dateString) {
        int length = dateString.length();
        return length < 6 ? YEAR_PATTERN_1
                : length < 8 ? MONTH_PATTERN_2
                : length < 10 ? DATE_PATTERN_1
                : length < 12 ? DATE_PATTERN_HOUR_2
                : length < 14 ? DATE_PATTERN_HOUR_MM_2 : DATETIME_PATTERN_1;
    }

    public static Date toDate(String dateString) {
        if (Miscs.isBlank(dateString)) {
            return null;
        }
        dateString = removeDateStringSplit(dateString);
        try {
            return getDateFormat(getSimplePattern(dateString)).parse(dateString);
        } catch (ParseException e) {
            throw new ConvertErrorException(dateString, Date.class, e);
        }
    }

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

    public static LocalDateTime toLocalDateTime(String dateString) {
        if (Miscs.isBlank(dateString)) {
            return null;
        }
        return toLocalDateTime(toDate(dateString));
    }

    private static String removeDateStringSplit(String dateString) {
        return dateString.trim().replace(" ", "").replace("-", "").replace("/", "").replace(":", "").replace("年", "")
                .replace("月", "").replace("日", "").replace("时", "").replace("分", "").replace("秒", "").replace("T", "");
    }

    public static java.sql.Date toSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Date toSqlDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return java.sql.Date.valueOf(localDate);
    }

    public static java.sql.Date toSqlDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return java.sql.Date.valueOf(localDateTime.toLocalDate());
    }

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

    public static Year toYear(Date date) {
        if (date == null) {
            return null;
        }
        return Year.of(Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).getYear());
    }

    public static Year toYear(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Year.of(localDateTime.getYear());
    }

    public static Year toYear(String dateStr) {
        if (Miscs.isBlank(dateStr)) {
            return null;
        }
        return toYear(toLocalDateTime(dateStr));
    }

    public static YearMonth toYearMonth(Date date) {
        if (date == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault());
        return YearMonth.of(zonedDateTime.getYear(), zonedDateTime.getMonth());
    }
}
