package com.sprint.common.converter.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hongfeng.li
 * @since 2022/6/5
 */
public class Miscs {

    public static String lowerFirst(String str) {
        if (isBlank(str)) {
            return str;
        }
        // 同理
        char[] cs = str.toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

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

    private static final Pattern POINT_PATTERN = Pattern.compile("\\.(\\w)");

    /**
     * 获取数据元素
     *
     * @param ts           数组
     * @param i            位置
     * @param defaultValue 默认值
     * @param <T>          数组元素类型
     * @return 数组元素
     */
    public static <T> T at(T[] ts, int i, T defaultValue) {
        if (ts == null) {
            return defaultValue;
        }
        if (i < 0) {
            i += ts.length;
        }
        return ts.length > i ? ts[i] : defaultValue;
    }


    /**
     * 获取数据元素
     *
     * @param ts  数组
     * @param i   位置
     * @param <T> 数组元素类型
     * @return 数组元素
     */
    public static <T> T at(T[] ts, int i) {
        return at(ts, i, null);
    }

    /**
     * 是否包含
     *
     * @param ts 数组
     * @param t  元素
     * @return 是否包含
     */
    public static boolean contained(Object[] ts, String t) {
        if (ts != null && t != null) {
            for (Object obj : ts) {
                if (obj != null && obj.equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 集合大小
     *
     * @param collection 数组
     * @return 数组元素
     */
    public static int size(Collection<?> collection) {
        if (collection == null) {
            return 0;
        }
        return collection.size();
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

    public static final int BUFFER_SIZE = 4096;

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Leaves both streams open when done.
     *
     * @param in  the InputStream to copy from
     * @param out the OutputStream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");

        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }
}
