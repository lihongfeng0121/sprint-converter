package com.sprint.common.converter.conversion.nested.json;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Json 转换
 *
 * @author hongfeng.li
 * @since 2019年12月25日
 */
/**
 * @author hongfeng.li
 * @version 1.0
 * @title JsonConverter
 * @desc json转换器
 * @since 2021年02月05日
 */
public interface JsonConverter {

    /**
     * 转为 json str
     *
     * @param obj obj
     * @return str
     * @throws JsonException
     */
    String toJsonString(Object obj) throws JsonException;

    /**
     * 转为obj对象
     *
     * @param value json str
     * @param type 目标类型
     * @param <T> 范型
     * @return 目标类型对象
     * @throws JsonException
     */
    <T> T toJavaObject(String value, Type type) throws JsonException;

    /**
     * 转为 json array
     *
     * @param value json str
     * @param type 目标类型
     * @param collectionType 目标集合类型
     * @param <T> 范型
     * @return 目标类型集合对象
     * @throws JsonException
     */
    <T> Collection<T> toJavaObjects(String value, Type type, Class<?> collectionType) throws JsonException;

}
