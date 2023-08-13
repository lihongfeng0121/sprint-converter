package com.sprint.common.converter.conversion.nested.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sprint.common.converter.exception.JsonException;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

/**
 * jackson转换器
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class JacksonConverter implements JsonConverter {

    private final ObjectMapper mapper;

    /**
     * 设置一些通用的属性
     */ {
        mapper = JacksonMapper.configure(new ObjectMapper());
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
