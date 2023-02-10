package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.BaseConverter;
import com.sprint.common.converter.Converter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author hongfeng.li
 * @since 2023/2/10
 */
public class JsonArray implements List<Object>, Cloneable, RandomAccess, Serializable {

    private final List<Object> list;

    public JsonArray() {
        this.list = new ArrayList<>();
    }

    public JsonArray(List<Object> list) {
        if (list == null) {
            throw new IllegalArgumentException("list is null.");
        } else {
            this.list = list;
        }
    }

    public JsonArray(int initialCapacity) {
        this.list = new ArrayList<>(initialCapacity);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return Converter.enforce(AnyConverter.convert(list.toArray(), a.getClass()));
    }

    @Override
    public boolean add(Object o) {
        return list.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Object get(int index) {
        return list.get(index);
    }

    public JsonObject getJsonObject(int index) {
        return JsonObject.toJavaObject(this.list.get(index));
    }

    public JsonArray getJsonArray(int index) {
        return JsonArray.toJsonArray(this.list.get(index));
    }

    public <T> T getObject(int index, Class<T> clazz) {
        Object obj = this.list.get(index);
        return AnyConverter.convert(obj, clazz);
    }

    public <T> T getObject(int index, Type type) {
        Object obj = this.list.get(index);
        return AnyConverter.convert(obj, type);
    }

    public ObjectValue getObjectValue(int index) {
        Object obj = this.list.get(index);
        return ObjectValue.valueOf(obj);
    }

    public Boolean getBoolean(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, Boolean.class);
    }

    public boolean getBooleanValue(int index) {
        Object value = this.get(index);
        return value == null ? false : BaseConverter.convert(value, Boolean.TYPE);
    }

    public Byte getByte(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, Byte.class);
    }

    public byte getByteValue(int index) {
        Byte byteVal = getByte(index);
        return byteVal == null ? 0 : byteVal;
    }

    public Short getShort(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, Short.class);
    }

    public short getShortValue(int index) {
        Object value = this.get(index);
        Short shortVal = BaseConverter.convert(value, Short.class);
        return shortVal == null ? 0 : shortVal;
    }

    public Integer getInteger(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, Integer.class);
    }

    public int getIntValue(int index) {
        Integer intVal = getInteger(index);
        return intVal == null ? 0 : intVal;
    }

    public Long getLong(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, Long.class);
    }

    public long getLongValue(int index) {
        Long longVal = getLong(index);
        return longVal == null ? 0L : longVal;
    }

    public Float getFloat(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, Float.class);
    }

    public float getFloatValue(int index) {
        Float floatValue = getFloat(index);
        return floatValue == null ? 0.0F : floatValue;
    }

    public Double getDouble(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, Double.class);
    }

    public double getDoubleValue(int index) {
        Double doubleValue = getDouble(index);
        return doubleValue == null ? 0.0D : doubleValue;
    }

    public BigDecimal getBigDecimal(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, BigDecimal.class);
    }

    public BigInteger getBigInteger(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, BigInteger.class);
    }

    public String getString(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, String.class);
    }

    public Date getDate(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, Date.class);
    }

    public java.sql.Date getSqlDate(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, java.sql.Date.class);
    }

    public Timestamp getTimestamp(int index) {
        Object value = this.get(index);
        return BaseConverter.convert(value, java.sql.Timestamp.class);
    }

    public <T> List<T> toJavaList(Class<T> clazz) {
        List<T> list = new ArrayList<>(this.size());
        if (JsonObject.class.isAssignableFrom(clazz)) {
            for (Object item : this) {
                list.add((T) JsonObject.toJavaObject(item));
            }
        } else {
            for (Object item : this) {
                T classItem = AnyConverter.convert(item, clazz);
                list.add(classItem);
            }
        }

        return list;
    }

    @Override
    public Object set(int index, Object element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        list.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public JsonArray clone() {
        try {
            return (JsonArray) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonArray objects = (JsonArray) o;
        return Objects.equals(list, objects.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public String toString() {
        return Jsons.toJsonString(this);
    }

    public String toJsonString(Object value) {
        return Jsons.toJsonString(this);
    }

    public static JsonArray toJsonArray(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JsonArray) {
            return (JsonArray) value;
        } else {
            return value instanceof List ? new JsonArray((List) value) : new JsonArray(AnyConverter.convert(value, List.class));
        }
    }
}
