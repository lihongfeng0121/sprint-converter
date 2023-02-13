package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.AbstractValue;
import com.sprint.common.converter.util.ObjectValue;

/**
 * @author hongfeng.li
 * @since 2023/2/13
 */
public class ObjectValueConverters implements DynamicConverterLoader {


    public static class ObjectToObjectValueConverter implements DynamicConverter<ObjectValue> {

        @Override
        public int sort() {
            return 1 << 2;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return ObjectValue.class.equals(targetType);
        }

        @Override
        public ObjectValue convert(Object source, Class<ObjectValue> targetType) throws ConversionException {
            return ObjectValue.ofNullable(source);
        }
    }

    public static class ObjectValueToObjectConverter implements DynamicConverter<Object> {

        @Override
        public int sort() {
            return 1 << 2;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return AbstractValue.class.isAssignableFrom(sourceType);
        }

        @Override
        public Object convert(Object source, Class<Object> targetType) throws ConversionException {
            if (source == null) {
                return null;
            }
            AbstractValue abstractValue = (AbstractValue) source;
            return abstractValue.getValue(targetType);
        }
    }
}
