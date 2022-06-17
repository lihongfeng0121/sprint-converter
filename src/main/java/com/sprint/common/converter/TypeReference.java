package com.sprint.common.converter;

import com.sprint.common.converter.util.ParameterizedTypeImpl;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 类型引用
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2021年03月29日
 */
public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {

    /*
     * Map<String, Object>引用的类型
     */
    public static final TypeReference<Map<String, Object>> STR_OBJ_MAP = new TypeReference<Map<String, Object>>() {
    };

    /*
     * List<Map<String, Object>>引用的类型
     */
    public static final TypeReference<List<Map<String, Object>>> STR_OBJ_MAP_LIST = new TypeReference<List<Map<String, Object>>>() {
    };
    /*
     * List<String>引用的类型
     */
    public static final TypeReference<List<String>> STR_LIST = new TypeReference<List<String>>() {
    };
    /*
     * List<Integer>引用的类型
     */
    public static final TypeReference<List<Integer>> INT_LIST = new TypeReference<List<Integer>>() {
    };
    /*
     * List<Double>引用的类型
     */
    public static final TypeReference<List<Double>> DOUBLE_LIST = new TypeReference<List<Double>>() {
    };
    /*
     * List<Object>引用的类型
     */
    public static final TypeReference<List<Object>> OBJ_LIST = new TypeReference<List<Object>>() {
    };
    /*
     * Set<String>引用的类型
     */
    public static final TypeReference<Set<String>> STR_SET = new TypeReference<Set<String>>() {
    };
    /*
     * Set<Integer>引用的类型
     */
    public static final TypeReference<Set<Integer>> INT_SET = new TypeReference<Set<Integer>>() {
    };
    /*
     * Set<Double>引用的类型
     */
    public static final TypeReference<Set<Double>> DOUBLE_SET = new TypeReference<Set<Double>>() {
    };
    /*
     * Set<Object>引用的类型
     */
    public static final TypeReference<Set<Object>> OBJ_SET = new TypeReference<Set<Object>>() {
    };

    /**
     * @param keyType   参数类型
     * @param valueType 参数类型
     * @param <K>       参数类型
     * @param <V>       参数类型
     * @return Map 引用的类型
     */
    public static <K, V> TypeReference<Map<K, V>> mapType(Class<K> keyType, Class<V> valueType) {
        return new TypeReference<Map<K, V>>() {
            @Override
            public Type getType() {
                return ParameterizedTypeImpl.make(Map.class, new Type[]{keyType, valueType}, null);
            }
        };
    }

    /**
     * @param valueType 参数类型
     * @param <V>       参数类型
     * @return List 引用的类型
     */
    public static <V> TypeReference<List<V>> listType(Class<V> valueType) {
        return new TypeReference<List<V>>() {
            @Override
            public Type getType() {
                return ParameterizedTypeImpl.make(List.class, new Type[]{valueType}, null);
            }
        };
    }

    /**
     * @param valueType 参数类型
     * @param <V>       参数类型
     * @return Set 引用的类型
     */
    public static <V> TypeReference<Set<V>> setType(Class<V> valueType) {
        return new TypeReference<Set<V>>() {
            @Override
            public Type getType() {
                return ParameterizedTypeImpl.make(Set.class, new Type[]{valueType}, null);
            }
        };
    }

    private final Type type;

    protected TypeReference() {
        this.type = Types.getClassSuperclassType(getClass(), 0);
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int compareTo(TypeReference<T> o) {
        return 0;
    }
}