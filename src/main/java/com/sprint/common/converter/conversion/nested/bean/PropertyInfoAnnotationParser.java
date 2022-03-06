package com.sprint.common.converter.conversion.nested.bean;

import com.sprint.common.converter.util.Types;
import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;

import java.lang.annotation.Annotation;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title PropertyInfoAnnotationParser
 * @desc 属性注解解析器
 * @since 2021年02月05日
 */
public interface PropertyInfoAnnotationParser<T extends Annotation> {

    /**
     * 注解类型
     *
     * @return 注解类型
     */
    default Class<T> annotationType() {
        return (Class<T>) Types.getInterfaceSuperclass(this.getClass(), PropertyInfoAnnotationParser.class, 0);
    }

    /**
     * 是否支持
     *
     * @param propertyAccess
     * @return
     */
    default boolean support(PropertyAccess propertyAccess) {
        return propertyAccess.getAnnotation(annotationType()) != null;
    }

    /**
     * 解析
     *
     * @param annotation
     * @return
     */
    PropertyInfoHolder parse(T annotation);
}
