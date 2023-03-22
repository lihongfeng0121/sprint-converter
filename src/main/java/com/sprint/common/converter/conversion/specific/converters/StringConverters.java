package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.nio.charset.StandardCharsets;

/**
 * @author hongfeng.li
 * @since 2022/3/17
 */
public class StringConverters implements SpecificConverterLoader {

    public static class ByteArrayToString implements SpecificConverter<byte[], String> {

        @Override
        public Class<byte[]> getSourceClass() {
            return byte[].class;
        }

        @Override
        public Class<String> getTargetClass() {
            return String.class;
        }

        @Override
        public String convert(byte[] source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return new String(source, StandardCharsets.UTF_8);
        }
    }

    public static class ByteArrayToString2 implements SpecificConverter<Byte[], String> {

        @Override
        public Class<Byte[]> getSourceClass() {
            return Byte[].class;
        }

        @Override
        public Class<String> getTargetClass() {
            return String.class;
        }

        @Override
        public String convert(Byte[] source) throws ConversionException {
            if (source == null) {
                return null;
            }
            byte[] bytes = new byte[source.length];
            for (int i = 0, length = source.length; i < length; i++) {
                bytes[i] = source[i];
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    public static class CharArrayToString implements SpecificConverter<char[], String> {

        @Override
        public Class<char[]> getSourceClass() {
            return char[].class;
        }

        @Override
        public Class<String> getTargetClass() {
            return String.class;
        }

        @Override
        public String convert(char[] source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return new String(source);
        }
    }

    public static class CharacterArrayToString implements SpecificConverter<Character[], String> {

        @Override
        public Class<Character[]> getSourceClass() {
            return Character[].class;
        }

        @Override
        public Class<String> getTargetClass() {
            return String.class;
        }

        @Override
        public String convert(Character[] source) throws ConversionException {
            if (source == null) {
                return null;
            }

            char[] chars = new char[source.length];
            for (int i = 0, length = source.length; i < length; i++) {
                chars[i] = source[i];
            }

            return new String(chars);
        }
    }
}
