package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.exception.ConversionException;

import java.nio.charset.StandardCharsets;

/**
 * @author hongfeng.li
 * @since 2022/3/17
 */
public class ByteArrayConverters implements SpecificConverterLoader {

    public static class StringToByteArray implements SpecificConverter<String, byte[]> {

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<byte[]> getTargetClass() {
            return byte[].class;
        }

        @Override
        public byte[] convert(String source) throws ConversionException {
            if (source == null) {
                return null;
            }
            return source.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static class StringToByteArray2 implements SpecificConverter<String, Byte[]> {

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Byte[]> getTargetClass() {
            return Byte[].class;
        }

        @Override
        public Byte[] convert(String source) throws ConversionException {
            if (source == null) {
                return null;
            }
            byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
            Byte[] bytes2 = new Byte[bytes.length];
            for (int i = 0, length = bytes.length; i < length; i++) {
                bytes2[i] = bytes[i];
            }
            return bytes2;
        }
    }
}
