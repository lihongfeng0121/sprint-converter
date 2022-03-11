package com.sprint.common.converter.conversion.nested.bean;

import com.sprint.common.converter.TypeReference;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.bean.introspection.CachedIntrospectionResults;
import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Assert;
import com.sprint.common.converter.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bean操作工具
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2021年11月22日
 */
public final class Beans {

    private static final Logger logger = LoggerFactory.getLogger(Beans.class);

    private static final String[] STRING_ARRAY = new String[0];

    private static final Pattern POINT_PATTERN = Pattern.compile("\\.(\\w)");

    private static final Function<String, String[]> splitter = (str) -> str == null ? new String[]{}
            : str.split("\\.");
    private static final Function<String[], String> joiner = (array) -> array == null ? null
            : Stream.of(array).filter(Objects::nonNull).collect(Collectors.joining("."));

    private Beans() {
    }

    /**
     * 实例化
     * <p>
     * clazz 不能为接口，抽象类，无参数构造方法
     * </p>
     *
     * @param clazz 被实例化的类
     * @param <T>   类范型
     * @return 实例化后的对象
     * @throws BeansException bean异常
     */
    public static <T> T instance(Class<T> clazz) throws BeansException {
        if (clazz == null) {
            throw new BeansException("Class must not be null");
        }
        if (clazz.isInterface()) {
            throw new BeansException("Specified class [" + clazz + "] is an interface");
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeansException("instance error", e);
        }
    }

    /**
     * 实例化集合对象
     *
     * @param type 被实例化的类
     * @param <T>  类范型
     * @return 实例化后的对象
     */
    public static <T> Collection<T> instanceCollection(Type type) {
        Class<?> rawClass = Types.extractClass(type);
        if (Types.getConstructorIfAvailable(rawClass) != null) {
            return (Collection<T>) instance(rawClass);
        }
        if (List.class.isAssignableFrom(rawClass)) {
            if (rawClass.isInterface()) {
                return new ArrayList<>();
            } else if (ArrayList.class.isAssignableFrom(rawClass)) {
                return new ArrayList<>();
            } else if (LinkedList.class.isAssignableFrom(rawClass)) {
                return new LinkedList<>();
            } else {
                return new ArrayList<>();
            }
        } else if (Set.class.isAssignableFrom(rawClass)) {
            if (LinkedHashSet.class.isAssignableFrom(rawClass)) {
                return new LinkedHashSet<>();
            } else if (EnumSet.class.isAssignableFrom(rawClass)) {
                Type itemType;
                if (type instanceof ParameterizedType) {
                    itemType = ((ParameterizedType) type).getActualTypeArguments()[0];
                } else {
                    itemType = Types.OBJECT_CLASS;
                }
                return (Collection<T>) EnumSet.noneOf((Class<Enum>) itemType);
            } else {
                return new HashSet<>();
            }
        } else if (Queue.class.isAssignableFrom(rawClass)) {
            return new LinkedList<>();
        } else if (rawClass.isInterface()) {
            return new LinkedList<>();
        } else {
            return (Collection<T>) instance(rawClass);
        }
    }

    /**
     * 创建一个Map
     *
     * @param mapClass mapClass
     * @param <K>      k
     * @param <V>      v
     * @return map
     */
    public static <K, V> Map<K, V> instanceMap(Class<?> mapClass) {
        Map<K, V> newMap;
        if (Types.getConstructorIfAvailable(mapClass) != null) {
            newMap = (Map<K, V>) instance(mapClass);
        } else if (HashMap.class.isAssignableFrom(mapClass)) {
            newMap = new HashMap<>();
        } else if (ConcurrentMap.class.isAssignableFrom(mapClass)) {
            newMap = new ConcurrentHashMap<>();
        } else if (LinkedHashMap.class.isAssignableFrom(mapClass)) {
            newMap = new LinkedHashMap<>();
        } else {
            newMap = new LinkedHashMap<>();
        }
        return newMap;
    }

    /**
     * 设置对象属性
     * <p>
     * 如果属性不存在，抛出异常BeansException
     * </p>
     *
     * @param obj          对象
     * @param propertyName 属性名称
     * @param value        值
     * @param ignored      ignored
     * @throws BeansException 异常信息
     */
    public static void setProperty(Object obj, String propertyName, Object value, boolean ignored)
            throws BeansException {
        setProperty(obj, propertyName, value, true, ignored);
    }

    /**
     * 设置对象属性
     * <p>
     * 如果属性不存在，抛出异常BeansException
     * </p>
     *
     * @param obj          对象
     * @param propertyName 属性名称
     * @param value        值
     * @throws BeansException 异常信息
     */
    public static void setProperty(Object obj, String propertyName, Object value) throws BeansException {
        setProperty(obj, propertyName, value, true, false);
    }

    /**
     * 设置对象属性
     *
     * @param obj          对象
     * @param propertyName 属性名称
     * @param value        值
     * @param convert      是否转换
     * @param ignored      属否忽略异常
     * @throws BeansException e
     */
    public static void setProperty(Object obj, String propertyName, Object value, boolean convert, boolean ignored)
            throws BeansException {
        setProperty(null, obj, propertyName, value, convert, ignored);
    }

    /**
     * 设置对象属性
     *
     * @param objType      对象类型
     * @param obj          对象
     * @param propertyName 属性名称
     * @param value        值
     * @param ignored      属否忽略异常
     * @param convert      convert
     * @throws BeansException e
     */
    public static void setProperty(Type objType, Object obj, String propertyName, Object value, boolean convert,
                                   boolean ignored) throws BeansException {
        try {
            String[] propertyCascade = splitter.apply(propertyName);
            // 仅一级属性
            if (propertyCascade.length == 1) {
                PropertyAccess desc = CachedIntrospectionResults.forClass(obj.getClass())
                        .getPropertyAccess(propertyName);
                if (desc != null) {
                    doSetProperty(objType, desc, obj, value, convert);
                } else if (obj instanceof Map) {
                    doSetMapProperty(objType, (Map<String, Object>) obj, propertyName, value, convert);
                }
            } else {// 多级属性设置
                String property = propertyCascade[0];

                Object back = getProperty(obj, property);
                Type propertyBeanType = null;
                PropertyAccess desc = CachedIntrospectionResults.forClass(obj.getClass()).getPropertyAccess(property);
                if (desc != null) {
                    propertyBeanType = desc.getType();
                } else if (Types.isMap(obj.getClass())) {
                    propertyBeanType = Types.getMapKVType(objType)[1];
                }
                // 如果该层级属性为null则实例化一个对象
                if (back == null) {
                    Class<?> propertyClass = Types.extractClass(propertyBeanType,
                            objType == null ? obj.getClass() : objType);
                    if (Types.isMap(propertyClass)) {
                        back = instanceMap(propertyClass);
                    } else if (Types.isCollection(propertyClass)) {
                        back = instanceCollection(propertyClass);
                    } else if (Types.isBean(propertyClass)) {
                        back = instance(propertyClass);
                    } else {
                        back = new LinkedHashMap<>(1);
                    }
                    setProperty(objType, obj, property, back, false, ignored);
                }
                setProperty(propertyBeanType, back,
                        joiner.apply(Arrays.copyOfRange(propertyCascade, 1, propertyCascade.length)), value, convert,
                        ignored);
            }
        } catch (Exception ex) {
            if (!ignored) {
                throw new BeansException("Could not set value of property '" + propertyName
                        + "' from source to target [" + obj.getClass() + "]", ex);
            } else {
                logger.warn("Could not set value of property '" + propertyName + "' from source to target ["
                        + obj.getClass() + "] error message : {}", ex.getMessage());
            }
        }
    }

    private static void doSetProperty(Type beanType, PropertyAccess desc, Object obj, Object value, boolean convert)
            throws ConversionException {
        if (value == null) {
            desc.setValue(obj, null);
        } else {
            if (convert) {
                Object targetValue = NestedConverters.convert(value, beanType == null ? desc.getBeanClass() : beanType,
                        desc.getType());
                if (targetValue != null) {
                    desc.setValue(obj, targetValue);
                }
            } else {
                if (desc.extractClass().isInstance(value)) {
                    desc.setValue(obj, value);
                }
            }
        }
    }

    private static void doSetMapProperty(Type beanType, Map<String, Object> map, String propertyName, Object value,
                                         boolean convert) throws ConversionException {
        if (value == null) {
            map.put(propertyName, null);
        } else {
            if (convert) {
                Type valueType = Types.getMapKVType(beanType)[1];
                Object targetValue = NestedConverters.convert(value, beanType == null ? Map.class : beanType,
                        valueType);
                if (targetValue != null) {
                    map.put(propertyName, targetValue);
                }
            } else {
                map.put(propertyName, value);
            }
        }
    }

    /**
     * 获取对象属性
     *
     * @param obj          对象
     * @param propertyName 属性名
     * @return property
     */
    public static Object getProperty(Object obj, String propertyName) {
        String[] propertyCascade = splitter.apply(propertyName);
        if (propertyCascade.length == 1) {
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                if (map.containsKey(propertyName)) {
                    return map.get(propertyName);
                } else {
                    return doGetProperty(obj, propertyName);
                }
            } else {
                return doGetProperty(obj, propertyName);
            }
        } else {
            String property = propertyCascade[0];
            Object back;
            if (obj instanceof Map) {
                Map map = ((Map) obj);
                Object res = map.get(propertyName);
                if (res != null) {
                    return res;
                }
                if (map.containsKey(property)) {
                    back = map.get(property);
                } else {
                    back = doGetProperty(obj, property);
                }
            } else {
                back = doGetProperty(obj, property);
            }
            if (back == null) {
                return null;
            }
            return getProperty(back, joiner.apply(Arrays.copyOfRange(propertyCascade, 1, propertyCascade.length)));
        }
    }

    private static Object doGetProperty(Object obj, String propertyName) {
        String name = propertyName;
        int index = -1;
        if (propertyName.contains("[") && propertyName.endsWith("]")) {
            name = propertyName.substring(0, propertyName.indexOf("["));
            index = Integer.parseInt(propertyName.substring(propertyName.indexOf("[") + 1, propertyName.indexOf("]")));
        }
        CachedIntrospectionResults introspectionResults = CachedIntrospectionResults.forClass(obj.getClass());
        PropertyAccess desc = introspectionResults.getPropertyAccess(name);
        if (desc == null) {
            logger.warn("class {} 's propertyName:{} not exit!", obj.getClass().getName(), propertyName);
            return null;
        }
        Object val = desc.getValue(obj);

        if (val != null && index > -1 && Types.isArray(val.getClass())) {
            int length = Array.getLength(val);
            return index < length ? Array.get(val, index) : null;
        }

        if (val != null && index > -1 && Types.isCollection(val.getClass())) {
            Collection<?> collection = (Collection<?>) val;
            int length = collection.size();
            return index < length ? new ArrayList<>(collection).get(index) : null;
        }

        return val;
    }

    /**
     * 设置对象属性
     *
     * @param obj   对象
     * @param props 属性
     */
    public static void setProperties(Object obj, Map<String, Object> props) {
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            setProperty(obj, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取属性的类型
     *
     * @param clazz        目标类型
     * @param propertyName 属性名称
     * @return 属性的类型
     */
    public static Class<?> getPropertyType(Class<?> clazz, String propertyName) {
        Assert.notNull(clazz, "clazz can't be null");
        Assert.notNull(propertyName, "propertyName can't be null");
        try {
            CachedIntrospectionResults introspectionResults = CachedIntrospectionResults.forClass(clazz);
            String[] propertyCascade = splitter.apply(propertyName);
            Class<?> type;
            if (propertyCascade.length == 1) {
                PropertyAccess desc = introspectionResults.getPropertyAccess(propertyName);
                type = desc.extractClass();
            } else {
                String property = propertyCascade[0];
                PropertyAccess desc = introspectionResults.getPropertyAccess(property);
                type = getPropertyType(desc.extractClass(),
                        joiner.apply(Arrays.copyOfRange(propertyCascade, 1, propertyCascade.length)));
            }

            return type;
        } catch (Exception e) {
            throw new BeansException("Could not get value of property '" + propertyName + "' from source to target", e);
        }
    }

    /**
     * 对象转换
     *
     * @param source 源对象
     * @param clazz  目标类
     * @param <T>    目标类范型
     * @return 返回目标类对象
     * @throws BeansException e
     */
    public static <T> T cast(Object source, Class<T> clazz) throws BeansException {
        return cast(source, clazz, STRING_ARRAY);
    }

    /**
     * 对象转换
     *
     * @param source           源对象
     * @param clazz            目标类
     * @param ignoreProperties 忽略拷贝属性
     * @param <T>              目标类范型
     * @return 返回目标类对象
     * @throws BeansException e
     */
    public static <T> T cast(Object source, Class<T> clazz, String... ignoreProperties) throws BeansException {
        return cast(source, clazz, true, ignoreProperties);
    }

    /**
     * 对象转换(浅拷贝，无转换)
     *
     * @param source 源对象
     * @param clazz  目标类
     * @param <T>    目标类范型
     * @return 返回目标类对象
     * @throws BeansException e
     */
    public static <T> T shallowCast(Object source, Class<T> clazz) throws BeansException {
        return cast(source, clazz, false, STRING_ARRAY);
    }

    /**
     * 对象转换(浅拷贝，无转换)
     *
     * @param source           源对象
     * @param clazz            目标类
     * @param ignoreProperties 忽略拷贝属性
     * @param <T>              目标类范型
     * @return 返回目标类对象
     * @throws BeansException e
     */
    public static <T> T shallowCast(Object source, Class<T> clazz, String... ignoreProperties) throws BeansException {
        return cast(source, clazz, false, ignoreProperties);
    }

    /**
     * 对象转换
     *
     * @param source           源对象
     * @param clazz            目标类
     * @param convert          是否转化
     * @param ignoreProperties 忽略拷贝属性
     * @param <T>              目标类范型
     * @return 返回目标类对象
     * @throws BeansException e
     */
    public static <T> T cast(Object source, Class<T> clazz, boolean convert, String... ignoreProperties)
            throws BeansException {
        return (T) doCast(source, clazz, convert, ignoreProperties);
    }

    /**
     * 对象转换
     *
     * @param source           源对象
     * @param targetType       目标类
     * @param convert          是否转化
     * @param ignoreProperties 忽略拷贝属性
     * @param <T>              目标类范型
     * @return 返回目标类对象
     * @throws BeansException e
     */
    public static <T> T cast(Object source, Type targetType, boolean convert, String... ignoreProperties)
            throws BeansException {
        return (T) doCast(source, targetType, convert, ignoreProperties);
    }

    /**
     * 对象转换
     *
     * @param source           源对象
     * @param targetType       目标类
     * @param convert          是否转化
     * @param ignoreProperties 忽略拷贝属性
     * @param <T>              目标类范型
     * @return 返回目标类对象
     * @throws BeansException e
     */
    public static <T> T cast(Object source, TypeReference<T> targetType, boolean convert, String... ignoreProperties)
            throws BeansException {
        return (T) doCast(source, targetType.getType(), convert, ignoreProperties);
    }

    static Object doCast(Object source, Type clazz, boolean convert, String... ignoreProperties) throws BeansException {
        Assert.notNull(source, "source con't be null");
        Object target = instance(Types.extractClass(clazz));
        copyProperties(source, target, clazz, true, convert, ignoreProperties);
        return target;
    }

    /**
     * 拷贝对象属性
     * <p>
     * sourceProps 与 targetProps 数量要保持一致，属性设置按顺序设置
     * </p>
     *
     * @param source      源对象
     * @param sourceProps 源属性
     * @param target      目标对象
     * @param targetProps 目标属性
     * @throws BeansException 对象异常
     */
    public static void copyProperties(Object source, String[] sourceProps, Object target, String[] targetProps)
            throws BeansException {
        if (sourceProps != null && sourceProps.length != 0 && targetProps != null && targetProps.length != 0) {
            if (sourceProps.length != targetProps.length) {
                throw new IllegalArgumentException("length is not same");
            } else {
                for (int i = 0; i < sourceProps.length; ++i) {
                    String sourceProp = sourceProps[i];
                    String targetProp = targetProps[i];
                    Object value = getProperty(source, sourceProp);
                    if (value != null) {
                        setProperty(target, targetProp, value);
                    }
                }
            }
        } else {
            throw new BeansException("sourceProps targetProps is null");
        }
    }

    /**
     * 拷贝对象属性
     *
     * @param source 源对象
     * @param target 目标对象
     * @throws BeansException 对象异常
     */
    public static void copyProperties(Object source, Object target) throws BeansException {
        copyProperties(source, target, STRING_ARRAY);
    }

    /**
     * 拷贝对象属性
     *
     * @param source 源对象
     * @param target 目标对象
     * @param merge  是否合并属性
     * @throws BeansException 对象异常
     */
    public static void copyProperties(Object source, Object target, boolean merge) throws BeansException {
        copyProperties(source, target, merge, STRING_ARRAY);
    }

    /**
     * 拷贝对象属性
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略拷贝属性
     * @throws BeansException 对象异常
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        copyProperties(source, target, true, ignoreProperties);
    }

    /**
     * 拷贝对象属性(深拷贝+属性转化)
     *
     * @param source           源对象
     * @param target           目标对象
     * @param merge            是否合并属性
     * @param ignoreProperties 忽略拷贝属性
     * @throws BeansException 对象异常
     */
    public static void copyProperties(Object source, Object target, boolean merge, String... ignoreProperties)
            throws BeansException {
        copyProperties(source, target, null, merge, true, ignoreProperties);
    }

    /**
     * 拷贝对象属性（浅拷贝，无属性转化）
     *
     * @param source 源对象
     * @param target 目标对象
     * @throws BeansException 对象异常
     */
    public static void shallowCopyProperties(Object source, Object target) {
        copyProperties(source, target, STRING_ARRAY);
    }

    /**
     * 拷贝对象属性（浅拷贝，无属性转化）
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略拷贝属性
     * @throws BeansException 对象异常
     */
    public static void shallowCopyProperties(Object source, Object target, String... ignoreProperties) {
        copyProperties(source, target, null, true, false, ignoreProperties);
    }

    /**
     * 拷贝对象属性（浅拷贝，无属性转化）
     *
     * @param source           源对象
     * @param target           目标对象
     * @param merge            merge
     * @param ignoreProperties 忽略拷贝属性
     * @throws BeansException 对象异常
     */
    public static void shallowCopyProperties(Object source, Object target, boolean merge, String... ignoreProperties) {
        copyProperties(source, target, null, merge, false, ignoreProperties);
    }

    /**
     * 拷贝对象属性（浅拷贝，无属性转化）
     *
     * @param source           源对象
     * @param target           目标对象
     * @param targetType       目标类型
     * @param merge            是否合并
     * @param ignoreProperties 忽略拷贝属性
     * @throws BeansException 对象异常
     */
    public static void shallowCopyProperties(Object source, Object target, Type targetType, boolean merge,
                                             String... ignoreProperties) {
        copyProperties(source, target, targetType, merge, false, ignoreProperties);
    }

    /**
     * 拷贝对象属性
     *
     * @param source           源对象
     * @param target           目标对象
     * @param targetType       目标类型
     * @param merge            是否合并属性
     * @param convert          是否转化
     * @param ignoreProperties 忽略拷贝属性
     * @throws BeansException 对象异常
     */
    public static void copyProperties(Object source, Object target, Type targetType, boolean merge, boolean convert,
                                      String... ignoreProperties) throws BeansException {
        copyProperties(source, target, targetType, merge, convert, true, ignoreProperties);
    }

    /**
     * 拷贝对象属性
     *
     * @param source           源对象
     * @param target           目标对象
     * @param merge            是否合并属性
     * @param convert          是否转化
     * @param supportMapKV     是否支持map
     * @param ignoreProperties 忽略拷贝属性
     * @throws BeansException 对象异常
     */
    public static void copyProperties(Object source, Object target, boolean merge, boolean convert,
                                      boolean supportMapKV, String... ignoreProperties) throws BeansException {
        copyProperties(source, target, null, merge, convert, supportMapKV, ignoreProperties);
    }

    /**
     * 拷贝对象属性
     *
     * @param source           源对象
     * @param target           目标对象
     * @param targetType       目标类型
     * @param merge            是否合并属性
     * @param convert          是否转化
     * @param ignoreProperties 忽略拷贝属性
     * @param supportMapKV     map kv
     * @throws BeansException 对象异常
     */
    public static void copyProperties(Object source, Object target, Type targetType, boolean merge, boolean convert,
                                      boolean supportMapKV, String... ignoreProperties) throws BeansException {
        Assert.notNull(source, "source con't be null");
        Assert.notNull(target, "target con't be null");
        String[][] mapper = Properties.getCommonPropertyNameMapper(source, target, supportMapKV, ignoreProperties);
        copyProperties(source, target, targetType, mapper[0], mapper[1], merge, convert);
    }

    /**
     * 拷贝对象属性
     *
     * @param source      源对象
     * @param target      目标对象
     * @param targetType  目标对象类型
     * @param sourceProps 源对象属性
     * @param alias       目标对象属性
     * @param merge       是否合并属性
     * @param convert     是否转化
     * @throws BeansException e
     */
    public static void copyProperties(Object source, Object target, Type targetType, String[] sourceProps,
                                      String[] alias, boolean merge, boolean convert) throws BeansException {
        Assert.notNull(source, "source con't be null");
        Assert.notNull(target, "target con't be null");
        Assert.notNull(sourceProps, "sourceProps con't be null");
        Assert.notNull(sourceProps, "alias con't be null");
        Assert.isTrue(sourceProps.length == alias.length, "sourceProps alias length not eq");

        for (int i = 0; i < alias.length; i++) {
            String aliaProp = alias[i];
            String sourceProp = sourceProps[i];
            Object value = getProperty(source, sourceProp);
            if (merge) {
                if (value != null) {
                    setProperty(targetType, target, aliaProp, value, convert, true);
                }
            } else {
                setProperty(targetType, target, aliaProp, value, convert, true);
            }
        }
    }

    /**
     * 展开内部属性
     *
     * @param source           源头
     * @param ignoreProperties 忽略属性
     * @return 平铺后map
     * <p>
     * public class Test {
     * private String text;
     * private Inner inner;
     * }
     * <p>
     * public class Inner {
     * private String inner;
     * private String inner2;
     * }
     * <p>
     * Beans.unfold(test) sout: {innerInner=i1, innerInner2=i2, text=zhangsan}
     * </p>
     */
    public static Map<String, Object> unfold(Object source, String... ignoreProperties) {
        List<String> propertyNames = Properties.getReadPropertyNames(source, true);
        Map<String, Object> map = new HashMap<>(propertyNames.size());
        propertyNames.stream().filter(property -> !contained(ignoreProperties, property))
                .forEach(property -> map.put(pointToCamel(property), getProperty(source, property)));
        return map;
    }

    static <T> boolean contained(String[] ts, String t) {
        if (ts != null && t != null) {
            int length = ts.length;
            for (int i = 0; i < length; ++i) {
                String obj = ts[i];
                if (obj != null && obj.equals(t)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    static String pointToCamel(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }
        Matcher matcher = POINT_PATTERN.matcher(source);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * bean 转 map
     *
     * <p>
     * sourceProps 与 alias 数量要保持一致，属性设置按顺序设置
     * </p>
     *
     * @param source         源对象
     * @param sourceProps    需要转化的属性名称
     * @param alias          需要转化的key值
     * @param filterCallback 过滤器
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static Map<String, Object> toMapWithFilter(Object source, String[] sourceProps, String[] alias,
                                                      FilterCallback filterCallback) throws BeansException {
        Map<String, Object> result = new HashMap<>();
        if (sourceProps != null && sourceProps.length != 0) {
            for (int i = 0; i < sourceProps.length; ++i) {
                String sourceProp = sourceProps[i];
                Object value = null;
                int index = sourceProp.indexOf("[]");
                if (index <= 0) {
                    value = getProperty(source, sourceProp);
                }

                if (filterCallback != null) {
                    value = filterCallback.filter(sourceProp, value);
                }

                if (value != null) {
                    String prop = sourceProp;
                    if (alias != null && alias.length > i) {
                        prop = alias[i];
                    }

                    result.put(prop, value);
                }
            }

            return result;
        } else {
            throw new BeansException("sourceProps  is null");
        }
    }

    /**
     * bean 转 map
     *
     * <p>
     * sourceProps 与 alias 数量要保持一致，属性设置按顺序设置
     * </p>
     *
     * @param source      源对象
     * @param sourceProps 需要转化的属性名称
     * @param alias       需要转化的key值
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static Map<String, Object> toMap(Object source, String[] sourceProps, String[] alias) {
        return toMapWithFilter(source, sourceProps, alias, null);
    }

    /**
     * bean 转 map
     *
     * @param source 源对象
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static Map<String, Object> toMap(Object source) {
        String[][] mapper = Properties.getCommonPropertyNameMapper(source, Collections.emptyMap(), true);
        return toMap(source, mapper[0], mapper[1]);
    }

    /**
     * bean 转 map
     *
     * @param source      源对象
     * @param sourceProps 需要转化的属性名称
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static Map<String, Object> toMap(Object source, String[] sourceProps) {
        return toMap(source, sourceProps, null);
    }

    /**
     * 集合Bean 转 集合map
     *
     * <p>
     * sourceProps 与 alias 数量要保持一致，属性设置按顺序设置
     * </p>
     *
     * @param source         源对象
     * @param sourceProps    需要转化的属性名称
     * @param alias          需要转化的key值
     * @param filterCallback 过滤器
     * @param other          other
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static List<Map<String, Object>> toMap(Collection<?> source, String[] sourceProps, String[] alias,
                                                  Map<String, Object> other, FilterCallback filterCallback) {
        List<Map<String, Object>> list = new LinkedList<>();

        for (Object object : source) {
            Map<String, Object> map = toMapWithFilter(object, sourceProps, alias, filterCallback);
            if (other != null) {
                map.putAll(other);
            }
            list.add(map);
        }

        return list;
    }

    /**
     * 集合Bean 转 集合map
     *
     * <p>
     * sourceProps 与 targetProps 数量要保持一致，属性设置按顺序设置
     * </p>
     *
     * @param source      源对象
     * @param sourceProps 需要转化的属性名称
     * @param alias       需要转化的key值
     * @param other       other
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static List<Map<String, Object>> toMap(Collection<?> source, String[] sourceProps, String[] alias,
                                                  Map<String, Object> other) {
        return toMap(source, sourceProps, alias, other, null);
    }

    /**
     * 集合Bean 转 集合map
     *
     * <p>
     * sourceProps 与 targetProps 数量要保持一致，属性设置按顺序设置
     * </p>
     *
     * @param source      源对象
     * @param sourceProps 需要转化的属性名称
     * @param alias       需要转化的key值
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static List<Map<String, Object>> toMap(Collection<?> source, String[] sourceProps, String[] alias) {
        return toMap(source, sourceProps, alias, null);
    }

    /**
     * 集合Bean 转 集合map
     *
     * <p>
     * sourceProps 与 targetProps 数量要保持一致，属性设置按顺序设置
     * </p>
     *
     * @param source      源对象
     * @param sourceProps 需要转化的属性名称
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static List<Map<String, Object>> toMap(Collection<?> source, String[] sourceProps) {
        List<Map<String, Object>> list = new LinkedList<>();

        for (Object object : source) {
            list.add(toMap(object, sourceProps, null));
        }

        return list;
    }

    /**
     * 集合Bean 转 List
     *
     * <p>
     * sourceProps 与 targetProps 数量要保持一致，属性设置按顺序设置
     * </p>
     *
     * @param source 源对象
     * @param prop   需要转化的属性名称
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static List<Object> toList(Collection<?> source, String prop) {
        List<Object> list = new LinkedList<>();
        for (Object object : source) {
            Object value = getProperty(object, prop);
            list.add(value);
        }

        return list;
    }

    /**
     * 集合Bean 转 List
     *
     * @param source     源对象
     * @param propGetter 需要转化的属性名称
     * @param <S>        s
     * @param <T>        t
     * @return 转换后的map对象
     * @throws BeansException e
     */
    public static <S, T> List<T> toList(Collection<S> source, Function<S, T> propGetter) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        return source.stream().map(propGetter).collect(Collectors.toList());
    }
}
