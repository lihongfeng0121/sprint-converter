package com.sprint.common.converter.conversion.specific;

import com.sprint.common.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title SpecificConverters
 * @desc 指定类型转化器
 * @since 2021年02月05日
 */
public final class SpecificConverters {

    private static final Logger logger = LoggerFactory.getLogger(SpecificConverters.class);

    private SpecificConverters() {
    }

    private static final ConcurrentMap<String, SpecificConverter<?, ?>> SPECIFIC_CONVERTER_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Optional<SpecificConverter<?, ?>>> SUPPORT_SPECIFIC_CONVERTER_CACHE = new ConcurrentHashMap<>();

    private static final String DELIMITER = "->";

    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        for (SpecificConverterLoader specificConverterLoader : ServiceLoader.load(SpecificConverterLoader.class,
                loader)) {
            specificConverterLoader.loadConverters();
        }

    }

    public static void loadContainedConverters(Class<?> containerClass) {
        Class<?>[] classes = containerClass.getClasses();
        for (Class<?> clz : classes) {
            if ((clz.getModifiers() & Modifier.ABSTRACT) == 0) {
                try {
                    Object value = clz.getConstructor().newInstance();
                    if (value instanceof SpecificConverter) {
                        registerSpecificConverter((SpecificConverter) value);
                    }
                } catch (Exception e) {
                    logger.error("Error load converter ", e);
                }
            }
        }
    }

    private static String getKey(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.getName().concat(DELIMITER).concat(targetClass.getName());
    }

    public static void registerSpecificConverter(SpecificConverter<?, ?> specificConverter) {
        String key = getKey(specificConverter.getSourceClass(), specificConverter.getTargetClass());
        SPECIFIC_CONVERTER_CACHE.put(key, specificConverter);
    }

    static SpecificConverter<?, ?> findSupportConverter(Class<?> sourceClass, Class<?> targetClass) {
        String key = getKey(sourceClass, targetClass);
        return SUPPORT_SPECIFIC_CONVERTER_CACHE.computeIfAbsent(key, k -> SPECIFIC_CONVERTER_CACHE.values().stream()
                .filter(item -> item.support(sourceClass, targetClass)).findFirst()).orElse(null);
    }

    /**
     * 是否支持
     *
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static boolean support(Class<?> sourceClass, Class<?> targetClass) {
        return getConverter(sourceClass, targetClass) != null;
    }

    /**
     * 获取转换器
     *
     * @param sourceClass
     * @param targetClass
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Class<T> targetClass) {
        String key = getKey(sourceClass, targetClass);
        SpecificConverter<?, ?> specificConverter = SPECIFIC_CONVERTER_CACHE.get(key);

        if (specificConverter == null) {
            specificConverter = findSupportConverter(sourceClass, targetClass);
        }

        if (specificConverter == null) {
            return null;
        }
        SpecificConverter<S, T> finalSpecificConverter = (SpecificConverter<S, T>) specificConverter;

        return finalSpecificConverter::convert;
    }
}
