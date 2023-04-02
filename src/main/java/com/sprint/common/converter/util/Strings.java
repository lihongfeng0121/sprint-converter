package com.sprint.common.converter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hongfeng.li
 * @since 2023/4/2
 */
public class Strings {

    private static final Pattern POINT_PATTERN = Pattern.compile("\\.(\\w)");

    /**
     * 首字母小写
     *
     * @param str str
     * @return str.
     */
    public static String lowerFirst(String str) {
        if (isBlank(str)) {
            return str;
        }
        // 同理
        char[] cs = str.toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

    /**
     * 是否是空
     *
     * @param cs cs
     * @return 是否是空.
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否不是空
     *
     * @param cs cs
     * @return 是否不是空.
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }


    /**
     * 点转驼峰
     *
     * @param source 源
     * @return 驼峰
     */
    public static String pointToCamel(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }
        Matcher matcher = POINT_PATTERN.matcher(source);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
