package com.sprint.common.converter.conversion.specific.converters;

import com.sprint.common.converter.conversion.specific.SpecificConverter;
import com.sprint.common.converter.conversion.specific.SpecificConverterLoader;

import java.nio.charset.Charset;

/**
 * @author hongfeng.li
 * @since 2023/3/1
 */
public class CharsetConverters implements SpecificConverterLoader {

    public static class StringToCharsetConverter implements SpecificConverter<String, Charset> {

        @Override
        public Class<String> getSourceClass() {
            return String.class;
        }

        @Override
        public Class<Charset> getTargetClass() {
            return Charset.class;
        }

        @Override
        public Charset convert(String source) {
            return Charset.forName(source);
        }
    }
}
