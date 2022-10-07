package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.TypeReference;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hongfeng.li
 * @since 2022/7/17
 */
public abstract class AbstractValue {

    public abstract Object getValue();

    private boolean isNullOrEmpty() {
        if (getValue() instanceof String) {
            return ((String) getValue()).isEmpty();
        } else {
            return getValue() == null;
        }
    }

    public <T> T getValue(Class<T> type, T defaultValue) {
        return isNullOrEmpty() ? defaultValue : AnyConverter.convert(getValue(), type);
    }

    public <T> T getValue(Class<T> type) {
        return getValue(type, null);
    }

    public <T> T getValue(TypeReference<T> type) {
        return isNullOrEmpty() ? null : AnyConverter.convert(getValue(), type);
    }

    public <T> T getValue(TypeReference<T> type, T defaultValue) {
        return isNullOrEmpty() ? defaultValue : AnyConverter.convert(getValue(), type);
    }

    public <T> T getValue(Type type) {
        return isNullOrEmpty() ? null : AnyConverter.convert(getValue(), type);
    }

    public <T> T getValue(Type type, T defaultValue) {
        return isNullOrEmpty() ? defaultValue : AnyConverter.convert(getValue(), type);
    }

    public int getIntValue() {
        return getValue(int.class, 0);
    }

    public int getIntValue(int defaultValue) {
        return getValue(int.class, defaultValue);
    }

    public Integer getInteger() {
        return getValue(Integer.class, null);
    }

    public long getLongValue() {
        return getValue(long.class, 0L);
    }

    public long getLongValue(long defaultValue) {
        return getValue(long.class, defaultValue);
    }

    public Long getLong() {
        return getValue(Long.class, null);
    }

    public double getDoubleValue(double defaultValue) {
        return getValue(double.class, defaultValue);
    }

    public Double getDouble() {
        return getValue(Double.class, null);
    }

    public Boolean getBoolean() {
        return getValue(Boolean.class, null);
    }

    public Map<String, Object> getMap() {
        return getValue(TypeReference.STR_OBJ_MAP, null);
    }

    public <K, V> Map<K, V> getMap(Class<K> keyType, Class<V> valueType) {
        return getValue(Types.makeMapType(keyType, valueType));
    }

    public List<Map<String, Object>> getListMap() {
        return getValue(TypeReference.STR_OBJ_MAP_LIST, null);
    }

    public Date getDate() {
        return getValue(Date.class, null);
    }

    public Date getDate(Date defaultValue) {
        return getValue(Date.class, defaultValue);
    }

    public Timestamp getTimestamp() {
        return getValue(Timestamp.class, null);
    }

    public Timestamp getTimestamp(Timestamp defaultValue) {
        return getValue(Timestamp.class, defaultValue);
    }
}
