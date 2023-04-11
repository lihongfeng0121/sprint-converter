package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;
import com.sprint.common.converter.util.Strings;

import java.util.UUID;

/**
 * @author hongfeng.li
 * @since 2023/3/1
 */
public class UUIDConverters implements SpecificConverterLoader {

    public static class StringToUUIDConverter implements SpecificConverter<String, UUID> {

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<UUID> getTargetClass() {
            return UUID.class;
        }

        @Override
        public UUID convert(String source) {
            return (Strings.isBlank(source) ? null : UUID.fromString(source.trim()));
        }
    }

    public static class ByteArrayToUUIDConverter implements SpecificConverter<byte[], UUID> {

        @Override
        public Class<byte[]> getSourceClass() {
            return byte[].class;
        }

        @Override
        public Class<UUID> getTargetClass() {
            return UUID.class;
        }

        @Override
        public UUID convert(byte[] source) {
            return (source == null ? null : UUID.nameUUIDFromBytes(source));
        }
    }
}
