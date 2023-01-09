package com.sprint.common.converter.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hongfeng.li
 * @since 2022/6/5
 */
class Miscs {

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

    /**
     * convert to map
     *
     * @param keys keys
     * @param vals vals
     * @param <K>  key类型
     * @param <V>  val 类型
     * @return map
     */
    public static <K, V> Map<K, V> toMap(K[] keys, V[] vals) {
        return toMap(new LinkedHashMap<>(), keys, vals);
    }


    private static <K, V> Map<K, V> toMap(Map<K, V> map, K[] keys, V[] vals) {
        if (keys.length != vals.length) {
            throw new IllegalArgumentException("keys/vals length not equal");
        }

        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], vals[i]);
        }

        return map;
    }
}
