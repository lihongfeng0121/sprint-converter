package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.BaseConverters;
import com.sprint.common.converter.Converter;
import com.sprint.common.converter.conversion.nested.bean.Beans;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.ConvertErrorException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.ConcurrentReferenceHashMap;
import com.sprint.common.converter.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 嵌套属性转换
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public final class NestedConverters {

    private static final Logger logger = LoggerFactory.getLogger(NestedConverters.class);

    private static final List<NestedConverter> NESTED_CONVERTERS = new LinkedList<>();
    private static final NestedConverter DEFAULT_NESTED_CONVERTER = new DefaultNestedConverter();

    private static final ConcurrentReferenceHashMap<String, Optional<Converter<?, ?>>> SPECIFIC_NESTED_CONVERTER_CACHE = new ConcurrentReferenceHashMap<>();
    private static final String DELIMITER = "->";


    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        for (NestedConverterLoader nestedConverterLoader : ServiceLoader.load(NestedConverterLoader.class, loader)) {
            nestedConverterLoader.loadConverters();
        }
    }

    public static void loadContainedConverters(Class<?> loader) {
        Class<?>[] classes = loader.getClasses();
        for (Class<?> clz : classes) {
            if ((clz.getModifiers() & Modifier.ABSTRACT) == 0) {
                try {
                    Object value = clz.getConstructor().newInstance();
                    if (value instanceof NestedConverter) {
                        addPropertyConverter((NestedConverter) value);
                    }
                } catch (Exception e) {
                    logger.error("Error Load Property Converter ", e);
                }
            }
        }
    }

    public static void addPropertyConverter(NestedConverter nestedConverter) {
        NESTED_CONVERTERS.add(nestedConverter);
        NESTED_CONVERTERS.sort(Comparator.comparingInt(NestedConverter::sort));
    }

    private static String getKey(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.getName().concat(DELIMITER).concat(targetClass.getName());
    }

    /**
     * 获取转换器
     *
     * @param sourceClass    sourceClass
     * @param targetBeanType targetBeanType
     * @param targetType     targetType
     * @param <S>            s
     * @param <T>            t
     * @return converter
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Type targetBeanType, Type targetType) {
        Converter<S, ?> stConverter = getCacheConverter(sourceClass, targetBeanType, targetType);
        if (stConverter == null) {
            return null;
        }
        return stConverter.enforce();
    }

    /**
     * 获取转换器
     *
     * @param sourceClass sourceClass
     * @param targetClass targetClass
     * @param <S>         s
     * @param <T>         t
     * @return converter
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Class<T> targetClass) {
        return getCacheConverter(sourceClass, null, targetClass);
    }

    private static <S, T> Converter<S, T> getCacheConverter(Class<S> sourceClass, Type targetBeanType, Type targetType) {
        Class<?> targetClass = Types.extractClass(targetType, targetBeanType);
        Optional<Converter<?, ?>> converterOptional = SPECIFIC_NESTED_CONVERTER_CACHE.computeIfAbsent(getKey(sourceClass, targetClass), (k) -> {
            List<NestedConverter> nestedConverters = NESTED_CONVERTERS.stream()
                    .filter(item -> item.support(sourceClass, targetClass)).collect(Collectors.toList());

            Converter<Object, Object> defaultConverter = (source) -> DEFAULT_NESTED_CONVERTER.convert(source, targetBeanType, targetType);

            if (nestedConverters.size() == 0) {
                return Optional.of(defaultConverter);
            }

            Converter<Object, Object> converter = (source) -> nestedConverters.get(0).convert(source, targetBeanType, targetType);

            for (int i = 1, length = nestedConverters.size(); i < length; i++) {
                int index = i;
                converter = converter.onError(((ex, source) -> {
                    if (ex instanceof NotSupportConvertException) {
                        return nestedConverters.get(index).convert(source, targetBeanType, targetType);
                    }
                    throw new ConvertErrorException(source, targetClass, ex);
                }));
            }

            converter = converter.onError(((ex, source) -> {
                if (ex instanceof NotSupportConvertException) {
                    return defaultConverter.convert(source);
                }
                throw new ConvertErrorException(source, targetClass, ex);
            }));

            return Optional.of(converter);
        });
        return converterOptional.<Converter<S, T>>map(Converter::enforce).orElse(null);
    }

    /**
     * 获取转换器
     *
     * @param value          value
     * @param targetBeanType targetBeanType
     * @param targetType     targetType
     * @param <S>            s
     * @param <T>            t
     * @return target
     * @throws ConversionException e
     */
    public static <S, T> T convert(S value, Type targetBeanType, Type targetType) throws ConversionException {
        if (value == null) {
            return null;
        }
        Class<?> sourceClass = value.getClass();
        Converter<?, ?> converter = getConverter(sourceClass, targetBeanType, targetType);
        if (converter == null) {
            throw new NotSupportConvertException(sourceClass, Types.extractClass(targetType, targetBeanType));
        }
        Converter<S, T> enforce = converter.enforce();
        return enforce.convert(value);
    }

    /**
     * 默认属性转化器
     */
    public static class DefaultNestedConverter implements NestedConverter {

        @Override
        public boolean support(Class<?> sourceClass, Class<?> targetClass) {
            return true;
        }

        @Override
        public Object convert(Object sourceValue, Type targetBeanType, Type targetFiledType)
                throws ConversionException {
            if (sourceValue == null) {
                return null;
            }

            Class<?> sourceType = sourceValue.getClass();
            Class<?> targetClassType = Types.extractClass(targetFiledType, targetBeanType);
            if (Types.isPrimitiveTypeOrWrapClass(targetClassType) && Objects.equals(sourceType, targetClassType)) {
                return sourceValue;
            }
            if (Objects.equals(targetClassType, Object.class)
                    && (Types.isBean(sourceType) || Types.isMulti(sourceType))) {
                return NestedConverters.convert(sourceValue, null, sourceType);
            } else if (targetClassType.isInterface() && targetClassType.isAssignableFrom(sourceType)) {
                return castBaseOrBase(sourceType, sourceType, sourceValue, sourceType);
            } else if (Modifier.isAbstract(targetClassType.getModifiers())
                    && targetClassType.isAssignableFrom(sourceType)) {
                return castBaseOrBase(sourceType, sourceType, sourceValue, sourceType);
            } else {
                return castBaseOrBase(sourceType, targetClassType, sourceValue, Types.getComponentType(targetBeanType, targetFiledType));
            }
        }

        private Object castBaseOrBase(Class<?> sourceClassType, Class<?> targetClassType, Object sourceValue, Type targetType) throws ConversionException {
            if (Types.isBean(sourceClassType) && Types.isBean(targetClassType)) {
                return Beans.cast(sourceValue, targetType, true);
            } else {
                return BaseConverters.convert(sourceValue, targetClassType);
            }
        }
    }
}
