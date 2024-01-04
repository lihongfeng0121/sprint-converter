package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.BaseConverter;
import com.sprint.common.converter.Converter;
import com.sprint.common.converter.TypeReference;
import com.sprint.common.converter.exception.NotSupportConvertException;

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
        if (getValue() instanceof String) {
            if (Types.isJsonObject((String) getValue())) {
                return Converter.enforce(JsonObject.parse((String) getValue()));
            } else {
                throw new NotSupportConvertException(String.class, BeanOptional.class);
            }
        } else {
            return Converter.enforce(BeanOptional.ofNullable(getValue()));
        }
    }

    @Transient
    public <T> T getValue(Class<T> type, T defaultValue) {
        return isNullOrEmpty() ? defaultValue : AnyConverter.convert(getValue(), type);
    }

    @Transient
    public <T> T getBaseValue(Class<T> type, T defaultValue) {
        return isNullOrEmpty() ? defaultValue : BaseConverter.convert(getValue(), type);
    }

    @Transient
    public <T> T getBaseValue(Class<T> type) {
        return BaseConverter.convert(getValue(), type);
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
    public String getString() {
        return getBaseValue(String.class, null);
    }

    @Transient
    public String getString(String defaultValue) {
        return getBaseValue(String.class, defaultValue);
    }

    @Transient
    public int getIntValue() {
        return getBaseValue(int.class, 0);
    }

    @Transient
    public int getIntValue(int defaultValue) {
        return getBaseValue(int.class, defaultValue);
    }

    @Transient
    public Integer getInteger() {
        return getBaseValue(Integer.class, null);
    }

    @Transient
    public long getLongValue() {
        return getBaseValue(long.class, 0L);
    }

    @Transient
    public long getLongValue(long defaultValue) {
        return getBaseValue(long.class, defaultValue);
    }

    @Transient
    public Long getLong() {
        return getBaseValue(Long.class, null);
    }

    @Transient
    public double getDoubleValue(double defaultValue) {
        return getBaseValue(double.class, defaultValue);
    }

    @Transient
    public Double getDouble() {
        return getBaseValue(Double.class, null);
    }

    @Transient
    public Boolean getBoolean() {
        return getBaseValue(Boolean.class, null);
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
        return getBaseValue(Date.class, null);
    }

    @Transient
    public Date getDate(Date defaultValue) {
        return getBaseValue(Date.class, defaultValue);
    }

    @Transient
    public Timestamp getTimestamp() {
        return getBaseValue(Timestamp.class, null);
    }

    @Transient
    public Timestamp getTimestamp(Timestamp defaultValue) {
        return getBaseValue(Timestamp.class, defaultValue);
    }

    @Transient
    public LocalDate getLocalDate() {
        return getBaseValue(LocalDate.class, null);
    }

    @Transient
    public LocalDate getLocalDate(LocalDate defaultValue) {
        return getBaseValue(LocalDate.class, defaultValue);
    }

    @Transient
    public LocalTime getLocalTime() {
        return getBaseValue(LocalTime.class, null);
    }

    @Transient
    public LocalTime getLocalTime(LocalTime defaultValue) {
        return getBaseValue(LocalTime.class, defaultValue);
    }

    @Transient
    public LocalDateTime getLocalDateTime() {
        return getBaseValue(LocalDateTime.class, null);
    }

    @Transient
    public LocalDateTime getLocalDateTime(LocalDateTime defaultValue) {
        return getBaseValue(LocalDateTime.class, defaultValue);
    }

    @Transient
    public YearMonth getYearMonth() {
        return getBaseValue(YearMonth.class, null);
    }

    @Transient
    public YearMonth getYearMonth(YearMonth defaultValue) {
        return getBaseValue(YearMonth.class, defaultValue);
    }

    @Transient
    public Year getYear() {
        return getBaseValue(Year.class, null);
    }

    @Transient
    public Year getYear(Year defaultValue) {
        return getBaseValue(Year.class, defaultValue);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectValue that = (ObjectValue) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
