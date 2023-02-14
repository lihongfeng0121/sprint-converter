package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.BaseConverter;
import com.sprint.common.converter.Converter;
import com.sprint.common.converter.ErrorHandler;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.Defaults;
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
    public static <S, T> Converter<S, T> getConverter(Class<?> sourceClass, Type targetBeanType, Type targetType) {
        Converter<?, ?> stConverter = doGetConverter(sourceClass, targetBeanType, targetType);
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
        return doGetConverter(sourceClass, null, targetClass).enforce();
    }

    private static Converter<?, ?> doGetConverter(Class<?> sourceClass, Type targetBeanType, Type targetType) {
        Class<?> targetClass = Types.extractClass(targetType, targetBeanType);
        Class<?> finalTargetClass = Types.isObjectType(targetClass) ? sourceClass : targetClass;
        List<NestedConverter> nestedConverters = NESTED_CONVERTERS.stream()
                .filter(item -> item.support(sourceClass, finalTargetClass)).collect(Collectors.toList());
        Converter<Object, Object> defaultConverter = (source) -> DEFAULT_NESTED_CONVERTER.convert(source, targetBeanType, targetType);
        if (nestedConverters.size() == 0) {
            return defaultConverter;
        }

        return (source) -> {
            for (NestedConverter nestedConverter : nestedConverters) {
                if (nestedConverter.preCheckSourceVal(source)) {
                    try {
                        return nestedConverter.convert(source, targetBeanType, targetType);
                    } catch (ConversionException e) {
                        logger.warn("this converter not support, use next converter, convert msg :{}", e.getMessage());
                    }
                }
            }
            return DEFAULT_NESTED_CONVERTER.convert(source, targetBeanType, targetType);
        };
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
            return getDefaultValue(targetBeanType, targetType);
        }
        Class<?> sourceClass = value.getClass();
        Converter<S, T> converter = getConverter(sourceClass, targetBeanType, targetType);
        if (converter == null) {
            throw new NotSupportConvertException(sourceClass, Types.extractClass(targetType, targetBeanType));
        }
        return converter.convert(value);
    }

    private static <T> T getDefaultValue(Type targetBeanType, Type targetType) {
        Class<?> targetClass = Types.extractClass(targetType, targetBeanType);
        return Converter.enforce(Defaults.defaultValue(targetClass));
    }

    /**
     * 获取转换器
     *
     * @param value          value
     * @param targetBeanType targetBeanType
     * @param targetType     targetType
     * @param errorHandler   errorHandler
     * @param <S>            s
     * @param <T>            t
     * @return target
     * @throws ConversionException e
     */
    public static <S, T> T convert(S value, Type targetBeanType, Type targetType, ErrorHandler<S, T> errorHandler) throws ConversionException {
        if (value == null) {
            return getDefaultValue(targetBeanType, targetType);
        }
        Class<?> sourceClass = value.getClass();
        Converter<S, T> converter = getConverter(sourceClass, targetBeanType, targetType);
        if (converter == null) {
            throw new NotSupportConvertException(sourceClass, Types.extractClass(targetType, targetBeanType));
        }
        return converter.onError(errorHandler).convert(value);
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
            if (Types.isObjectType(targetClassType)
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
                return BaseConverter.convert(sourceValue, targetClassType);
            }
        }
    }
}
