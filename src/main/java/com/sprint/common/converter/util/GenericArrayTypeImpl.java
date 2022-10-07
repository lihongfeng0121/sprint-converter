package com.sprint.common.converter.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author hongfeng.li
 * @since 2021/3/5
 */
class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;

    GenericArrayTypeImpl(Type genericComponentType) {
        this.genericComponentType = genericComponentType;
    }

    public Type getGenericComponentType() {
        return this.genericComponentType;
    }

    public String toString() {
        Type type = this.getGenericComponentType();
        StringBuilder builder = new StringBuilder();
        if (type instanceof Class) {
            builder.append(((Class<?>) type).getName());
        } else {
            builder.append(type.toString());
        }
        builder.append("[]");
        return builder.toString();
    }

    public boolean equals(Object type2) {
        if (type2 instanceof GenericArrayType) {
            GenericArrayType var2 = (GenericArrayType) type2;
            return Objects.equals(this.genericComponentType, var2.getGenericComponentType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.genericComponentType);
    }
}
