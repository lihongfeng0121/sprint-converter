package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng.li
 * @since 2023/3/1
 */
public class CharConverters implements SpecificConverterLoader {

    public static class StringToCharacter implements SpecificConverter<String, Character> {

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Character> getTargetClass() {
            return Character.class;
        }

        @Override
        public Character convert(String source) throws ConversionException {
            if (source.isEmpty()) {
                return null;
            }
            if (source.length() > 1) {
                throw new ConversionException(
                        "Can only convert a [String] with length of 1 to a [Character]; string value '" + source + "'  has length of " + source.length());
            }
            return source.charAt(0);
        }
    }
}
