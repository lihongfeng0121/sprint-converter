package com.sprint.common.converter.util;

import java.lang.reflect.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * 类操作工具
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月11日
 */
public class Types {

    private static final char PACKAGE_SEPARATOR = '.';

    public static final String ARRAY_SUFFIX = "[]";
    private static final String INTERNAL_ARRAY_PREFIX = "[";

    private static final String INTERNAL_ARRAY_SUFFIX = "]";

    public static final String JSON_OBJECT_PREFIX = "{";
    public static final String JSON_OBJECT_SUFFIX = "}";

    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

    private static final char INNER_CLASS_SEPARATOR = '$';

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);
    private static final Map<String, Class<?>> commonClassCache = new HashMap<>(32);

    public static final Class<?> OBJECT_CLASS = Object.class;

    public static final String[] COLLECTION_IGNORES = {"size", "empty"};

    public static final String[] MAP_IGNORES = {"size", "empty"};
    private static final ClassLoader APP_CLASS_LOADER = ClassLoader.getSystemClassLoader();
    public static final GenericsResolver COLLECTION_GENERICS_RESOLVER = GenericsResolver.of(Collection.class);
    public static final GenericsResolver MAP_GENERICS_RESOLVER = GenericsResolver.of(Map.class);


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


    /**
     * 获取包装类的基本类型
     *
     * @param name 类名字
     * @return 包装类的基本类型
     */
    public static Class<?> resolvePrimitiveClassName(String name) {
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name != null && name.length() <= 8) {
            // Could be a primitive - likely.
            return primitiveTypeNameMap.get(name);
        }
        return null;
    }

    /**
     * 是否存在类
     *
     * @param className   类名
     * @param classLoader 类加载器
     * @return 是否存在。
     */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        } catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }

    /**
     * 获取类
     *
     * @param name        类名
     * @param classLoader 类加载器
     * @return 类
     * @throws ClassNotFoundException 异常
     * @throws LinkageError           异常
     */
    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = commonClassCache.get(name);
        }
        if (clazz != null) {
            return clazz;
        }
        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[[I" or "[[Ljava.lang.String;" style arrays
        if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader clToUse = classLoader;
        if (clToUse == null) {
            clToUse = getDefaultClassLoader();
        }
        try {
            return (clToUse != null ? clToUse.loadClass(name) : Class.forName(name));
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
            if (lastDotIndex != -1) {
                String innerClassName =
                        name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);
                try {
                    return (clToUse != null ? clToUse.loadClass(innerClassName) : Class.forName(innerClassName));
                } catch (ClassNotFoundException ignored) {
                }
            }
            throw ex;
        }
    }


    /**
     * 获取默认类加载器
     *
     * @return 默认类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignored) {
        }
        if (cl == null) {
            cl = Types.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignored) {
                }
            }
        }
        return cl;
    }


    /**
     * 获取所有成员变量
     *
     * @param clazz  对象类
     * @param filter 过滤
     * @return map
     */
    public static Map<String, Field> getFields(Class<?> clazz, BiPredicate<String, Field> filter) {
        Map<String, Field> fieldMap = new HashMap<>();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String propertyName = field.getName();
            if (filter == null || filter.test(propertyName, field)) {
                fieldMap.put(propertyName, field);
            }
        }
        return fieldMap;
    }

    /**
     * Retrieves a declared field from the class and its superclasses.
     *
     * @param clazz The {@link Class} object to retrieve the field from.
     * @param fieldName The name of the field to retrieve.
     * @return The {@link Field} object representing the declared field.
     * @throws NoSuchFieldException If the field does not exist in the class or any of its superclasses.
     */
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
     * 获取所有含注解可访问的成员变量
     *
     * @param clazz  对象类
     * @param filter 过滤
     * @return 可访问的成员变量
     */
    public static List<Field> getDeclaredFields(Class<?> clazz, BiPredicate<String, Field> filter) {
        return new ArrayList<>(getDeclaredFieldMap(clazz, filter).values());
    }

    /**
     * 获取所有可访问的成员变量
     *
     * @param clazz  对象类
     * @param filter 过滤
     * @return 可访问的成员变量
     */
    public static Map<String, Field> getDeclaredFieldMap(Class<?> clazz, BiPredicate<String, Field> filter) {
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        while (null != clazz) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String propertyName = field.getName();
                if (filter == null || filter.test(propertyName, field)) {
                    fieldMap.put(propertyName, field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fieldMap;
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
            return ((ParameterizedType) type).getActualTypeArguments();
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
        return Miscs.at(getClassSuperclassType(clazz), lc, null);
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
        return Miscs.at(getInterfaceSuperclass(clazz, intf), lc, null);
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
     * @param declaringType 类
     * @param rawType       类
     * @return ComponentType
     */
    public static Type getActualType(Type declaringType, Type rawType) {
        if (rawType instanceof TypeVariable) {
            return resolveVariableTypeMap(declaringType).get(rawType);
        } else {
            return rawType;
        }
    }

    /**
     * 获取类的范型
     *
     * @param type 类
     * @return ClassTypeMap
     */
    public static Map<TypeVariable<?>, Type> resolveVariableTypeMap(Type type) {
        Class<?> clazz = extractClass(type);
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + clazz + "] is an interface");
        }
        return Maps.toMap(clazz.getTypeParameters(), getActualTypeArguments(type));
    }


    /**
     * 获取类的范型
     *
     * @param clazz 类
     * @return ClassSuperclassTypeMap
     */
    public static Map<TypeVariable<?>, Type> resolveSuperclassVariableTypeMap(Class<?> clazz) {
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + clazz + "] is an interface");
        }
        Class<?> superclass = clazz.getSuperclass();
        return Maps.toMap(superclass.getTypeParameters(), getClassSuperclassType(clazz));
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
     * @param rawType       类型
     * @param declaringType 描述类型
     * @return Class对象
     */
    public static Class<?> extractClass(Type rawType, Type declaringType) {
        if (isArray(rawType)) {
            Class<?> componentType = extractClass(getArrayComponentType(rawType, declaringType));
            return Array.newInstance(componentType, 0).getClass();
        } else {
            if (rawType instanceof Class<?>) {
                return (Class<?>) rawType;
            } else if (rawType instanceof ParameterizedType) {
                return extractClass(((ParameterizedType) rawType).getRawType(), declaringType);
            } else if (rawType instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) rawType;
                if (wildcardType.getUpperBounds() != null && wildcardType.getUpperBounds().length > 0) {
                    return extractClass(wildcardType.getUpperBounds()[0], declaringType);
                } else if (wildcardType.getLowerBounds() != null && wildcardType.getLowerBounds().length > 0) {
                    return extractClass(wildcardType.getLowerBounds()[0], declaringType);
                } else {
                    return OBJECT_CLASS;
                }
            } else if (rawType instanceof TypeVariable && declaringType != null) {
                if (declaringType instanceof Class) {
                    return extractClass(resolveSuperclassVariableTypeMap((Class<?>) declaringType).get(rawType), declaringType);
                } else if (declaringType instanceof ParameterizedType) {
                    return extractClass(resolveVariableTypeMap(declaringType).get(rawType), declaringType);
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
     * @param type type
     * @return ArrayComponentType
     */
    public static Type getArrayComponentType(Type type) {
        return getArrayComponentType(type, null);
    }


    /**
     * 获取数组中元素的Class对象
     *
     * @param rawType       rawType
     * @param declaringType declaringType
     * @return ArrayComponentType
     */
    public static Type getArrayComponentType(Type rawType, Type declaringType) {
        if (rawType instanceof Class<?>) {
            return ((Class<?>) rawType).getComponentType();
        } else if (rawType instanceof ParameterizedType) {
            return getArrayComponentType(((ParameterizedType) rawType).getRawType(), declaringType);
        } else if (rawType instanceof GenericArrayType) {
            return extractGenericArrayClass((GenericArrayType) rawType, declaringType);
        } else if (rawType instanceof TypeVariable) {
            return getArrayComponentType(getActualType(declaringType, rawType), declaringType);
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
    private static Type extractGenericArrayClass(GenericArrayType genericArrayType, Type declaringType) {
        Type componentType = genericArrayType.getGenericComponentType();
        if (componentType instanceof Class<?>) {
            return componentType;
        } else if (componentType instanceof ParameterizedType) {
            return componentType;
        } else if (componentType instanceof GenericArrayType) {
            return Array
                    .newInstance(extractClass(extractGenericArrayClass((GenericArrayType) componentType, declaringType)), 0)
                    .getClass();
        } else if (componentType instanceof TypeVariable) {
            if (declaringType instanceof Class) {
                return resolveSuperclassVariableTypeMap((Class<?>) declaringType).get(componentType);
            } else if (declaringType instanceof ParameterizedType) {
                return resolveVariableTypeMap(declaringType).get(componentType);
            } else {
                return OBJECT_CLASS;
            }
        } else {
            return OBJECT_CLASS;
        }
    }

    /**
     * 获取Map key/value 类型
     *
     * @param type type
     * @return key/value 类型
     */
    public static Type[] getMapKVType(Type type) {
        return getMapKVType(null, type);
    }

    /**
     * 获取Map key/value 类型
     *
     * @param declaringType declaringType
     * @param rawType       rawType
     * @return key/value 类型
     */
    public static Type[] getMapKVType(Type declaringType, Type rawType) {
        return MAP_GENERICS_RESOLVER.resolve(declaringType, rawType);
    }

    /**
     * 获取集合元素类型
     *
     * @param type type
     * @return 集合元素类型
     */
    public static Type getCollectionItemType(Type type) {
        return getCollectionItemType(null, type);
    }

    /**
     * 获取集合元素类型
     *
     * @param declaringType declaringType
     * @param rawType       rawType
     * @return 集合元素类型
     */
    public static Type getCollectionItemType(Type declaringType, Type rawType) {
        return COLLECTION_GENERICS_RESOLVER.resolve(declaringType, rawType)[0];
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

    public static Class<?> getPrimitiveTypeClass(Class<?> clazz) {
        return primitiveWrapperTypeMap.get(clazz);
    }

    public static Class<?> getPrimitiveWrapTypeClass(Class<?> clazz) {
        return primitiveTypeToWrapperMap.get(clazz);
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

        return !clazz.isPrimitive() && (clazz.getClassLoader() != null);
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
        return str.startsWith(JSON_OBJECT_PREFIX) && str.endsWith(JSON_OBJECT_SUFFIX);
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
        return str.startsWith(INTERNAL_ARRAY_PREFIX) && str.endsWith(INTERNAL_ARRAY_SUFFIX);
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


    /**
     * make type
     *
     * @param rawType             rawType
     * @param actualTypeArguments actualTypeArguments
     * @param ownerType           ownerType
     * @return ParameterizedType
     */
    public static ParameterizedType makeType(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
        return new ParameterizedTypeImpl(rawType, actualTypeArguments, ownerType);
    }

    /**
     * make map type
     *
     * @param keyType   keyType
     * @param valueType valueType
     * @param <K>       K
     * @param <V>       V
     * @return ParameterizedType
     */
    public static <K, V> ParameterizedType makeMapType(Class<K> keyType, Class<V> valueType) {
        return new ParameterizedTypeImpl(Map.class, new Type[]{keyType, valueType}, null);
    }

    /**
     * make list type
     *
     * @param elementType elementType
     * @param <E>         E
     * @return ParameterizedType
     */
    public static <E> ParameterizedType makeListType(Class<E> elementType) {
        return new ParameterizedTypeImpl(List.class, new Type[]{elementType}, null);
    }

    /**
     * make set type
     *
     * @param elementType elementType
     * @param <E>         E
     * @return ParameterizedType
     */
    public static <E> ParameterizedType makeSetType(Class<E> elementType) {
        return new ParameterizedTypeImpl(Set.class, new Type[]{elementType}, null);
    }

    /**
     * make array type
     *
     * @param genericComponentType genericComponentType
     * @return GenericArrayType
     */
    public static GenericArrayType makeArrayType(Type genericComponentType) {
        return new GenericArrayTypeImpl(genericComponentType);
    }
}