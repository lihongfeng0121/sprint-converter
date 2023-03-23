package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.TypeDescriptor;
import com.sprint.common.converter.util.VirtualBean;

/**
 * @author hongfeng.li
 * @since 2023/3/22
 */
public class InterfaceConverters implements NestedConverterLoader {

    public static class Bean2Interface implements NestedConverter {

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.isBeanOrMap() && targetType.isInterface() && !targetType.isMap() && !targetType.isCollection() && !targetType.isAssignableFrom(sourceType);
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor) throws ConversionException {
            Class<?> actualType = targetTypeDescriptor.getActualClass();
            VirtualBean<?> virtualBean = new VirtualBean<>(actualType);
            for (PropertyAccess propertyAccess : virtualBean.getProperties()) {
                Object property = Beans.getProperty(sourceValue, propertyAccess.getName());
                if (property != null) {
                    Object targetVal = NestedConverters.convert(property, TypeDescriptor.of(propertyAccess.extractClass()));
                    virtualBean.setProperty(propertyAccess.getName(), targetVal);
                }
            }
            return virtualBean.getObject();
        }
    }
}
