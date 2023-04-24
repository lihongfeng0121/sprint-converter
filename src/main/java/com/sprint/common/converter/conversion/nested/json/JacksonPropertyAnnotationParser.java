package com.sprint.common.converter.conversion.nested.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.common.converter.conversion.nested.bean.Access;
import com.sprint.common.converter.conversion.nested.bean.PropertyAnnotationParser;
import com.sprint.common.converter.conversion.nested.bean.PropertyInfo;

/**
 * @author hongfeng.li
 * @since 2023/4/23
 */
public class JacksonPropertyAnnotationParser implements PropertyAnnotationParser<JsonProperty> {

    @Override
    public PropertyInfo parse(JsonProperty annotation) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName(annotation.value());
        propertyInfo.setIndex(annotation.index());
        propertyInfo.setAccess(Access.valueOf(annotation.access().name()));
        return propertyInfo;
    }
}
