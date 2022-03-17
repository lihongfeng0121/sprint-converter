package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng.li
 * @since 2022/3/17
 */
public class CharArrayConverters implements SpecificConverterLoader {

    public static class StringToCharArray implements SpecificConverter<String, char[]> {

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<char[]> getTargetClass() {
            return char[].class;
        }

        @Override
        public char[] convert(String source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return source.toCharArray();
        }
    }
}
