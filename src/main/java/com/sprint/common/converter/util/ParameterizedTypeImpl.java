package com.sprint.common.converter.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author hongfeng.li
 * @since 2021/3/5
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    private final Type[] actualTypeArguments;
    private final Class<?> rawType;
    private final Type ownerType;

    private ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
        this.ownerType = ownerType != null ? ownerType : rawType.getDeclaringClass();
    }

    public static ParameterizedTypeImpl make(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
        return new ParameterizedTypeImpl(rawType, actualTypeArguments, ownerType);
    }

    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments.clone();
    }

    public Class<?> getRawType() {
        return this.rawType;
    }

    public Type getOwnerType() {
        return this.ownerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
        return Arrays.equals(actualTypeArguments, that.actualTypeArguments) && Objects.equals(rawType, that.rawType)
                && Objects.equals(ownerType, that.ownerType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rawType, ownerType);
        result = 31 * result + Arrays.hashCode(actualTypeArguments);
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.ownerType != null) {
            if (this.ownerType instanceof Class) {
                builder.append(((Class) this.ownerType).getName());
            } else {
                builder.append(this.ownerType);
            }

            builder.append("$");
            if (this.ownerType instanceof ParameterizedTypeImpl) {
                builder.append(this.rawType.getName()
                        .replace(((ParameterizedTypeImpl) this.ownerType).getRawType().getName() + "$", ""));
            } else {
                builder.append(this.rawType.getSimpleName());
            }
        } else {
            builder.append(this.rawType.getName());
        }

        if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
            builder.append("<");
            boolean ending = true;
            for (Type item : this.actualTypeArguments) {
                if (!ending) {
                    builder.append(", ");
                }

                builder.append(item.getTypeName());
                ending = false;
            }
            builder.append(">");
        }

        return builder.toString();
    }
}
