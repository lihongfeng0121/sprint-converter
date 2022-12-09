package com.sprint.common.converter.util;

import com.sprint.common.converter.conversion.nested.bean.Access;
import com.sprint.common.converter.conversion.nested.bean.PropertyAnnotationParser;
import com.sprint.common.converter.conversion.nested.bean.PropertyInfo;
import com.sprint.common.converter.conversion.nested.bean.annotation.DefaultPropertyAnnotationParser;
import com.sprint.common.converter.conversion.nested.bean.introspection.CachedIntrospectionResults;
import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Property工具
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class Properties {

    private static final Logger logger = LoggerFactory.getLogger(Properties.class);

    private static final Set<String> MAP_IGNORE_PROPERTY = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList("size","empty")));

    private static final String[] STRING_ARRAY = new String[0];

    static List<String> doGetPropertyNames(Class<?> clazz, boolean inner) {
        List<String> propertyNames = new LinkedList<>();

        CachedIntrospectionResults introspectionResults = CachedIntrospectionResults.forClass(clazz);
        PropertyAccess[] propertyAccesses = introspectionResults.getPropertyAccesses();

        for (PropertyAccess propertyAccess : propertyAccesses) {
            Class<?> type = propertyAccess.extractClass();
            if (inner && !Types.isIterable(type) && !Types.isMap(type) && Types.isBean(type)) {
                propertyNames.addAll(doGetPropertyNames(type, true).stream()
                        .map(item -> propertyAccess.getName() + "." + item).collect(Collectors.toList()));
            } else {
                propertyNames.add(propertyAccess.getName());
            }
        }

        return propertyNames;
    }

    static List<String> doGetReadPropertyNames(Class<?> clazz, boolean inner) {
        List<String> propertyNames = new LinkedList<>();

        CachedIntrospectionResults introspectionResults = CachedIntrospectionResults.forClass(clazz);
        PropertyAccess[] propertyAccesses = introspectionResults.getPropertyAccesses();

        for (PropertyAccess propertyAccess : propertyAccesses) {
            if (propertyAccess.isReadAccessible()) {
                Class<?> type = propertyAccess.extractClass();
                if (inner && !Types.isIterable(type) && !Types.isMap(type) && Types.isBean(type)) {
                    propertyNames.addAll(doGetReadPropertyNames(type, true).stream()
                            .map(item -> propertyAccess.getName() + "." + item).collect(Collectors.toList()));
                } else {
                    propertyNames.add(propertyAccess.getName());
                }
            }
        }

        return propertyNames;
    }

    static List<String> doGetWritePropertyNames(Class<?> clazz, boolean inner) {
        List<String> propertyNames = new LinkedList<>();

        CachedIntrospectionResults introspectionResults = CachedIntrospectionResults.forClass(clazz);
        PropertyAccess[] propertyAccesses = introspectionResults.getPropertyAccesses();

        for (PropertyAccess propertyAccess : propertyAccesses) {
            if (propertyAccess.isWriteAccessible()) {
                Class<?> type = propertyAccess.extractClass();
                if (inner && !Types.isIterable(type) && !Types.isMap(type) && Types.isBean(type)) {
                    propertyNames.addAll(doGetWritePropertyNames(type, true).stream()
                            .map(item -> propertyAccess.getName() + "." + item).collect(Collectors.toList()));
                } else {
                    propertyNames.add(propertyAccess.getName());
                }
            }
        }

        return propertyNames;
    }

    private static final List<PropertyAnnotationParser<?>> EXT_ANNOTATION_PARSER_LIST = new ArrayList<>();

    private static final PropertyAnnotationParser<?> DEFAULT_ANNOTATION_PARSER = new DefaultPropertyAnnotationParser();

    static {// 通过ServiceLoader 加载service， 可自定义属性解析器 META-INF
        // 增加com.sprint.common.converter.bean.PropertyInfoAnnotationParser 文件
        // 将PropertyInfoAnnotationParser实现类全路径复制到文件内
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (PropertyAnnotationParser<?> propertyAnnotationParser : ServiceLoader
                    .load(PropertyAnnotationParser.class, loader)) {
                EXT_ANNOTATION_PARSER_LIST.add(propertyAnnotationParser);
            }
        } catch (Exception ex) {

        }

    }

    /**
     * 解析Bean属性信息
     *
     * @param propertyAccess propertyAccess
     * @return PropertyInfoHolder
     */
    public static PropertyInfo parsePropertyInfo(
            PropertyAccess propertyAccess) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setPropertyAccess(propertyAccess);
        propertyInfo.setAccess(Access.AUTO);
        propertyInfo.setIndex(-1);
        propertyInfo.setName(propertyAccess.getName());
        if (DEFAULT_ANNOTATION_PARSER.support(propertyAccess)) {
            PropertyInfo parsePropertyInfo = DEFAULT_ANNOTATION_PARSER
                    .parse(propertyAccess.getAnnotation(DEFAULT_ANNOTATION_PARSER.annotationType()));
            if (parsePropertyInfo.getName() != null && !parsePropertyInfo.getName().isEmpty()) {
                propertyInfo.setName(parsePropertyInfo.getName());
            }
            if (!Objects.equals(propertyInfo.getAccess(), Access.AUTO)) {
                propertyInfo.setAccess(parsePropertyInfo.getAccess());
            }
            if (parsePropertyInfo.getIndex() > -1) {
                propertyInfo.setIndex(parsePropertyInfo.getIndex());
            }
        }
        if (!EXT_ANNOTATION_PARSER_LIST.isEmpty()) {
            EXT_ANNOTATION_PARSER_LIST.stream().filter(item -> item.support(propertyAccess)).forEach(item -> {
                PropertyInfo parsePropertyInfo = item
                        .parse(propertyAccess.getAnnotation(item.annotationType()));
                if (parsePropertyInfo.getName() != null && !parsePropertyInfo.getName().isEmpty()) {
                    propertyInfo.setName(parsePropertyInfo.getName());
                }
                if (!Objects.equals(propertyInfo.getAccess(), Access.AUTO)) {
                    propertyInfo.setAccess(parsePropertyInfo.getAccess());
                }
                if (parsePropertyInfo.getIndex() > -1) {
                    propertyInfo.setIndex(parsePropertyInfo.getIndex());
                }
            });
        }
        return propertyInfo;
    }

    /**
     * 获取map的所有key值
     *
     * @param obj obj
     * @return list
     */
    public static List<String> getMapPropertyNames(Map<String, ?> obj) {
        return new ArrayList<>(obj.keySet());
    }

    /**
     * 获取对象所有属性名字
     *
     * @param obj obj
     * @return list
     */
    public static List<String> getPropertyNames(Object obj) {
        return getPropertyNames(obj, false);
    }

    /**
     * 获取对象所有可读属性名字
     *
     * @param obj obj
     * @return list
     */
    public static List<String> getReadPropertyNames(Object obj) {
        return getReadPropertyNames(obj, false);
    }

    /**
     * 获取对象所有可读属性名字
     *
     * @param obj obj
     * @return list
     */
    public static List<PropertyAccess> getReadPropertyAccess(Object obj) {
        CachedIntrospectionResults introspectionResults = CachedIntrospectionResults.forClass(obj.getClass());
        return Arrays.asList(introspectionResults.getReadPropertyAccess());
    }

    /**
     * 获取对象所有可写属性名字
     *
     * @param obj 对象
     * @return list
     */
    public static List<PropertyAccess> getWritePropertyAccess(Object obj) {
        CachedIntrospectionResults introspectionResults = CachedIntrospectionResults.forClass(obj.getClass());
        return Arrays.asList(introspectionResults.getWritePropertyAccess());
    }

    /**
     * 获取所有对象可写属性名字
     *
     * @param obj 对象
     * @return list
     */
    public static List<String> getWritePropertyNames(Object obj) {
        return getWritePropertyNames(obj, false);
    }

    /**
     * 获取对象名字（嵌套获取） eg:test.test.name
     *
     * @param obj   obj
     * @param inner inner
     * @return list
     */
    public static List<String> getPropertyNames(Object obj, boolean inner) {
        List<String> propertyNames = new LinkedList<>();
        if (obj instanceof Map) {
            Map<String, ?> map = (Map<String, ?>) obj;
            propertyNames.addAll(map.keySet());
        }
        propertyNames.addAll(doGetPropertyNames(obj.getClass(), inner));
        return propertyNames;
    }

    /**
     * 获取对象可读属性名字（嵌套获取） eg:test.test.name
     *
     * @param obj   obj
     * @param inner inner
     * @return list
     */
    public static List<String> getReadPropertyNames(Object obj, boolean inner) {
        List<String> propertyNames = new LinkedList<>();
        if (obj instanceof Map) {
            Map<String, ?> map = (Map<String, ?>) obj;
            propertyNames.addAll(map.keySet());
        }
        propertyNames.addAll(doGetReadPropertyNames(obj.getClass(), inner));
        return propertyNames;
    }

    /**
     * 获取对象可写属性名字（嵌套获取） eg:test.test.name
     *
     * @param obj   obj
     * @param inner inner
     * @return list
     */
    public static List<String> getWritePropertyNames(Object obj, boolean inner) {
        List<String> propertyNames = new LinkedList<>();
        if (obj instanceof Map) {
            Map<String, ?> map = (Map<String, ?>) obj;
            propertyNames.addAll(map.keySet());
        }
        propertyNames.addAll(doGetWritePropertyNames(obj.getClass(), inner));
        return propertyNames;
    }

    /**
     * 获取公共属性名称
     *
     * @param source           源对象
     * @param target           目标对象
     * @param supportMapKV     是否支持map内属性
     * @param ignoreProperties 忽略属性
     * @return 属性映射
     */
    public static String[][] getCommonPropertyNameMapper(Object source, Object target, boolean supportMapKV,
                                                         String... ignoreProperties) {
        Set<String> ignore = new HashSet<>(Arrays.asList(ignoreProperties));
        Map<String, PropertyAccess> sourceReadPropertyAccess = getReadAblePropertyAccessMap(source, ignore);
        Map<String, PropertyAccess> targetWritePropertyAccess = getWriteAblePropertyAccessMap(target, ignore);

        List<String> sourceProperty = new LinkedList<>();
        List<String> targetProperty = new LinkedList<>();

        if (supportMapKV) {
            List<String> mapCommonProperties = getMapPropertyMapper(source, sourceReadPropertyAccess, target,
                    targetWritePropertyAccess, ignore);
            sourceProperty.addAll(mapCommonProperties);
            targetProperty.addAll(mapCommonProperties);
        }

        for (Map.Entry<String, PropertyAccess> targetEntry : targetWritePropertyAccess.entrySet()) {
            PropertyAccess sourcePropertyAccess = sourceReadPropertyAccess.get(targetEntry.getKey());
            if (sourcePropertyAccess != null) {
                sourceProperty.add(sourcePropertyAccess.getName());
                targetProperty.add(targetEntry.getValue().getName());
            }
        }

        return new String[][]{sourceProperty.toArray(STRING_ARRAY), targetProperty.toArray(STRING_ARRAY)};
    }

    private static Map<String, PropertyAccess> getReadAblePropertyAccessMap(Object source, Set<String> ignore) {
        return getReadPropertyAccess(source).stream().map(Properties::parsePropertyInfo)
                .filter(item -> !ignore.contains(item.getName()))
                .filter(item -> !Objects.equals(item.getAccess(), Access.WRITE_ONLY))
                .collect(Collectors.toMap(PropertyInfo::getName, PropertyInfo::getPropertyAccess));
    }

    private static Map<String, PropertyAccess> getWriteAblePropertyAccessMap(Object target, Set<String> ignore) {
        return getWritePropertyAccess(target).stream().map(Properties::parsePropertyInfo)
                .filter(item -> !ignore.contains(item.getName()))
                .filter(item -> !Objects.equals(item.getAccess(), Access.READ_ONLY))
                .collect(Collectors.toMap(PropertyInfo::getName, PropertyInfo::getPropertyAccess));
    }

    // 获取map 属性映射
    private static List<String> getMapPropertyMapper(Object source,
                                                     Map<String, PropertyAccess> sourceReadPropertyAccess, Object target,
                                                     Map<String, PropertyAccess> targetWritePropertyAccess, Set<String> ignore) {
        Set<String> propertyNames = new HashSet<>();
        if (target instanceof Map) {
            if (source instanceof Map) {
                for (String item : Properties.getMapPropertyNames((Map<String, ?>) source)) {
                    if (!ignore.contains(item)) {
                        propertyNames.add(item);
                    }
                }
            }

            for (Map.Entry<String, PropertyAccess> sourceEntry : sourceReadPropertyAccess.entrySet()) {
                String propertyName = sourceEntry.getValue().getName();
                if (source instanceof Map && MAP_IGNORE_PROPERTY.contains(propertyName)) {
                    continue;
                }
                propertyNames.add(sourceEntry.getValue().getName());
            }
        } else if (source instanceof Map) {
            for (Map.Entry<String, PropertyAccess> targetEntry : targetWritePropertyAccess.entrySet()) {
                if (((Map<?, ?>) source).containsKey(targetEntry.getKey())) {
                    propertyNames.add(targetEntry.getValue().getName());
                }
            }
        }
        return new ArrayList<>(propertyNames);
    }
}
