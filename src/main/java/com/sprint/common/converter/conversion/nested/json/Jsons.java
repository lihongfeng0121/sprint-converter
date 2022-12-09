package com.sprint.common.converter.conversion.nested.json;

import com.sprint.common.converter.exception.JsonException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Json转换工具
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class Jsons {

    private static final Logger log = LoggerFactory.getLogger(Jsons.class);

    private static JsonConverter jsonConverter;

    /**
     * 设置一些通用的属性
     */
    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        for (JsonConverter jsonConverter : ServiceLoader.load(JsonConverter.class, loader)) {
            Jsons.jsonConverter = jsonConverter;
            log.warn("Jsons use {} as JsonConverter.", jsonConverter.getClass().getName());
            break;
        }

        if (jsonConverter == null) {
            try {
                Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
                Jsons.jsonConverter = (JsonConverter) Beans.instance(
                        Types.forName("com.sprint.common.converter.conversion.nested.json.JacksonConverter", Types.getDefaultClassLoader()));
                log.warn("Jsons use jackson as JsonConverter.");
            } catch (Throwable ignored) {
            }
        }
    }

    public static String toJsonString(Object obj) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return jsonConverter.toJsonString(obj);
    }

    public static <T> T toJavaObject(String value, Type type) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return jsonConverter.toJavaObject(value, type);
    }

    public static <T> Collection<T> toJavaObjects(String value, Type type, Class<?> collectionType)
            throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return jsonConverter.toJavaObjects(value, type, collectionType);
    }

    public static <T> Collection<T> toJavaObjectList(String value, Type type) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return jsonConverter.toJavaObjects(value, type, List.class);
    }
}
