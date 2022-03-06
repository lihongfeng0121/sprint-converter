package com.sprint.common.converter.conversion.nested.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title JacksonConverter
 * @desc jackson转换器
 * @since 2021年02月05日
 */
public class JacksonConverter implements JsonConverter {

    private final ObjectMapper mapper;

    /**
     * 设置一些通用的属性
     */
    {
        mapper = new ObjectMapper();
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        // mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        // 如果存在未知属性，则忽略不报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许key没有双引号
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许key有单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许整数以0开头
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        // 允许字符串中存在回车换行控制符
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        SimpleFilterProvider filters = new SimpleFilterProvider();
        filters.setFailOnUnknownId(false);

        mapper.setFilterProvider(filters);
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
