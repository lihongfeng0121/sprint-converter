package com.sprint.common.converter;

import com.sprint.common.converter.util.Types;

import java.lang.reflect.Type;

/**
 * 类型引用
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2021年03月29日
 */
public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {

    private final Type type;

    protected TypeReference() {
        this.type = Types.getClassSuperclassType(getClass(), 0);
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int compareTo(TypeReference<T> o) {
        return 0;
    }
}