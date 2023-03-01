package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.conversion.nested.NestedConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.Miscs;
import com.sprint.common.converter.util.TypeDescriptor;
import com.sprint.common.converter.util.Types;

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
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.isMap() && targetType.isMap();
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            Map<?, ?> mValue = (Map<?, ?>) sourceValue;
            Class<?> extractClass = targetTypeDescriptor.getActualClass();
            Map<Object, Object> targetMValue = extractClass.isAssignableFrom(mValue.getClass())
                    ? Beans.instanceMap(mValue.getClass())
                    : Beans.instanceMap(extractClass);

            if (Types.isBean(targetMValue.getClass())) {
                Beans.copyProperties(mValue, targetMValue, true, true, false, Types.MAP_IGNORES);
            }

            if (!mValue.isEmpty()) {
                TypeDescriptor[] kvTypeDescriptor = targetTypeDescriptor.getMapKVTypeDescriptor();
                TypeDescriptor keyActualType = Miscs.at(kvTypeDescriptor, 0);
                TypeDescriptor valActualType = Miscs.at(kvTypeDescriptor, 1);

                for (Map.Entry<?, ?> entry : mValue.entrySet()) {
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    if (key != null) {
                        key = NestedConverters.convert(key, keyActualType);
                    }
                    if (val != null) {
                        val = NestedConverters.convert(val, valActualType);
                    }
                    targetMValue.put(key, val);
                }
            }

            return targetMValue;
        }
    }
}
