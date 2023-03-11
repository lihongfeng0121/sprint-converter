package com.sprint.common.converter.util;

import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hongfeng.li
 * @since 2023/2/28
 */
public class TypeDescriptor implements Serializable {

    private final Type declaringType;
    private final Type rawType;


    private volatile Class<?> actualClass = null;
    private volatile Type actualType = null;
    private volatile Type arrayComponentType = null;
    private final Map<Class<?>, Type[]> classGenericsType = new ConcurrentHashMap<>();

    public TypeDescriptor(Type declaringType, Type rawType) {
        this.declaringType = declaringType;
        this.rawType = rawType;
    }

    public Type getDeclaringType() {
        return declaringType;
    }

    public Type getRawType() {
        return rawType;
    }

    @Transient
    public Class<?> getActualClass() {
        if (actualClass == null) {
            actualClass = Types.extractClass(rawType, declaringType);
        }
        return actualClass;
    }

    public Type getActualType() {
        if (actualType == null) {
            actualType = Types.getActualType(declaringType, rawType);
        }
        return actualType;
    }

    public Type getArrayComponentType() {
        if (arrayComponentType == null) {
            arrayComponentType = Types.getArrayComponentType(rawType, declaringType);
        }
        return arrayComponentType;
    }

    public TypeDescriptor getArrayComponentTypeDescriptor() {
        return TypeDescriptor.of(getDeclaringType(), getArrayComponentType());
    }

    public Type getCollectionItemType() {
        return Miscs.at(classGenericsType.computeIfAbsent(Collection.class, (key) -> GenericsResolver.of(Collection.class).resolve(this)), 0);
    }

    public TypeDescriptor getCollectionItemTypeDescriptor() {
        return TypeDescriptor.of(declaringType, getCollectionItemType());
    }

    public Type[] getMapKVType() {
        return classGenericsType.computeIfAbsent(Map.class, (key) -> GenericsResolver.of(Map.class).resolve(this));
    }

    public TypeDescriptor[] getMapKVTypeDescriptor() {
        Type[] kvTypes = getMapKVType();
        return new TypeDescriptor[]{TypeDescriptor.of(declaringType, kvTypes[0]), TypeDescriptor.of(declaringType, kvTypes[1])};
    }

    public Type[] getGenericsTypes(Class<?> actualClass) {
        return classGenericsType.computeIfAbsent(actualClass, (key) -> GenericsResolver.of(key).resolve(this));
    }

    /**
     * 是否是OBJECT类型
     *
     * @return 是否
     */
    @Transient
    public boolean isObjectType() {
        return Types.isObjectType(getActualClass());
    }

    @Transient
    public boolean isBean() {
        return Types.isBean(getActualClass());
    }

    @Transient
    public boolean isBeanOrMap() {
        return isBean() || isMap();
    }

    @Transient
    public boolean isMulti() {
        return Types.isMulti(getActualClass());
    }

    @Transient
    public boolean isCollection() {
        return Types.isCollection(getActualClass());
    }

    @Transient
    public boolean isMap() {
        return Types.isMap(getActualClass());
    }

    @Transient
    public boolean isArray() {
        return Types.isArray(getActualClass());
    }

    @Transient
    public boolean isPrimitiveTypeOrWrapClass() {
        return Types.isPrimitiveTypeOrWrapClass(getActualClass());
    }

    @Transient
    public boolean isInterface() {
        return getActualClass().isInterface();
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getActualClass().getModifiers());
    }

    public boolean isAssignableFrom(TypeDescriptor descriptor) {
        return getActualClass().isAssignableFrom(descriptor.getActualClass());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeDescriptor that = (TypeDescriptor) o;
        return Objects.equals(declaringType, that.declaringType) && Objects.equals(rawType, that.rawType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaringType, rawType);
    }

    public boolean actualTypeEquals(TypeDescriptor descriptor) {
        if (descriptor == null) {
            return false;
        }
        return Objects.equals(getActualClass(), descriptor.getActualClass());
    }


    private static final ConcurrentReferenceHashMap<Type, TypeDescriptor> TYPE_CACHE = new ConcurrentReferenceHashMap<>();
    private static final ConcurrentReferenceHashMap<Type, ConcurrentReferenceHashMap<Type, TypeDescriptor>> RAW_TYPE_CACHE = new ConcurrentReferenceHashMap<>();

    public static final TypeDescriptor OBJ_TYPE_DESCRIPTOR = new TypeDescriptor(null, Object.class) {

        @Override
        public boolean isObjectType() {
            return true;
        }

        @Override
        public boolean isBean() {
            return false;
        }

        @Override
        public boolean isMulti() {
            return false;
        }

        @Override
        public boolean isCollection() {
            return false;
        }

        @Override
        public boolean isMap() {
            return false;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isPrimitiveTypeOrWrapClass() {
            return false;
        }

        @Override
        public boolean isInterface() {
            return false;
        }

        @Override
        public boolean isAbstract() {
            return false;
        }
    };

    public static TypeDescriptor of(Type type) {
        if (type == null) {
            return OBJ_TYPE_DESCRIPTOR;
        }
        return TYPE_CACHE.computeIfAbsent(type, (key) -> new TypeDescriptor(null, key));
    }

    public static TypeDescriptor of(Type declaringType, Type rawType) {
        if (declaringType == null) {
            return of(rawType);
        }
        if (rawType == null) {
            return OBJ_TYPE_DESCRIPTOR;
        }
        return RAW_TYPE_CACHE.computeIfAbsent(declaringType, (key) -> new ConcurrentReferenceHashMap<>()).get(rawType, () -> new TypeDescriptor(declaringType, rawType));
    }
}
