package com.sprint.common.converter.conversion.nested.json.databind;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author hongfeng.li
 * @since 2023/7/27
 */
public class JacksonDateFormat extends DateFormat {

    public static final String PATTERN_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final String PATTERN_YYYYMMDDHHMMSSSSS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String PATTERN_YYYYMMDD = "yyyy-MM-dd";

    public static final String PATTERN_HHMMSS = "HH:mm:ss";

    public static final String PATTERN2_HHMMSSSSS = "HH:mm:ss.SSS";

    public final static String PATTERN_YYYYMMDDHHMMSSSSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static final String[] patterns = new String[] {
            PATTERN_YYYYMMDDHHMMSS, PATTERN_YYYYMMDDHHMMSSSSS, PATTERN_YYYYMMDD, PATTERN_HHMMSS, PATTERN2_HHMMSSSSS, PATTERN_YYYYMMDDHHMMSSSSSZ
    };

    private static final ThreadLocal<SimpleDateFormat> dateFormats = ThreadLocal.withInitial(() -> new SimpleDateFormat());

    private static Calendar CALENDAR = new GregorianCalendar();

    private static NumberFormat NUMBER_FORMAT = new DecimalFormat();

    private final String formatPattern;

    public JacksonDateFormat(String formatPattern) {
        this.numberFormat = NUMBER_FORMAT;
        this.calendar = CALENDAR;
        this.formatPattern = formatPattern;
    }

    public static Date parseDateStrictly(String str, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, parsePatterns);
    }

    private static Date parseDateWithLeniency(
            String str, String[] parsePatterns) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        SimpleDateFormat parser = dateFormats.get();
        parser.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        for (String parsePattern : parsePatterns) {

            String pattern = parsePattern;

            // LANG-530 - need to make sure 'ZZ' output doesn't get passed to SimpleDateFormat
            if (parsePattern.endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }

            parser.applyPattern(pattern);
            pos.setIndex(0);

            String str2 = str;
            // LANG-530 - need to make sure 'ZZ' output doesn't hit SimpleDateFormat as it will ParseException
            if (parsePattern.endsWith("ZZ")) {
                str2 = str.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
            }

            Date date = parser.parse(str2, pos);
            if (date != null && pos.getIndex() == str2.length()) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        SimpleDateFormat format = dateFormats.get();
        format.setLenient(false);
        format.applyPattern(formatPattern);
        return format.format(date, toAppendTo, fieldPosition);
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        try {
            return parse(source);
        }
        catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Date parse(String source) throws ParseException {
        return parseDateStrictly(source, patterns);
    }

    @Override
    public Object clone() {
        return this;
    }

    @Override
    public String toString() {
        return getClass().getName();
    }
}
