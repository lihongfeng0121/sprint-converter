package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author hongfeng.li
 * @since 2023/2/2
 */
public class JsonObject extends LinkedHashMap<String, Object> {

    public JsonObject() {
    }

    public JsonObject(Map<String, Object> map) {
        this.putAll(map);
    }

    public static JsonObject parse(String json) {
        return Jsons.toJavaObject(json, JsonObject.class);
    }

    public static JsonObject toJavaObject(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonObject) {
            return (JsonObject) value;
        } else {
            return value instanceof Map ? new JsonObject((Map) value) : AnyConverter.convert(value, JsonObject.class);
        }
    }

    public <T> T get(String key, Class<T> type) {
        return AnyConverter.convert(key, type);
    }

    public <T> T get(String key, Class<T> type, Supplier<T> supplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(supplier::get);
    }

    public ObjectValue getObjectValue(String key) {
        return ObjectValue.valueOf(get(key));
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

    @Override
    public String toString() {
        return Jsons.toJsonString(this);
    }
}
