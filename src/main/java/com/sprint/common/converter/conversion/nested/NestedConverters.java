package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.BaseConverter;
import com.sprint.common.converter.Converter;
import com.sprint.common.converter.ErrorHandler;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.Defaults;
import com.sprint.common.converter.util.TypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
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
        Converter<?, ?> stConverter = doGetConverter(sourceType, targetType);
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
    public static <S, T> Converter<S, T> getConverter(Class<?> sourceClass, Class<?> targetClass) {
        return doGetConverter(TypeDescriptor.of(sourceClass), TypeDescriptor.of(targetClass)).enforce();
    }

    private static Converter<?, ?> doGetConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        TypeDescriptor finalTargetType = targetType.isObjectType() ? sourceType : targetType;
        List<NestedConverter> nestedConverters = NESTED_CONVERTERS.stream()
                .filter(item -> item.support(sourceType, finalTargetType)).collect(Collectors.toList());
        if (nestedConverters.size() == 0) {
            return (source) -> DEFAULT_NESTED_CONVERTER.convert(source, finalTargetType);
        }
        return (source) -> {
            if (source == null) {
                return Converter.enforce(Defaults.defaultValue(finalTargetType.getActualClass()));
            }
            for (NestedConverter nestedConverter : nestedConverters) {
                if (nestedConverter.preCheckSourceVal(source)) {
                    try {
                        return nestedConverter.convert(source, finalTargetType);
                    } catch (ConversionException e) {
                        logger.warn("this converter not support, use next converter, convert msg :{}", e.getMessage());
                    }
                }
            }
            return DEFAULT_NESTED_CONVERTER.convert(source, finalTargetType);
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
