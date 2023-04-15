package com.sprint.common.converter.util;

import java.util.*;

/**
 * @author hongfeng.li
 * @since 2023/2/14
 */
public class Defaults {

    private Defaults() {
    }

    private static final Map<Class<?>, Object> DEFAULTS;

    static {
        // Only add to this map via put(Map, Class<T>, T)
        Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
        put(map, boolean.class, false);
        put(map, char.class, '\0');
        put(map, byte.class, (byte) 0);
        put(map, short.class, (short) 0);
        put(map, int.class, 0);
        put(map, long.class, 0L);
        put(map, float.class, 0f);
        put(map, double.class, 0d);
        put(map, Optional.class, Optional.empty());
        put(map, ObjectValue.class, ObjectValue.empty());
        DEFAULTS = Collections.unmodifiableMap(map);
    }

    private static <T> void put(Map<Class<?>, Object> map, Class<T> type, T value) {
        map.put(type, value);
    }

    /**
     * Returns the default value of {@code type} as defined by JLS --- {@code 0} for numbers, {@code
     * false} for {@code boolean} and {@code '\0'} for {@code char}. For non-primitive types and
     * {@code void}, {@code null} is returned.
     *
     * @param type 类型
     * @param <T>  t
     * @return default value.
     */
    public static <T> T defaultValue(Class<T> type) {
        // Primitives.wrap(type).cast(...) would avoid the warning, but we can't use that from here
        @SuppressWarnings("unchecked") // the put method enforces this key-value relationship
        T t = (T) DEFAULTS.get(Objects.requireNonNull(type));
        return t;
    }
}
