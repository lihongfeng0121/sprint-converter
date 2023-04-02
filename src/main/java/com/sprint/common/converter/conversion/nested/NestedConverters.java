package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.BaseConverter;
import com.sprint.common.converter.Converter;
import com.sprint.common.converter.ErrorHandler;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.ConcurrentReferenceHashMap;
import com.sprint.common.converter.util.Defaults;
import com.sprint.common.converter.util.TypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

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

    private static final ConcurrentReferenceHashMap<String, Optional<Converter<?, ?>>> cache = new ConcurrentReferenceHashMap<>();
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
            if (!Modifier.isAbstract(clz.getModifiers())) {
                try {
                    Object value = clz.getConstructor().newInstance();
                    if (value instanceof NestedConverter) {
                        registerNestedConverter((NestedConverter) value);
                    }
                } catch (Exception e) {
                    logger.error("Error Load Property Converter ", e);
                }
            }
        }
    }

    public static void registerNestedConverter(NestedConverter nestedConverter) {
        NESTED_CONVERTERS.add(nestedConverter);
        NESTED_CONVERTERS.sort(Comparator.comparingInt(NestedConverter::sort));
    }

    private static String getKey(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.getId().concat(DELIMITER).concat(targetType.getId());
    }

    /**
     * 获取转换器
     *
     * @param sourceType sourceType
     * @param targetType targetType
     * @param <S>        s
     * @param <T>        t
     * @return converter
     */
    public static <S, T> Converter<S, T> getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        TypeDescriptor finalTargetType = targetType.isObjectType() ? sourceType : targetType;
        if (sourceType.isObjectType() && finalTargetType.isObjectType()) {
            Converter<?, ?> stConverter = doGetConverter(sourceType, finalTargetType);
            return stConverter.enforce();
        } else {
            Optional<Converter<?, ?>> converterOptional = cache.computeIfAbsent(getKey(sourceType, finalTargetType), (k) -> Optional.of(doGetConverter(sourceType, finalTargetType)));
            return converterOptional.<Converter<S, T>>map(converter -> converter.enforce()).orElse(null);
        }
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
    public static <S, T> Converter<S, T> getConverter(Class<?> sourceClass, Class<?> targetClass) {
        return getConverter(TypeDescriptor.of(sourceClass), TypeDescriptor.of(targetClass)).enforce();
    }

    private static Converter<?, ?> doGetConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        List<NestedConverter> supports = new ArrayList<>();
        for (NestedConverter item : NESTED_CONVERTERS) {
            if (item.support(sourceType, targetType)) {
                supports.add(item);
            }
        }
        if (supports.size() == 0) {
            return (source) -> DEFAULT_NESTED_CONVERTER.convert(source, targetType);
        }
        return (source) -> {
            if (source == null) {
                return Converter.enforce(Defaults.defaultValue(targetType.getActualClass()));
            }
            for (NestedConverter nestedConverter : supports) {
                if (nestedConverter instanceof NestedPreCheckConverter) {
                    if (((NestedPreCheckConverter) nestedConverter).preCheckSourceVal(source, targetType)) {
                        try {
                            return nestedConverter.convert(source, targetType);
                        } catch (ConversionException e) {
                            logger.warn("this converter not support, use next converter, convert msg :{}", e.getMessage());
                        }
                    }
                } else {
                    try {
                        return nestedConverter.convert(source, targetType);
                    } catch (ConversionException e) {
                        logger.warn("this converter not support, use next converter, convert msg :{}", e.getMessage());
                    }
                }
            }
            return DEFAULT_NESTED_CONVERTER.convert(source, targetType);
        };
    }

    /**
     * 获取转换器
     *
     * @param value      value
     * @param targetType targetType
     * @param <S>        s
     * @param <T>        t
     * @return target
     * @throws ConversionException e
     */
    public static <S, T> T convert(S value, TypeDescriptor targetType) throws ConversionException {
        if (value == null) {
            return getDefaultValue(targetType);
        }
        TypeDescriptor sourceType = TypeDescriptor.of(value.getClass());
        Converter<S, T> converter = getConverter(sourceType, targetType);
        if (converter == null) {
            throw new NotSupportConvertException(sourceType.getActualClass(), targetType.getActualClass());
        }
        return converter.convert(value);
    }

    private static <T> T getDefaultValue(TypeDescriptor typeDescriptor) {
        return Converter.enforce(Defaults.defaultValue(typeDescriptor.getActualClass()));
    }

    /**
     * 获取转换器
     *
     * @param value        value
     * @param targetType   targetType
     * @param errorHandler errorHandler
     * @param <S>          s
     * @param <T>          t
     * @return target
     * @throws ConversionException e
     */
    public static <S, T> T convert(S value, TypeDescriptor targetType, ErrorHandler<S, T> errorHandler) throws ConversionException {
        if (value == null) {
            return getDefaultValue(targetType);
        }
        TypeDescriptor sourceType = TypeDescriptor.of(value.getClass());
        Converter<S, T> converter = getConverter(sourceType, targetType);
        if (converter == null) {
            throw new NotSupportConvertException(sourceType.getActualClass(), targetType.getActualClass());
        }
        return converter.onError(errorHandler).convert(value);
    }

    /**
     * 默认属性转化器
     */
    public static class DefaultNestedConverter implements NestedConverter {

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return true;
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor)
                throws ConversionException {
            if (sourceValue == null) {
                return getDefaultValue(targetTypeDescriptor);
            }
            TypeDescriptor sourceType = TypeDescriptor.of(sourceValue.getClass());
            if (targetTypeDescriptor.isPrimitiveTypeOrWrapClass() && targetTypeDescriptor.actualTypeEquals(sourceType)) {
                return sourceValue;
            }
            if (targetTypeDescriptor.isObjectType() && (sourceType.isBean() || sourceType.isMulti())) {
                return NestedConverters.convert(sourceValue, sourceType);
            } else if ((targetTypeDescriptor.isInterface() || targetTypeDescriptor.isAbstract()) && targetTypeDescriptor.isAssignableFrom(sourceType)) {
                return castBaseOrBase(sourceType, sourceType, sourceValue);
            } else {
                return castBaseOrBase(sourceType, targetTypeDescriptor, sourceValue);
            }
        }

        private Object castBaseOrBase(TypeDescriptor sourceType, TypeDescriptor targetType, Object sourceValue) throws ConversionException {
            if (sourceType.isBeanOrMap() && targetType.isBeanOrMap()) {
                return Beans.cast(sourceValue, targetType.getActualType(), true);
            } else {
                return BaseConverter.convert(sourceValue, targetType.getActualClass());
            }
        }
    }
}
