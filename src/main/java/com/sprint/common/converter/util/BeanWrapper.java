package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.TypeReference;
import com.sprint.common.converter.conversion.nested.bean.Beans;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author hongfeng.li
 * @since 2022/3/17
 */
public class BeanWrapper {

    private final Object obj;

    BeanWrapper(Object obj) {
        this.obj = obj;
    }

    /**
     * 包装Map 为MapWrapper
     *
     * @param obj 源obj
     * @return 返回被包装后的BeanWrapper
     */
    public static BeanWrapper of(Object obj) {
        Assert.notNull(obj, "obj can't be null");
        Assert.isTrue(Types.isBean(obj.getClass()) || Types.isMap(obj.getClass()), "obj mast be bean or map");
        return new BeanWrapper(obj);
    }

    /**
     * 包装Map 为MapWrapper
     *
     * @param obj 源obj
     * @return 返回被包装后的BeanWrapper
     */
    public static BeanWrapper ofNullable(Object obj) {
        if (obj != null) {
            Assert.isTrue(Types.isBean(obj.getClass()) || Types.isMap(obj.getClass()), "obj mast be bean or map");
        }
        return new BeanWrapper(obj);
    }

    /**
     * 获取bean
     *
     * @return
     */
    public Object get() {
        return obj;
    }

    /**
     * 获取bean 属性
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return Beans.getProperty(obj, key);
    }

    /**
     * 获取bean 属性
     *
     * @param key 属性
     * @return 属性值
     */
    public Object safeGet(String key) {
        if (obj == null) {
            return null;
        }
        return Beans.getProperty(obj, key);
    }

    private Object doGet(String key, Type type, Supplier<Object> defaultSupplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(defaultSupplier);
    }

    private Object doSafeGet(String key, Type type, Supplier<Object> defaultSupplier) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(defaultSupplier);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        return MiscUtils.cast(doGet(key, ParameterizedTypeImpl.make(List.class, new Type[]{clazz}, null), () -> null));
    }

    public <T> List<T> safeGetList(String key, Class<T> clazz) {
        return MiscUtils.cast(doSafeGet(key, ParameterizedTypeImpl.make(List.class, new Type[]{clazz}, null), () -> null));
    }

    public <T> Set<T> getSet(String key, Class<T> clazz) {
        return MiscUtils.cast(doGet(key, ParameterizedTypeImpl.make(Set.class, new Type[]{clazz}, null), () -> null));
    }

    public <T> Set<T> safeGetSet(String key, Class<T> clazz) {
        return MiscUtils.cast(doSafeGet(key, ParameterizedTypeImpl.make(Set.class, new Type[]{clazz}, null), () -> null));
    }

    public <K, V> Map<K, V> getMap(String key, Class<K> keyClazz, Class<V> valClazz) {
        return MiscUtils.cast(doGet(key, ParameterizedTypeImpl.make(Map.class, new Type[]{keyClazz, valClazz}, null), () -> null));
    }

    public <K, V> Map<K, V> safeGetMap(String key, Class<K> keyClazz, Class<V> valClazz) {
        return MiscUtils.cast(doSafeGet(key, ParameterizedTypeImpl.make(Map.class, new Type[]{keyClazz, valClazz}, null), () -> null));
    }

    public <T> T get(String key, Class<T> clazz, Supplier<T> defaultSupplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, clazz)).orElseGet(defaultSupplier);
    }

    public <T> T safeGet(String key, Class<T> clazz, Supplier<T> defaultSupplier) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, clazz)).orElseGet(defaultSupplier);
    }

    public <T> T get(String key, Class<T> clazz) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, clazz)).orElse(null);
    }

    public <T> T safeGet(String key, Class<T> clazz) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, clazz)).orElse(null);
    }

    public <T> T get(String key, TypeReference<T> type, Supplier<T> defaultSupplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(defaultSupplier);
    }

    public <T> T safeGet(String key, TypeReference<T> type, Supplier<T> defaultSupplier) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(defaultSupplier);
    }

    public <T> T get(String key, TypeReference<T> type) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElse(null);
    }

    public <T> T safeGet(String key, TypeReference<T> type) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, type)).orElse(null);
    }

    public String getString(String key) {
        return get(key, String.class);
    }

    public String getString(String key, String defaultValue) {
        return get(key, String.class, () -> defaultValue);
    }

    public int getIntValue(String key) {
        return get(key, Integer.TYPE, () -> 0);
    }

    public int getIntValue(String key, int defaultValue) {
        return get(key, Integer.TYPE, () -> defaultValue);
    }

    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    public long getLongValue(String key) {
        return get(key, Long.TYPE, () -> 0L);
    }

    public long getLongValue(String key, long defaultValue) {
        return get(key, Long.TYPE, () -> defaultValue);
    }

    public Long getLong(String key) {
        return get(key, Long.class);
    }

    public double getDoubleValue(String key) {
        return get(key, Double.TYPE, () -> 0D);
    }

    public double getDoubleValue(String key, double defaultValue) {
        return get(key, Double.TYPE, () -> defaultValue);
    }

    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    public Date getDate(String key) {
        return get(key, Date.class);
    }

    public Date getDate(String key, Date defaultVal) {
        return get(key, Date.class, () -> defaultVal);
    }

    public Timestamp getTimestamp(String key) {
        return get(key, Timestamp.class);
    }

    public Timestamp getTimestamp(String key, Timestamp defaultVal) {
        return get(key, Timestamp.class, () -> defaultVal);
    }

    public LocalDate getLocalDate(String key) {
        return get(key, LocalDate.class);
    }

    public LocalDate getLocalDate(String key, LocalDate defaultVal) {
        return get(key, LocalDate.class, () -> defaultVal);
    }


    public LocalDateTime getLocalDateTime(String key) {
        return get(key, LocalDateTime.class);
    }

    public LocalDateTime getLocalDateTime(String key, LocalDateTime defaultVal) {
        return get(key, LocalDateTime.class, () -> defaultVal);
    }

    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    public Boolean getBooleanValue(String key) {
        return get(key, Boolean.TYPE, () -> Boolean.FALSE);
    }

    public Boolean getBoolean(String key, Boolean defaultVal) {
        return get(key, Boolean.class, () -> defaultVal);
    }

    public String safeGetString(String key) {
        return safeGet(key, String.class);
    }

    public String safeGetString(String key, String defaultValue) {
        return safeGet(key, String.class, () -> defaultValue);
    }

    public int safeGetIntValue(String key) {
        return safeGet(key, Integer.TYPE, () -> 0);
    }

    public int safeGetIntValue(String key, int defaultValue) {
        return safeGet(key, Integer.TYPE, () -> defaultValue);
    }

    public Integer safeGetInteger(String key) {
        return safeGet(key, Integer.class);
    }

    public long safeGetLongValue(String key) {
        return safeGet(key, Long.TYPE, () -> 0L);
    }

    public long safeGetLongValue(String key, long defaultValue) {
        return safeGet(key, Long.TYPE, () -> defaultValue);
    }

    public Long safeGetLong(String key) {
        return safeGet(key, Long.class);
    }

    public double safeGetDoubleValue(String key) {
        return safeGet(key, Double.TYPE, () -> 0D);
    }

    public double safeGetDoubleValue(String key, double defaultValue) {
        return safeGet(key, Double.TYPE, () -> defaultValue);
    }

    public Double safeGetDouble(String key) {
        return safeGet(key, Double.class);
    }

    public Date safeGetDate(String key) {
        return safeGet(key, Date.class);
    }

    public Date safeGetDate(String key, Date defaultVal) {
        return safeGet(key, Date.class, () -> defaultVal);
    }

    public Timestamp safeGetTimestamp(String key) {
        return safeGet(key, Timestamp.class);
    }

    public Timestamp safeGetTimestamp(String key, Timestamp defaultVal) {
        return safeGet(key, Timestamp.class, () -> defaultVal);
    }

    public LocalDate safeGetLocalDate(String key) {
        return safeGet(key, LocalDate.class);
    }

    public LocalDate safeGetLocalDate(String key, LocalDate defaultVal) {
        return safeGet(key, LocalDate.class, () -> defaultVal);
    }


    public LocalDateTime safeGetLocalDateTime(String key) {
        return safeGet(key, LocalDateTime.class);
    }

    public LocalDateTime safeGetLocalDateTime(String key, LocalDateTime defaultVal) {
        return safeGet(key, LocalDateTime.class, () -> defaultVal);
    }

    public Boolean safeGetBoolean(String key) {
        return safeGet(key, Boolean.class);
    }

    public Boolean safeGetBooleanValue(String key) {
        return safeGet(key, Boolean.TYPE, () -> Boolean.FALSE);
    }

    public Boolean safeGetBoolean(String key, Boolean defaultVal) {
        return safeGet(key, Boolean.class, () -> defaultVal);
    }
}
