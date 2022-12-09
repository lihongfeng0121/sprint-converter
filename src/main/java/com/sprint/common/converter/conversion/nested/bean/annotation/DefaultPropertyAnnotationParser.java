package com.sprint.common.converter.conversion.nested.bean.annotation;

import com.sprint.common.converter.conversion.nested.bean.PropertyAnnotationParser;
import com.sprint.common.converter.conversion.nested.bean.PropertyInfo;

/**
 * 默认属性注解解析器
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class DefaultPropertyAnnotationParser implements PropertyAnnotationParser<Property> {

    @Override
    public Class<Property> annotationType() {
        return Property.class;
    }

    @Override
    public PropertyInfo parse(Property annotation) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName(annotation.value());
        propertyInfo.setIndex(annotation.index());
        propertyInfo.setAccess(annotation.access());
        return propertyInfo;
    }
}
