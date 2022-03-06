package com.sprint.common.converter.conversion.dynamic.converters;

import com.sprint.common.converter.conversion.dynamic.DynamicConverter;
import com.sprint.common.converter.conversion.dynamic.DynamicConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title PrimitiveConverters
 * @desc 包装类转换
 * @since 2021年02月05日
 */
public class PrimitiveConverters implements DynamicConverterLoader {

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

    static {
        // 包装类->基本类
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        primitiveWrapperTypeMap.forEach((key, val) -> {
            primitiveTypeToWrapperMap.put(val, key);
        });
    }

    /**
     * 获取基本类型包装类
     *
     * @param primitiveType
     * @return
     */
    public static Class<?> getWrapperType(Class<?> primitiveType) {
        return primitiveTypeToWrapperMap.getOrDefault(primitiveType, primitiveType);
    }

    /**
     * 获取基本类型类
     *
     * @param wrapperType
     * @return
     */
    public static Class<?> getPrimitiveType(Class<?> wrapperType) {
        return primitiveWrapperTypeMap.get(wrapperType);
    }

    public static class PrimitiveToWrapper implements DynamicConverter<Object> {

        @Override
        public int sort() {
            return 1 << 6;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return targetType.isPrimitive() && getWrapperType(targetType).isAssignableFrom(sourceType);
        }

        @Override
        public Object convert(Object source, Class<Object> targetType) throws ConversionException {
            return source;
        }
    }

    public static class WrapperToWrapper implements DynamicConverter<Object> {

        @Override
        public int sort() {
            return 1 << 6;
        }

        @Override
        public boolean support(Class<?> sourceType, Class<?> targetType) {
            return getPrimitiveType(sourceType) != null && sourceType.isAssignableFrom(sourceType);
        }

        @Override
        public Object convert(Object source, Class<Object> targetType) throws ConversionException {
            return source;
        }
    }
}
