package com.sprint.common.converter.conversion.nested.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sprint.common.converter.exception.JsonException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.Types;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.TimeZone;

/**
 * jackson转换器
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class JacksonConverter implements JsonConverter {

    private final ObjectMapper mapper;

    private final ClassLoader moduleClassLoader = JacksonConverter.class.getClassLoader();

    /**
     * 设置一些通用的属性
     */ {
        mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)                // 默认为true
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)                // 默认为true
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)        // 默认为true
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)       // 默认为false
                .configure(JsonParser.Feature.ALLOW_COMMENTS, true)                        // 默认为false
                .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)                    // 默认为false
                .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)    // 默认为false
                .configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true)                // 默认为false
                .configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)            // 默认为false
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)            // 默认为false
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)            // 默认为false
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)                    // 默认为false
                .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)                         // 默认为false
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        registerWellKnownModulesIfAvailable(mapper);

        SimpleFilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false);
        mapper.setFilterProvider(filters);
    }

    @SuppressWarnings("unchecked")
    private void registerWellKnownModulesIfAvailable(ObjectMapper objectMapper) {
        // Java 7 java.nio.file.Path class present?
        if (Types.isPresent("java.nio.file.Path", this.moduleClassLoader)) {
            try {
                Class<? extends Module> jdk7Module = (Class<? extends Module>)
                        Types.forName("com.fasterxml.jackson.datatype.jdk7.Jdk7Module", this.moduleClassLoader);
                objectMapper.registerModule(Beans.instance(jdk7Module));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-jdk7 not available
            }
        }

        // Java 8 java.util.Optional class present?
        if (Types.isPresent("java.util.Optional", this.moduleClassLoader)) {
            try {
                Class<? extends Module> jdk8Module = (Class<? extends Module>)
                        Types.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", this.moduleClassLoader);
                objectMapper.registerModule(Beans.instance(jdk8Module));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-jdk8 not available
            }
        }

        // Java 8 java.time package present?
        if (Types.isPresent("java.time.LocalDate", this.moduleClassLoader)) {
            try {
                Class<? extends Module> javaTimeModule = (Class<? extends Module>)
                        Types.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", this.moduleClassLoader);
                objectMapper.registerModule(Beans.instance(javaTimeModule));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-jsr310 not available
            }
        }

        // Joda-Time present?
        if (Types.isPresent("org.joda.time.LocalDate", this.moduleClassLoader)) {
            try {
                Class<? extends Module> jodaModule = (Class<? extends Module>)
                        Types.forName("com.fasterxml.jackson.datatype.joda.JodaModule", this.moduleClassLoader);
                objectMapper.registerModule(Beans.instance(jodaModule));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-joda not available
            }
        }

        // Kotlin present?
        if (Types.isPresent("kotlin.Unit", this.moduleClassLoader)) {
            try {
                Class<? extends Module> kotlinModule = (Class<? extends Module>)
                        Types.forName("com.fasterxml.jackson.module.kotlin.KotlinModule", this.moduleClassLoader);
                objectMapper.registerModule(Beans.instance(kotlinModule));
            } catch (ClassNotFoundException ex) {
                // jackson-module-kotlin not available
            }
        }
    }

    @Override
    public String toJsonString(Object obj) throws JsonException {
        try {
            if (Objects.isNull(obj)) {
                return null;
            }
            return mapper.writeValueAsString(obj);
        } catch (Throwable e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> T toJavaObject(String value, Type type) throws JsonException {
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return mapper.readValue(value, TypeFactory.defaultInstance().constructType(type));
        } catch (Throwable e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> Collection<T> toJavaObjects(String value, Type type, Class<?> collectionType) throws JsonException {
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionType,
                    TypeFactory.defaultInstance().constructType(type));
            return mapper.readValue(value, javaType);
        } catch (Throwable e) {
            throw new JsonException(e);
        }
    }
}
