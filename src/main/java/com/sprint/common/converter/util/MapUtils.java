package com.sprint.common.converter.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author hongfeng.li
 * @since 2022/6/5
 */
public class MapUtils {

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

    static <K, V> Map<K, V> toMap(Map<K, V> map, K[] keys, V[] vals) {
        if (keys.length != vals.length) {
            throw new IllegalArgumentException("keys/vals length not equal");
        }

        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], vals[i]);
        }

        return map;
    }
}
