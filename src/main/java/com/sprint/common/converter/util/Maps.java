package com.sprint.common.converter.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author hongfeng.li
 * @since 2023/3/9
 */
public class Maps {


    /**
     * 是否不为空
     *
     * @param map map
     * @return 返回是否不为空
     */
    public static boolean isNotEmpty(Map map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 是否为空
     *
     * @param map map
     * @return 返回是否为空
     */
    public static boolean isEmpty(Map map) {
        return !isNotEmpty(map);
    }

    /**
     * 重写map的key，val
     *
     * @param map       源map
     * @param keyWriter key重写方法
     * @param valWriter val重写方法
     * @param <K>       源Map key的值类型
     * @param <V>       源Map val的值类型
     * @param <K2>      目标Map的key值类型
     * @param <V2>      目标Map的val值类型
     * @return 返回重写k/v后的Map
     */
    public static <K, V, K2, V2> Map<K2, V2> rewrite(Map<K, V> map, Function<K, K2> keyWriter,
                                                     Function<V, V2> valWriter) {

        Map<K2, V2> rewrite;

        if (Types.getConstructorIfAvailable(map.getClass()) != null) {
            rewrite = Beans.instanceMap(map.getClass());
        } else if (map instanceof HashMap) {
            rewrite = new HashMap<>(map.size());
        } else if (map instanceof ConcurrentHashMap) {
            rewrite = new ConcurrentHashMap<>(map.size());
        } else {
            rewrite = new LinkedHashMap<>(map.size());
        }

        map.forEach((key, val) -> rewrite.put(keyWriter.apply(key), valWriter.apply(val)));

        return rewrite;
    }

    /**
     * 重写Map的值
     *
     * @param map       源map
     * @param keyWriter key重写方法
     * @param <K>       源Map key的范型
     * @param <K2>      目标Map的key值
     * @param <V>       源Map val的范型
     * @return rewrite key map.
     */
    public static <K, V, K2> Map<K2, V> rewriteKey(Map<K, V> map, Function<K, K2> keyWriter) {
        Map<K2, V> rewrite;
        if (Types.getConstructorIfAvailable(map.getClass()) != null) {
            rewrite = Beans.instanceMap(map.getClass());
        } else if (map instanceof HashMap) {
            rewrite = new HashMap<>(map.size());
        } else if (map instanceof ConcurrentHashMap) {
            rewrite = new ConcurrentHashMap<>(map.size());
        } else {
            rewrite = new LinkedHashMap<>(map.size());
        }

        map.forEach((key, val) -> rewrite.put(keyWriter.apply(key), val));

        return rewrite;
    }

    /**
     * 重写Map的值
     *
     * @param map       源map
     * @param valWriter val重写方法
     * @param <K>       源Map key的范型
     * @param <V>       源Map val的范型
     * @param <V2>      目标Map的Val值
     * @return 返回重写Map val后的map
     */
    public static <K, V, V2> Map<K, V2> rewriteVal(Map<K, V> map, Function<V, V2> valWriter) {
        Map<K, V2> rewrite;

        if (Types.getConstructorIfAvailable(map.getClass()) != null) {
            rewrite = Beans.instanceMap(map.getClass());
        } else if (map instanceof HashMap) {
            rewrite = new HashMap<>(map.size());
        } else if (map instanceof ConcurrentHashMap) {
            rewrite = new ConcurrentHashMap<>(map.size());
        } else {
            rewrite = new LinkedHashMap<>(map.size());
        }

        map.forEach((key, val) -> rewrite.put(key, valWriter.apply(val)));

        return rewrite;
    }

    /**
     * 翻转K-V
     *
     * @param map 源map
     * @param <K> key type
     * @param <V> value type
     * @return 返回翻转后的map
     */
    public static <K, V> Map<V, K> reverse(Map<K, V> map) {
        Map<V, K> reversed;
        if (Types.getConstructorIfAvailable(map.getClass()) != null) {
            reversed = Beans.instanceMap(map.getClass());
        } else if (map instanceof HashMap) {
            reversed = new HashMap<>(map.size());
        } else if (map instanceof ConcurrentHashMap) {
            reversed = new ConcurrentHashMap<>(map.size());
        } else {
            reversed = new LinkedHashMap<>(map.size());
        }

        map.forEach((key, val) -> {
            reversed.put(val, key);
        });
        return reversed;
    }

    /**
     * 合并map
     *
     * @param maps 源maps
     * @param <K>  key type
     * @param <V>  value type
     * @return 返回合并后的map
     */
    public static <K, V> Map<K, V> merge(Map<K, V>... maps) {
        Map<K, V> merged;
        if (Types.getConstructorIfAvailable(maps[0].getClass()) != null) {
            merged = Beans.instanceMap(maps[0].getClass());
        } else if (maps[0] instanceof HashMap) {
            merged = new HashMap<>();
        } else if (maps[0] instanceof ConcurrentHashMap) {
            merged = new ConcurrentHashMap<>();
        } else {
            merged = new LinkedHashMap<>();
        }

        for (Map<K, V> map : maps) {
            if (isNotEmpty(map)) {
                merged.putAll(map);
            }
        }
        return merged;
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
        return toMap(new LinkedHashMap<>(keys.length), keys, vals);
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

    public static <V, V1 extends V> Map<String, V> toMap(String name1, V1 value1) {
        return populateMap(new HashMap<>(1), name1, value1);
    }

    public static <V, V1 extends V, V2 extends V> Map<String, V> toMap(String name1, V1 value1,
                                                                       String name2, V2 value2) {
        return populateMap(new LinkedHashMap<>(2), name1, value1, name2, value2);
    }

    public static <V, V1 extends V, V2 extends V, V3 extends V> Map<String, V> toMap(String name1, V1 value1,
                                                                                     String name2, V2 value2,
                                                                                     String name3, V3 value3) {
        return populateMap(new LinkedHashMap<>(3), name1, value1, name2, value2, name3, value3);
    }

    private static <V> Map<String, V> populateMap(Map<String, V> map, Object... data) {
        int i = 0;
        while (i < data.length) {
            map.put((String) data[i++], (V) data[i++]);
        }
        return map;
    }
}
