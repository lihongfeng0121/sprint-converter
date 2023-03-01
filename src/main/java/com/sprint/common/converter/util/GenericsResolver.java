package com.sprint.common.converter.util;

import com.sprint.common.converter.TypeReference;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;

/**
 * @author hongfeng.li
 * @since 2022/6/5
 */
public class GenericsResolver {

    public static final String JAVA_INNER = "java.";
    private static final ConcurrentReferenceHashMap<ParameterizedType, Type[]> MAP_PARAMETER_TYPE_CACHE = new ConcurrentReferenceHashMap<>();
    private static final ConcurrentReferenceHashMap<Class<?>, GenericsResolver> MAP_INTERFACE_GENERICS_RESOLVER = new ConcurrentReferenceHashMap<>();
    private final Class<?> classType;
    private final TypeVariable<? extends Class<?>>[] typeParameters;
    private volatile Type[] noneTypeParameters = null;

    GenericsResolver(Class<?> classType) {
        Assert.notNull(classType, "class type not null");
        this.classType = classType;
        this.typeParameters = classType.getTypeParameters();
    }

    /**
     * OF
     *
     * @param classType 解析类型
     * @return 解析器
     */
    public static GenericsResolver of(Class<?> classType) {
        return MAP_INTERFACE_GENERICS_RESOLVER.computeIfAbsent(classType, GenericsResolver::new);
    }

    /**
     * 获取默认范型类型
     *
     * @return 无声明范型类型
     */
    public Type[] getNoneTypeParameters() {
        if (this.noneTypeParameters == null) {
            Type[] types = new Type[this.typeParameters.length];
            for (int i = 0, length = this.typeParameters.length; i < length; i++) {
                types[i] = Types.OBJECT_CLASS;
            }
            this.noneTypeParameters = types;
        }
        return this.noneTypeParameters;
    }

    /**
     * 解析范型真实类型
     *
     * @param declaringType 描述类型（可为空）
     * @param rawType       类型
     * @return 范型
     */
    public Type[] resolve(Type declaringType, Type rawType) {
        if (rawType instanceof TypeVariable && declaringType != null) {
            return resolve(Types.getActualType(declaringType, rawType));
        } else if (rawType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = getParameterizedType(declaringType, rawType);
            return getTypeParametersFromCache(parameterizedType);
        } else if (rawType instanceof Class<?>) {
            return resolve((Class<?>) rawType);
        } else {
            return getNoneTypeParameters();
        }
    }


    /**
     * 解析范型真实类型
     *
     * @param type 类型
     * @return 范型
     */
    public Type[] resolve(TypeDescriptor type) {
        Type declaringType = type.getDeclaringType();
        Type rawType = type.getRawType();
        if (rawType instanceof TypeVariable && declaringType != null) {
            return resolve(Types.getActualType(declaringType, rawType));
        } else if (rawType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = getParameterizedType(declaringType, rawType);
            return getTypeParametersFromCache(parameterizedType);
        } else if (rawType instanceof Class<?>) {
            return resolve((Class<?>) rawType);
        } else {
            return getNoneTypeParameters();
        }
    }

    /**
     * 解析范型真实类型
     *
     * @param type 类型
     * @return 范型
     */
    public Type[] resolve(Type type) {
        return resolve(null, type);
    }

    /**
     * 解析范型真实类型
     *
     * @param reference 类
     * @return 范型
     */
    public Type[] resolve(TypeReference<?> reference) {
        return resolve(reference.getType());
    }

    /**
     * 解析范型真实类型
     *
     * @param clazz 类
     * @return 范型
     */
    public Type[] resolve(Class<?> clazz) {
        return clazz.getName().startsWith(JAVA_INNER) ? getNoneTypeParameters() : resolve(getSuperType(clazz, classType));
    }

    private ParameterizedType getParameterizedType(Type declaringType, Type rawType) {
        ParameterizedType parameterizedType = (ParameterizedType) rawType;
        if (declaringType != null) {
            Map<TypeVariable<?>, Type> variableTypeMap = Types.resolveVariableTypeMap(declaringType);
            parameterizedType = makeParameterizedType(Types.extractClass(rawType),
                    parameterizedType.getActualTypeArguments(), variableTypeMap);
        }
        return parameterizedType;
    }

    private Type[] getTypeParametersFromCache(ParameterizedType parameterizedType) {
        return Miscs.get(MAP_PARAMETER_TYPE_CACHE, parameterizedType, () -> doResolve(parameterizedType));
    }

    private Type[] doResolve(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (rawType == this.classType) {
            return Arrays.stream(actualTypeArguments).map(this::getWildcardTypeUpperBounds).toArray(Type[]::new);
        }
        Class<?> rawClass = (Class<?>) rawType;
        Map<TypeVariable<?>, Type> actualTypeMap = Miscs.toMap(rawClass.getTypeParameters(), actualTypeArguments);
        Type superType = getSuperType(rawClass, this.classType);
        if (superType instanceof ParameterizedType) {
            Class<?> superClass = Types.extractClass(superType);
            Type[] superClassTypeParameters = ((ParameterizedType) superType).getActualTypeArguments();
            return superClassTypeParameters.length > 0
                    ? resolve(makeParameterizedType(superClass, superClassTypeParameters, actualTypeMap))
                    : resolve(superClass);
        }
        return resolve(superType);
    }

    private Type getSuperType(Class<?> clazz, Class<?> interfacesOrSuperclass) {
        Type assignable = null;
        if (interfacesOrSuperclass.isInterface()) {
            for (Type type : clazz.getGenericInterfaces()) {
                Class<?> rawClass = Types.extractClass(type);
                if (rawClass == interfacesOrSuperclass) {
                    return type;
                }
                if (interfacesOrSuperclass.isAssignableFrom(rawClass)) {
                    assignable = type;
                }
            }
        }
        return assignable == null ? clazz.getGenericSuperclass() : assignable;
    }

    /**
     * <p>
     * 返回代表上层的{@code Type}对象的数组该类型变量的边界。注意，如果没有上界其上限为{@code Object}。
     * </p>
     *
     * @param type 类型
     * @return 上届
     */
    private Type getWildcardTypeUpperBounds(Type type) {
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            return upperBounds.length > 0 ? upperBounds[0] : Types.OBJECT_CLASS;
        }
        return type;
    }

    private static ParameterizedType makeParameterizedType(Class<?> rawClass, Type[] typeParameters,
                                                           Map<TypeVariable<?>, Type> actualTypeMap) {
        int length = typeParameters.length;
        Type[] actualTypeArguments = new Type[length];
        for (int i = 0; i < length; i++) {
            actualTypeArguments[i] = getActualType(typeParameters[i], actualTypeMap);
        }
        return Types.makeType(rawClass, actualTypeArguments, null);
    }

    private static Type getActualType(Type typeParameter, Map<TypeVariable<?>, Type> actualTypeMap) {
        if (typeParameter instanceof TypeVariable) {
            return actualTypeMap.get(typeParameter);
        } else if (typeParameter instanceof ParameterizedType) {
            return makeParameterizedType(Types.extractClass(typeParameter),
                    ((ParameterizedType) typeParameter).getActualTypeArguments(), actualTypeMap);
        } else if (typeParameter instanceof GenericArrayType) {
            return Types.makeArrayType(getActualType(((GenericArrayType) typeParameter).getGenericComponentType(), actualTypeMap));
        }
        return typeParameter;
    }
}
