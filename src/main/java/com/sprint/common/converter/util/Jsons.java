package com.sprint.common.converter.util;

import com.sprint.common.converter.TypeReference;
import com.sprint.common.converter.conversion.nested.bean.PropertyAnnotationParser;
import com.sprint.common.converter.conversion.nested.json.JacksonPropertyAnnotationParser;
import com.sprint.common.converter.conversion.nested.json.JsonConverter;
import com.sprint.common.converter.exception.JsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    /*
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
                PropertyAnnotationParser<?> annotationParser = (PropertyAnnotationParser<?>) Beans.instance(
                        Types.forName("com.sprint.common.converter.conversion.nested.json.JacksonPropertyAnnotationParser", Types.getDefaultClassLoader()));
                Properties.registerAnnotationParser(annotationParser);
                log.warn("Jsons use jackson as JsonConverter.");
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Helper method to generate a JSON string representation of the given object.
     *
     * @param obj the object to be serialized
     * @return a JSON string representation of the given object, or null if an error occurs
     * @throws JsonException if an error occurs during serialization
     */
    public static String toJsonString(Object obj) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return jsonConverter.toJsonString(obj);
    }

    /**
     * Converts a JSON string value to a Java object.
     *
     * @param <T> The type of the object to be converted.
     * @param value The JSON string value.
     * @param type The type of the object to be converted.
     * @return The Java object created from the JSON string.
     * @throws JsonException If there is an error in the conversion process.
     */
    public static <T> T toJavaObject(String value, Class<T> type) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return jsonConverter.toJavaObject(value, type);
    }

    public static <T> T toJavaObject(String value, TypeReference<T> type) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return jsonConverter.toJavaObject(value, type.getType());
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

    public static <T> List<T> toJavaObjectList(String value, Class<T> type) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return (List<T>) jsonConverter.toJavaObjects(value, type, List.class);
    }

    public static <T> List<T> toJavaObjectList(String value, Type type) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return (List<T>) jsonConverter.toJavaObjects(value, type, List.class);
    }

    public static <T> List<T> toJavaObjectList(String value, TypeReference<T> type) throws JsonException {
        if (jsonConverter == null) {
            return null;
        }
        return (List<T>) jsonConverter.toJavaObjects(value, type.getType(), List.class);
    }

    public static Map<String, Object> toMap(String value) throws JsonException {
        return toJavaObject(value, TypeReference.STR_OBJ_MAP);
    }

    public static List<Map<String, Object>> toMaps(String value) throws JsonException {
        return toJavaObjectList(value, TypeReference.STR_OBJ_MAP);
    }

    public static JsonArray toJsonArray(String value) {
        return new JsonArray(toJavaObjectList(value, Object.class));
    }

    public static JsonObject toJsonObject(String value) {
        return JsonObject.parse(value);
    }

}
