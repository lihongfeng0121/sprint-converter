package com.sprint.common.converter.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author hongfeng.li
 * @since 2022/3/5
 */
public class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;

    private GenericArrayTypeImpl(Type var1) {
        this.genericComponentType = var1;
    }

    public static GenericArrayTypeImpl make(Type var0) {
        return new GenericArrayTypeImpl(var0);
    }

    public Type getGenericComponentType() {
        return this.genericComponentType;
    }

    public String toString() {
        Type var1 = this.getGenericComponentType();
        StringBuilder var2 = new StringBuilder();
        if (var1 instanceof Class) {
            var2.append(((Class) var1).getName());
        } else {
            var2.append(var1.toString());
        }

        var2.append("[]");
        return var2.toString();
    }

    public boolean equals(Object var1) {
        if (var1 instanceof GenericArrayType) {
            GenericArrayType var2 = (GenericArrayType) var1;
            return Objects.equals(this.genericComponentType, var2.getGenericComponentType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.genericComponentType);
    }
}
