package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.Converter;
import com.sprint.common.converter.TypeReference;

import java.beans.Transient;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author hongfeng.li
 * @since 2022/7/17
 */
public abstract class AbstractValue {

    public abstract Object getValue();

    public boolean isNullOrEmpty() {
        if (getValue() instanceof String) {
            return ((String) getValue()).isEmpty();
        } else {
            return getValue() == null;
        }
    }

    @Transient
    public <T> BeanOptional<T> getBeanOptional() {
        return Converter.enforce(BeanOptional.ofNullable(getValue()));
    }

    @Transient
    public <T> T getValue(Class<T> type, T defaultValue) {
        return isNullOrEmpty() ? defaultValue : AnyConverter.convert(getValue(), type);
    }

    @Transient
    public <T> T getValue(Class<T> type) {
        return getValue(type, null);
    }

    @Transient
    public <T> T getValue(TypeReference<T> type) {
        return isNullOrEmpty() ? null : AnyConverter.convert(getValue(), type);
    }

    @Transient
    public <T> T getValue(TypeReference<T> type, T defaultValue) {
        return isNullOrEmpty() ? defaultValue : AnyConverter.convert(getValue(), type);
    }

    @Transient
    public <T> T getValue(Type type) {
        return isNullOrEmpty() ? null : AnyConverter.convert(getValue(), type);
    }

    @Transient
    public <T> T getValue(Type type, T defaultValue) {
        return isNullOrEmpty() ? defaultValue : AnyConverter.convert(getValue(), type);
    }

    @Transient
    public int getIntValue() {
        return getValue(int.class, 0);
    }

    @Transient
    public int getIntValue(int defaultValue) {
        return getValue(int.class, defaultValue);
    }

    @Transient
    public Integer getInteger() {
        return getValue(Integer.class, null);
    }

    @Transient
    public long getLongValue() {
        return getValue(long.class, 0L);
    }

    @Transient
    public long getLongValue(long defaultValue) {
        return getValue(long.class, defaultValue);
    }

    @Transient
    public Long getLong() {
        return getValue(Long.class, null);
    }

    @Transient
    public double getDoubleValue(double defaultValue) {
        return getValue(double.class, defaultValue);
    }

    @Transient
    public Double getDouble() {
        return getValue(Double.class, null);
    }

    @Transient
    public Boolean getBoolean() {
        return getValue(Boolean.class, null);
    }

    @Transient
    public Map<String, Object> getMap() {
        return getValue(TypeReference.STR_OBJ_MAP, null);
    }

    @Transient
    public <K, V> Map<K, V> getMap(Class<K> keyType, Class<V> valueType) {
        return getValue(Types.makeMapType(keyType, valueType));
    }

    @Transient
    public List<Map<String, Object>> getListMap() {
        return getValue(TypeReference.STR_OBJ_MAP_LIST, null);
    }

    @Transient
    public Date getDate() {
        return getValue(Date.class, null);
    }

    @Transient
    public Date getDate(Date defaultValue) {
        return getValue(Date.class, defaultValue);
    }

    @Transient
    public Timestamp getTimestamp() {
        return getValue(Timestamp.class, null);
    }

    @Transient
    public Timestamp getTimestamp(Timestamp defaultValue) {
        return getValue(Timestamp.class, defaultValue);
    }

    @Transient
    public LocalDate getLocalDate() {
        return getValue(LocalDate.class);
    }

    @Transient
    public LocalDate getLocalDate(LocalDate defaultValue) {
        return getValue(LocalDate.class, defaultValue);
    }

    @Transient
    public LocalTime getLocalTime() {
        return getValue(LocalTime.class);
    }

    @Transient
    public LocalTime getLocalTime(LocalTime defaultValue) {
        return getValue(LocalTime.class, defaultValue);
    }

    @Transient
    public LocalDateTime getLocalDateTime() {
        return getValue(LocalDateTime.class);
    }

    @Transient
    public LocalDateTime getLocalDateTime(LocalDateTime defaultValue) {
        return getValue(LocalDateTime.class, defaultValue);
    }

    @Transient
    public YearMonth getYearMonth() {
        return getValue(YearMonth.class);
    }

    @Transient
    public YearMonth getYearMonth(YearMonth defaultValue) {
        return getValue(YearMonth.class, defaultValue);
    }

    @Transient
    public Year getYear() {
        return getValue(Year.class);
    }

    @Transient
    public Year getYear(Year defaultValue) {
        return getValue(Year.class, defaultValue);
    }

    @Transient
    public ObjectValue getObjectValue() {
        return ObjectValue.ofNullable(this);
    }

    @Override
    public String toString() {
        return getValue() == null ? "null" : getValue().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectValue that = (ObjectValue) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
