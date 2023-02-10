package com.sprint.common.converter.util;

/**
 * @author hongfeng.li
 * @since 2023/2/2
 */
public class ObjectValue extends AbstractValue {

    private final Object value;

    public ObjectValue(Object value) {
        this.value = value instanceof ObjectValue ? ((ObjectValue) value).getValue() : value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public static ObjectValue valueOf(Object value) {
        return new ObjectValue(value);
    }
}
