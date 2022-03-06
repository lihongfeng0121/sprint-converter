package com.sprint.common.converter.conversion.dynamic;

import com.sprint.common.converter.Converter;
import com.sprint.common.converter.util.ConcurrentReferenceHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title DynamicConverters
 * @desc 动态属性转换器
 * @since 2021年02月05日
 */
public final class DynamicConverters {

    private static final Logger logger = LoggerFactory.getLogger(DynamicConverters.class);

    private static final List<DynamicConverter<?>> DYNAMIC_CONVERTER_CACHE = new LinkedList<>();
    private static final ConcurrentReferenceHashMap<String, Optional<Converter<?, ?>>> cache = new ConcurrentReferenceHashMap<>();
    private static final String DELIMITER = "->";

    private DynamicConverters() {
    }

    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        for (DynamicConverterLoader dynamicConverterLoader : ServiceLoader.load(DynamicConverterLoader.class, loader)) {
            dynamicConverterLoader.loadConverters();
        }
    }

    public static void loadContainedConverters(Class<?> containerClass) {
        Class<?>[] classes = containerClass.getClasses();
        for (Class<?> clz : classes) {
            if ((clz.getModifiers() & Modifier.ABSTRACT) == 0) {
                try {
                    Object value = clz.getConstructor().newInstance();
                    if (value instanceof DynamicConverter) {
                        registerDynamicConverter((DynamicConverter<?>) value);
                    }
                } catch (Exception e) {
                    logger.error("Error load converter ", e);
                }
            }
        }
    }

    public static void registerDynamicConverter(DynamicConverter<?> dynamicConverter) {
        DYNAMIC_CONVERTER_CACHE.add(dynamicConverter);
        DYNAMIC_CONVERTER_CACHE.sort(Comparator.comparing(DynamicConverter::sort));
    }

    private static String getKey(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.getName().concat(DELIMITER).concat(targetClass.getName());
    }

    /**
     * 是否支持
     *
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static boolean support(Class<?> sourceClass, Class<?> targetClass) {
        return DYNAMIC_CONVERTER_CACHE.stream().anyMatch(item -> item.support(sourceClass, targetClass));
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
        Optional<Converter<?, ?>> converterOptional = cache.computeIfAbsent(getKey(sourceClass, targetClass), (k) -> {
            DynamicConverter<?> dynamicConverter = DYNAMIC_CONVERTER_CACHE.stream()
                    .filter(item -> item.support(sourceClass, targetClass)).findFirst().orElse(null);
            if (dynamicConverter == null) {
                return Optional.empty();
            }
            return Optional.of((source) -> ((DynamicConverter<T>) dynamicConverter).convert(source, targetClass));
        });
        return converterOptional.<Converter<S, T>> map(Converter::enforce).orElse(null);
    }

}
