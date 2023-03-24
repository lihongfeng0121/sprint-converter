package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
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
            return sourceType.isBeanOrMap() && !targetType.isMap() && !targetType.isCollection()
                    && targetType.isInterface() && targetType.isBean() && !targetType.isAssignableFrom(sourceType);
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor) throws ConversionException {
            Class<?> actualType = targetTypeDescriptor.getActualClass();
            return VirtualBean.of(actualType).setProperties(sourceValue).getObject();
        }
    }
}
