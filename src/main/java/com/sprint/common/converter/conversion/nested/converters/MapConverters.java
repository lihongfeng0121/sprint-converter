package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.conversion.nested.bean.Beans;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author hongfeng.li
 * @since 2021/1/25
 */
public class MapConverters implements NestedConverterLoader {
    /**
     * map转map
     */
    public static class Map2Map implements NestedConverter {

        @Override
        public int sort() {
            return 7;
        }

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return Types.isMap(sourceClass) && Types.isMap(targetClass);
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            Map<?, ?> mValue = (Map<?, ?>) sourceValue;
            Class<?> extractClass = Types.extractClass(targetFiledType, targetBeanType);
            Map<Object, Object> targetMValue = extractClass.isAssignableFrom(mValue.getClass())
                    ? Beans.instanceMap(mValue.getClass())
                    : Beans.instanceMap(extractClass);

            if (Types.isBean(targetMValue.getClass())) {
                Beans.copyProperties(mValue, targetMValue, true, true, false, Types.MAP_IGNORES);
            }

            if (!mValue.isEmpty()) {
                Type[] kvType = Types.getMapKVType(targetBeanType, targetFiledType);
                Type keyActualType = kvType[0];
                Type valActualType = kvType[1];

                for (Map.Entry<?, ?> entry : mValue.entrySet()) {
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    if (key != null) {
                        key = NestedConverters.convert(key, targetBeanType,
                                Types.isObjectType(keyActualType) ? key.getClass() : keyActualType);
                    }
                    if (val != null) {
                        val = NestedConverters.convert(val, targetBeanType,
                                Types.isObjectType(valActualType) ? val.getClass() : valActualType);
                    }
                    targetMValue.put(key, val);
                }
            }

            return targetMValue;
        }
    }
}
