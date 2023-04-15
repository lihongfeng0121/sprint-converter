package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.TypeReference;

import java.beans.Transient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author hongfeng.li
 * @since 2023/2/2
 */
public class JsonObject extends LinkedHashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1801021242820209131L;

    public JsonObject() {
    }

    public JsonObject(Map<String, Object> map) {
        this.putAll(map);
    }

    public static JsonObject parse(String json) {
        return Jsons.toJavaObject(json, JsonObject.class);
    }

    public static JsonObject toJsonObject(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonObject) {
            return (JsonObject) value;
        } else {
            return value instanceof Map ? new JsonObject((Map) value) : AnyConverter.convert(value, JsonObject.class);
        }
    }

    @Transient
    public <T> T get(String key, Class<T> type) {
        return AnyConverter.convert(get(key), type);
    }

    @Transient
    public <T> T get(String key, Class<T> type, Supplier<T> supplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(supplier::get);
    }

    @Transient
    public <T> T get(String key, TypeReference<T> type) {
        return AnyConverter.convert(get(key), type);
    }

    @Transient
    public <T> T get(String key, TypeReference<T> type, Supplier<T> supplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(supplier::get);
    }

    @Transient
    public ObjectValue getObjectValue(String key) {
        return ObjectValue.ofNullable(get(key));
    }

    @Transient
    public JsonObject getJsonObject(String key) {
        return get(key, JsonObject.class);
    }

    @Transient
    public JsonArray getJsonArray(String key) {
        return get(key, JsonArray.class);
    }

    @Transient
    public String getString(String key) {
        return get(key, String.class);
    }

    @Transient
    public String getString(String key, String defaultValue) {
        return get(key, String.class, () -> defaultValue);
    }

    @Transient
    public int getIntValue(String key) {
        return get(key, Integer.TYPE, () -> 0);
    }

    @Transient
    public int getIntValue(String key, int defaultValue) {
        return get(key, Integer.TYPE, () -> defaultValue);
    }

    @Transient
    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    @Transient
    public long getLongValue(String key) {
        return get(key, Long.TYPE, () -> 0L);
    }

    @Transient
    public long getLongValue(String key, long defaultValue) {
        return get(key, Long.TYPE, () -> defaultValue);
    }

    @Transient
    public Long getLong(String key) {
        return get(key, Long.class);
    }

    @Transient
    public double getDoubleValue(String key) {
        return get(key, Double.TYPE, () -> 0D);
    }

    @Transient
    public double getDoubleValue(String key, double defaultValue) {
        return get(key, Double.TYPE, () -> defaultValue);
    }

    @Transient
    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    @Transient
    public Date getDate(String key) {
        return get(key, Date.class);
    }

    @Transient
    public Date getDate(String key, Date defaultVal) {
        return get(key, Date.class, () -> defaultVal);
    }

    @Transient
    public Timestamp getTimestamp(String key) {
        return get(key, Timestamp.class);
    }

    @Transient
    public Timestamp getTimestamp(String key, Timestamp defaultVal) {
        return get(key, Timestamp.class, () -> defaultVal);
    }

    @Transient
    public LocalDate getLocalDate(String key) {
        return get(key, LocalDate.class);
    }

    @Transient
    public LocalDate getLocalDate(String key, LocalDate defaultVal) {
        return get(key, LocalDate.class, () -> defaultVal);
    }

    @Transient
    public LocalDateTime getLocalDateTime(String key) {
        return get(key, LocalDateTime.class);
    }

    @Transient
    public LocalDateTime getLocalDateTime(String key, LocalDateTime defaultVal) {
        return get(key, LocalDateTime.class, () -> defaultVal);
    }

    @Transient
    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    @Transient
    public Boolean getBooleanValue(String key) {
        return get(key, Boolean.TYPE, () -> Boolean.FALSE);
    }

    @Transient
    public Boolean getBoolean(String key, Boolean defaultVal) {
        return get(key, Boolean.class, () -> defaultVal);
    }

    @Transient
    public LocalTime getLocalTime(String key) {
        return get(key, LocalTime.class);
    }

    @Transient
    public LocalTime getLocalTime(String key, LocalTime defaultValue) {
        return get(key, LocalTime.class, () -> defaultValue);
    }

    @Transient
    public YearMonth getYearMonth(String key) {
        return get(key, YearMonth.class);
    }

    @Transient
    public YearMonth getYearMonth(String key, YearMonth defaultValue) {
        return get(key, YearMonth.class, () -> defaultValue);
    }

    @Transient
    public Year getYear(String key) {
        return get(key, Year.class);
    }

    @Transient
    public Year getYear(String key, Year defaultValue) {
        return get(key, Year.class, () -> defaultValue);
    }

    @Override
    public String toString() {
        return Jsons.toJsonString(this);
    }

    public <T> T toJavaObject(Class<T> tClass) {
        return AnyConverter.convert(this, tClass);
    }

    public <T> T toJavaObject(TypeReference<T> tClass) {
        return AnyConverter.convert(this, tClass);
    }

}
