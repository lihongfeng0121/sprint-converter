package com.sprint.common.converter.util;

import java.lang.reflect.*;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * 类操作工具
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月11日
 */
public class Types {

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);
    private static final Map<String, Class<?>> commonClassCache = new HashMap<>(32);

    public static final Class<?> OBJECT_CLASS = Object.class;

    public static final String[] COLLECTION_IGNORES = {"size", "empty"};

    public static final String[] MAP_IGNORES = {"size", "empty"};
    private static final ClassLoader APP_CLASS_LOADER = ClassLoader.getSystemClassLoader();

    static {
        // 包装类->基本类
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        primitiveWrapperTypeMap.forEach((key, val) -> {
            primitiveTypeToWrapperMap.put(val, key);
            registerCommonClasses(key);
        });

        Set<Class<?>> primitiveTypes = new HashSet<>(32);

        primitiveTypes.addAll(primitiveWrapperTypeMap.values());

        primitiveTypes.addAll(Arrays.asList(boolean[].class, byte[].class, char[].class, double[].class, float[].class,
                int[].class, long[].class, short[].class));

        primitiveTypes.add(void.class);

        for (Class<?> primitiveType : primitiveTypes) {
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }

        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class,
                Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Object.class, Object[].class,
                Class.class, Class[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class,
                StackTraceElement.class, StackTraceElement[].class);
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            commonClassCache.put(clazz.getName(), clazz);
        }
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Assert.notNull(clazz, "clazz can't be null");
        NoSuchFieldException ex = null;
        while (null != clazz) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                ex = e;
                clazz = clazz.getSuperclass();
            }
        }
        throw ex;
    }

    /**
     * 获取类的范型
     *
     * @param clazz 类
     * @return ClassSuperclassType
     */
    public static Type[] getClassSuperclassType(Class<?> clazz) {
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + clazz + "] is an interface");
        }
        Type type = clazz.getGenericSuperclass();

        if (type instanceof Class) {
            return new Type[]{};
        } else if (type instanceof ParameterizedType) {
            return ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
        } else {
            return new Type[]{};
        }
    }

    /**
     * 获取类的范型
     *
     * @param clazz 类
     * @param lc    lc
     * @return ClassSuperclassType
     */
    public static Type getClassSuperclassType(Class<?> clazz, int lc) {
        return MiscUtils.at(getClassSuperclassType(clazz), lc, null);
    }

    /**
     * 获取类的范型
     *
     * @param clazz 类
     * @return ClassSuperclassTypeMap
     */
    public static Map<TypeVariable<?>, Type> getClassSuperclassTypeMap(Class<?> clazz) {
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + clazz + "] is an interface");
        }
        return MapUtils.toMap(clazz.getSuperclass().getTypeParameters(), getClassSuperclassType(clazz));
    }

    /**
     * 获取指定类的范型
     *
     * @param clazz 原类
     * @param intf  目标接口
     * @param lc    位置
     * @return InterfaceSuperclass
     */
    public static Class<?> getInterfaceSuperclass(Class<?> clazz, Class<?> intf, int lc) {
        return MiscUtils.at(getInterfaceSuperclass(clazz, intf), lc, null);
    }

    /**
     * 获取指定类的范型
     *
     * @param clazz 原类
     * @param intf  目标接口
     * @return InterfaceSuperclass
     */
    public static Class<?>[] getInterfaceSuperclass(Class<?> clazz, Class<?> intf) {
        Type[] types = getInterfaceSuperclassType(clazz, intf);
        List<Class<?>> classes = new ArrayList<>();
        for (Type type : types) {
            if (type instanceof Class) {
                classes.add((Class<?>) type);
            }
        }
        return classes.toArray(new Class[]{});
    }

    /**
     * 获取指定类的范型
     *
     * @param clazz 原类
     * @param intf  目标接口
     * @return InterfaceSuperclassType
     */
    public static Type[] getInterfaceSuperclassType(Class<?> clazz, Class<?> intf) {
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + clazz + "] is not an interface");
        }
        Type[] interfacesTypes = clazz.getGenericInterfaces();
        for (Type t : interfacesTypes) {
            if (t instanceof ParameterizedType && t.getTypeName().contains(intf.getName())) {
                ParameterizedType parameterizedType = (ParameterizedType) t;
                return parameterizedType.getActualTypeArguments();
            }
        }
        return new Class[]{};
    }

    /**
     * 获取类的范型
     *
     * @param beanType  类
     * @param fieldType 类
     * @return ComponentType
     */
    public static Type getComponentType(Type beanType, Type fieldType) {
        if (fieldType instanceof TypeVariable) {
            return getClassTypeMap(beanType).get(fieldType);
        } else {
            return fieldType;
        }
    }

    /**
     * 获取类的范型
     *
     * @param type 类
     * @return ClassTypeMap
     */
    public static Map<TypeVariable<?>, Type> getClassTypeMap(Type type) {
        Class<?> clazz = extractClass(type);
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + clazz + "] is an interface");
        }

        return MapUtils.toMap(clazz.getTypeParameters(), getActualTypeArguments(type));
    }

    /**
     * 提取Class对象
     *
     * @param type 类型
     * @return Class对象
     */
    public static Class<?> extractClass(Type type) {
        return extractClass(type, null);
    }

    /**
     * 提取Class对象
     *
     * @param type      类型
     * @param clazzType clazzType
     * @return Class对象
     */
    public static Class<?> extractClass(Type type, Type clazzType) {
        if (isArray(type)) {
            Class<?> componentType = extractClass(getArrayComponentType(type, clazzType));
            return Array.newInstance(componentType, 0).getClass();
        } else {
            if (type instanceof Class<?>) {
                return (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                return extractClass(((ParameterizedType) type).getRawType(), clazzType);
            } else if (type instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) type;
                if (wildcardType.getUpperBounds() != null && wildcardType.getUpperBounds().length > 0) {
                    return extractClass(wildcardType.getUpperBounds()[0], clazzType);
                } else if (wildcardType.getLowerBounds() != null && wildcardType.getLowerBounds().length > 0) {
                    return extractClass(wildcardType.getLowerBounds()[0], clazzType);
                } else {
                    return OBJECT_CLASS;
                }
            } else if (type instanceof TypeVariable && clazzType != null) {
                if (clazzType instanceof Class) {
                    return extractClass(getClassSuperclassTypeMap((Class<?>) clazzType).get(type), clazzType);
                } else if (clazzType instanceof ParameterizedType) {
                    return extractClass(getClassTypeMap(clazzType).get(type), clazzType);
                } else {
                    return OBJECT_CLASS;
                }
            } else {
                return OBJECT_CLASS;
            }
        }
    }

    /**
     * 获取对象范型参数
     *
     * @param type type
     * @return actualTypeArguments
     */
    public static Type[] getActualTypeArguments(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] paramTypes = ((ParameterizedType) type).getActualTypeArguments();
            Type[] classes = new Type[paramTypes.length];
            System.arraycopy(paramTypes, 0, classes, 0, paramTypes.length);
            return classes;
        }
        return new Type[]{};
    }


    /**
     * 获取数组中元素的Class对象
     *
     * @param type      type
     * @param clazzType clazzType
     * @return ArrayComponentType
     */
    public static Type getArrayComponentType(Type type, Type clazzType) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).getComponentType();
        } else if (type instanceof ParameterizedType) {
            return getArrayComponentType(((ParameterizedType) type).getRawType(), clazzType);
        } else if (type instanceof GenericArrayType) {
            return extractGenericArrayClass((GenericArrayType) type, clazzType);
        } else if (type instanceof TypeVariable) {
            return getArrayComponentType(getComponentType(clazzType, type), clazzType);
        } else {
            return OBJECT_CLASS;
        }
    }

    /**
     * 获取泛型数组中元素的Class对象
     *
     * @param genericArrayType 泛型数组类型
     * @return 数组中元素的Class对象
     */
    private static Type extractGenericArrayClass(GenericArrayType genericArrayType, Type clazzType) {
        Type componentType = genericArrayType.getGenericComponentType();
        if (componentType instanceof Class<?>) {
            return componentType;
        } else if (componentType instanceof ParameterizedType) {
            return componentType;
        } else if (componentType instanceof GenericArrayType) {
            return Array
                    .newInstance(extractClass(extractGenericArrayClass((GenericArrayType) componentType, clazzType)), 0)
                    .getClass();
        } else if (componentType instanceof TypeVariable) {
            if (clazzType instanceof Class) {
                return getClassSuperclassTypeMap((Class<?>) clazzType).get(componentType);
            } else if (clazzType instanceof ParameterizedType) {
                return getClassTypeMap(clazzType).get(componentType);
            } else {
                return OBJECT_CLASS;
            }
        } else {
            return OBJECT_CLASS;
        }
    }

    public static Type[] getMapKVType(Type fieldType) {
        return getMapKVType(null, fieldType);
    }

    public static Type[] getMapKVType(Type beanType, Type fieldType) {
        return GenericsResolver.of(Map.class).resolve(beanType, fieldType);
    }

    public static Type getCollectionItemType(Type fieldType) {
        return getCollectionItemType(null, fieldType);
    }

    public static Type getCollectionItemType(Type beanType, Type fieldType) {
        return GenericsResolver.of(Collection.class).resolve(beanType, fieldType)[0];
    }

    public static ParameterizedType makeParameterizedType(Class<?> rawClass, Type[] typeParameters,
                                                          Map<TypeVariable<?>, Type> actualTypeMap) {
        int length = typeParameters.length;
        Type[] actualTypeArguments = new Type[length];
        for (int i = 0; i < length; i++) {
            actualTypeArguments[i] = getActualType(typeParameters[i], actualTypeMap);
        }
        return ParameterizedTypeImpl.make(rawClass, actualTypeArguments, null);
    }

    private static Type getActualType(Type typeParameter, Map<TypeVariable<?>, Type> actualTypeMap) {
        if (typeParameter instanceof TypeVariable) {
            return actualTypeMap.get(typeParameter);
        } else if (typeParameter instanceof ParameterizedType) {
            return makeParameterizedType(extractClass(typeParameter),
                    ((ParameterizedType) typeParameter).getActualTypeArguments(), actualTypeMap);
        } else if (typeParameter instanceof GenericArrayType) {
            return GenericArrayTypeImpl
                    .make(getActualType(((GenericArrayType) typeParameter).getGenericComponentType(), actualTypeMap));
        }
        return typeParameter;
    }


    /**
     * 获取bean构造函数
     *
     * @param clazz      类型
     * @param paramTypes 构造函数类型
     * @param <T>        范型
     * @return 构造函数
     */
    public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
        try {
            return clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * 判断类型是否是 基础类型的包装类
     *
     * @param clazz Class
     * @return true
     * @see Boolean
     * @see Character
     * @see Byte
     * @see Short
     * @see Integer
     * @see Long
     * @see Float
     * @see Double
     * @see Void
     * @since 1.2.5
     */
    public static boolean isPrimitiveTypeWrapClass(Class<?> clazz) {
        if (Boolean.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)
                || Byte.class.isAssignableFrom(clazz) || Short.class.isAssignableFrom(clazz)
                || Integer.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)
                || Float.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)
                || Void.class.isAssignableFrom(clazz)) {
            return true;
        }

        return false;
    }

    /**
     * 是否是基础类型
     *
     * @param clazz 类型
     * @return 是否
     */
    public static boolean isPrimitiveTypeClass(Class<?> clazz) {
        return primitiveTypeToWrapperMap.containsKey(clazz);
    }

    /**
     * 是否是基础类型或着基础类型包装类
     *
     * @param clazz clazz
     * @return isPrimitiveTypeOrWrapClass
     */
    public static boolean isPrimitiveTypeOrWrapClass(Class<?> clazz) {
        return isPrimitiveTypeWrapClass(clazz) || isPrimitiveTypeClass(clazz);
    }

    /**
     * 判断对象是否Bean
     * <p>
     * 如下情况不是：
     * </p>
     * <p>
     * 1、继承了 Number
     * </p>
     * <p>
     * 2、继承了 Date
     * <p>
     * 3、继承了 CharSequence
     * </p>
     *
     * <p>
     * 4、基础类型的type，如：Integer.TYPE ， Long.TYPE
     * </p>
     *
     * @param clazz Class
     * @return true is class is bean
     * @since 1.2.5
     */
    public static boolean isBean(Class<?> clazz) {
        if (isPrimitiveTypeWrapClass(clazz) || Number.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || Temporal.class.isAssignableFrom(clazz)) {
            return false;
        }

        return !clazz.isPrimitive()
                && (clazz.getClassLoader() != null && Objects.equals(clazz.getClassLoader(), APP_CLASS_LOADER));
    }

    /**
     * 是否是多元素数据结构类型
     *
     * @param clazz 类型
     * @return 是否
     */
    public static boolean isMulti(Class<?> clazz) {
        return isArray(clazz) || isCollection(clazz) || isMap(clazz);
    }

    /**
     * 是否是数组类型
     *
     * @param type 类型
     * @return 是否
     */
    public static boolean isArray(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).isArray();
        } else {
            return type instanceof ParameterizedType ? isArray(((ParameterizedType) type).getRawType())
                    : type instanceof GenericArrayType;
        }
    }

    /**
     * 是否是集合类
     *
     * @param clazz clazz
     * @return isCollection
     */
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 是否是可遍历的
     *
     * @param clazz clazz
     * @return isIterable
     */
    public static boolean isIterable(Class<?> clazz) {
        return Iterable.class.isAssignableFrom(clazz);
    }

    /**
     * 是否是map
     *
     * @param clazz clazz
     * @return isMap
     */
    public static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }


    /**
     * 是否是json
     *
     * @param str str
     * @return 是否是json
     */
    public static boolean isJson(String str) {
        return isJsonObject(str) || isJsonArray(str);
    }

    /**
     * 是否是json对象
     *
     * @param str str
     * @return 是否是json对象
     */
    public static boolean isJsonObject(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.startsWith("{") && str.endsWith("}");
    }

    /**
     * 是否是json数组
     *
     * @param str str
     * @return 是否是json数组
     */
    public static boolean isJsonArray(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.startsWith("[") && str.endsWith("]");
    }

    /**
     * 是否是OBJECT类型
     *
     * @param type 类型
     * @return 是否
     */
    public static boolean isObjectType(Type type) {
        return OBJECT_CLASS.equals(type);
    }
}