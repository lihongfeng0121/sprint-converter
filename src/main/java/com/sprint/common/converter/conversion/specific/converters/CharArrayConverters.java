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

    public static class StringToCharacterArray implements SpecificConverter<String, Character[]> {

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Character[]> getTargetClass() {
            return Character[].class;
        }

        @Override
        public Character[] convert(String source) throws ConversionException {
            if (source == null) {
                return null;
            }
            char[] chars = source.toCharArray();

            Character[] characters = new Character[chars.length];
            for (int i = 0; i < chars.length; i++) {
                characters[i] = chars[i];
            }
            return characters;
        }
    }
}
