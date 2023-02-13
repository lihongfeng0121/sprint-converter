package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.BeanOptional;
import com.sprint.common.converter.util.ObjectValue;

/**
 * @author hongfeng.li
 * @since 2023/2/13
 */
public class BeanOptionalConverters implements DynamicConverterLoader {


    public static class ObjectToBeanOptionalConverter implements DynamicConverter<BeanOptional<?>> {

        @Override
        public int sort() {
            return 1 << 2;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return BeanOptional.class.equals(targetType);
        }

        @Override
        public BeanOptional<?> convert(Object source, Class<BeanOptional<?>> targetType) throws ConversionException {
            return BeanOptional.ofNullable(source);
        }
    }

    public static class BeanOptionalToObjectConverter implements DynamicConverter<Object> {

        @Override
        public int sort() {
            return 1 << 2;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return BeanOptional.class.isAssignableFrom(sourceType) && !BeanOptional.class.isAssignableFrom(targetType);
        }

        @Override
        public Object convert(Object source, Class<Object> targetType) throws ConversionException {
            if (source == null) {
                return null;
            }
            BeanOptional<?> optional = (BeanOptional<?>) source;
            return optional.map(ObjectValue::ofNullable).map(obj -> obj.getValue(targetType)).orElse(null);
        }
    }
}
