package com.sprint.common.converter.conversion.nested.bean.introspection;

import com.sprint.common.converter.conversion.nested.bean.BeansException;
import com.sprint.common.converter.util.ConcurrentReferenceHashMap;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 内省缓存
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class CachedIntrospectionResults {

    private static final String[] STRING_ARRAY = new String[0];

    /* 忽略转化的字段 */
    private static final String IGNORE_CLASS = "class";

    private static final ConcurrentMap<Class<?>, CachedIntrospectionResults> cache = new ConcurrentReferenceHashMap<>();

    public static CachedIntrospectionResults forClass(Class<?> beanClass) {
        CachedIntrospectionResults results = cache.get(beanClass);
        if (results != null) {
            return results;
        }

        results = new CachedIntrospectionResults(beanClass);

        cache.put(beanClass, results);

        return results;
    }

    private final BeanInfo beanInfo;

    private final Map<String, PropertyAccess> propertyAccessCache;

    /**
     * Create a new CachedIntrospectionResults instance for the given class.
     *
     * @param beanClass the bean class to analyze
     * @throws BeansException in case of introspection failure
     */
    private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
        try {
            this.beanInfo = Introspector.getBeanInfo(beanClass, Introspector.IGNORE_ALL_BEANINFO);

            this.propertyAccessCache = new LinkedHashMap<>();
            // This call is slow so we do it once.
            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if (Class.class == beanClass
                        && ("classLoader".equals(pd.getName()) || "protectionDomain".equals(pd.getName()))) {
                    // Ignore Class.getClassLoader() and getProtectionDomain() methods - nobody needs to bind to
                    // those
                    continue;
                }
                if (IGNORE_CLASS.equals(pd.getName())) {
                    continue;
                }

                this.propertyAccessCache.put(pd.getName(),
                        new PropertyAccess(pd.getName(), beanClass, pd.getReadMethod(), pd.getWriteMethod()));
            }

            Class<?> clazz = beanClass;
            while (clazz != null) {
                Class<?>[] ifcs = clazz.getInterfaces();
                for (Class<?> ifc : ifcs) {
                    BeanInfo ifcInfo = Introspector.getBeanInfo(ifc, Introspector.IGNORE_ALL_BEANINFO);
                    PropertyDescriptor[] ifcPds = ifcInfo.getPropertyDescriptors();
                    for (PropertyDescriptor pd : ifcPds) {
                        if (!this.propertyAccessCache.containsKey(pd.getName())) {
                            this.propertyAccessCache.put(pd.getName(), new PropertyAccess(pd.getName(), beanClass,
                                    pd.getReadMethod(), pd.getWriteMethod()));
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

            Set<String> fieldNames = getAllFieldNames(beanClass);

            for (String fieldName : fieldNames) {
                if (propertyAccessCache.get(fieldName) == null) {
                    propertyAccessCache.put(fieldName, new PropertyAccess(fieldName, beanClass, null, null));
                }
            }
        } catch (IntrospectionException ex) {
            throw new BeansException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
        }
    }

    BeanInfo getBeanInfo() {
        return this.beanInfo;
    }

    Class<?> getBeanClass() {
        return this.beanInfo.getBeanDescriptor().getBeanClass();
    }

    public PropertyAccess getPropertyAccess(String name) {
        PropertyAccess pd = this.propertyAccessCache.get(name);
        if (pd == null && name != null && name.length() > 0) {
            // Same lenient fallback checking as in PropertyTypeDescriptor...
            pd = this.propertyAccessCache.get(name.substring(0, 1).toLowerCase() + name.substring(1));
            if (pd == null) {
                pd = this.propertyAccessCache.get(name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
        return pd;
    }

    public PropertyAccess[] getPropertyAccesses() {
        PropertyAccess[] pds = new PropertyAccess[this.propertyAccessCache.size()];
        int i = 0;
        for (PropertyAccess pd : this.propertyAccessCache.values()) {
            pds[i] = pd;
            i++;
        }
        return pds;
    }

    /**
     * 获取所有可读属性名字
     *
     * @return PropertyAccess[]
     */
    public PropertyAccess[] getReadPropertyAccess() {
        return this.propertyAccessCache.values().stream().filter(PropertyAccess::isReadAccessible)
                .toArray(PropertyAccess[]::new);
    }

    /**
     * 获取所有可读属性名字
     *
     * @return PropertyAccess[]
     */
    public PropertyAccess[] getWritePropertyAccess() {
        return this.propertyAccessCache.values().stream().filter(PropertyAccess::isWriteAccessible)
                .toArray(PropertyAccess[]::new);
    }

    /**
     * 获取所有可访问的成员变量
     *
     * @param clazz 对象类
     * @return 可访问的成员变量
     */
    Set<String> getAllFieldNames(Class<?> clazz) {
        Set<String> fieldNames = new HashSet<>();
        while (null != clazz) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    fieldNames.add(field.getName());
                }
            }
            clazz = clazz.getSuperclass();
        }

        return fieldNames;
    }
}