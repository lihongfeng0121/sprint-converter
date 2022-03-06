package com.sprint.common.converter.conversion.nested.bean.annotation;

import com.sprint.common.converter.conversion.nested.bean.PropertyInfoAnnotationParser;
import com.sprint.common.converter.conversion.nested.bean.PropertyInfoHolder;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title DefaultPropertyInfoAnnotationParser
 * @desc 默认属性注解解析起
 * @since 2021年02月05日
 */
public class DefaultPropertyInfoAnnotationParser implements PropertyInfoAnnotationParser<PropertyInfo> {

    @Override
    public Class<PropertyInfo> annotationType() {
        return PropertyInfo.class;
    }

    @Override
    public PropertyInfoHolder parse(PropertyInfo annotation) {
        PropertyInfoHolder propertyInfoHolder = new PropertyInfoHolder();
        propertyInfoHolder.setName(annotation.value());
        propertyInfoHolder.setIndex(annotation.index());
        propertyInfoHolder.setAccess(annotation.access());
        return propertyInfoHolder;
    }
}
