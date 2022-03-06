package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.util.Types;
import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.lang.reflect.Constructor;

/**
 * @author hongfeng.li
 * @since 2022/2/10
 */
public class ConstructorConverters implements DynamicConverterLoader {

    public static class ConstructorConverter implements DynamicConverter<Object> {

        @Override
        public int sort() {
            return 1 << 12;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return Types.getConstructorIfAvailable(targetType, sourceType) != null
                    || Types.getConstructorIfAvailable(targetType, Object.class) != null;
        }

        @Override
        public Object convert(Object source, Class<Object> targetType) throws ConversionException {
            try {
                Constructor<?> constructor = Types.getConstructorIfAvailable(targetType, source.getClass());
                if (constructor == null) {
                    constructor = Types.getConstructorIfAvailable(targetType, Object.class);
                }
                if (constructor == null) {
                    throw new ConversionException("not exit constructor!");
                }
                return constructor.newInstance(source);
            } catch (Throwable t) {
                throw new ConversionException(t);
            }
        }
    }

}
