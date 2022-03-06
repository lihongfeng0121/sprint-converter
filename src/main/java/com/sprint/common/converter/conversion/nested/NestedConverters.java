package com.sprint.common.converter.conversion.nested;

import com.sprint.common.converter.BaseConverters;
import com.sprint.common.converter.Converter;
import com.sprint.common.converter.util.Types;
import com.sprint.common.converter.conversion.nested.bean.Beans;
import com.sprint.common.converter.exception.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title NestedConverters
 * @desc 嵌套属性转换
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

    /**
     * 获取转换器
     * 
     * @param sourceClass
     * @param targetBeanType
     * @param targetFiledType
     * @return
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Type targetBeanType, Type targetFiledType) {
        Class<?> targetClass = Types.extractClass(targetFiledType, targetBeanType);
        NestedConverter nestedConverter = NESTED_CONVERTERS.stream()
                .filter(item -> item.support(sourceClass, targetClass)).findFirst().orElse(DEFAULT_NESTED_CONVERTER);
        Converter<Object, Object> converter = (source) -> nestedConverter.convert(source, targetBeanType,
                targetFiledType);
        return converter.enforce();
    }

    /**
     * 获取转换器
     * 
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Class<T> targetClass) {
        NestedConverter nestedConverter = NESTED_CONVERTERS.stream()
                .filter(item -> item.support(sourceClass, targetClass)).findFirst().orElse(DEFAULT_NESTED_CONVERTER);
        Converter<Object, Object> converter = (source) -> nestedConverter.convert(source, null, targetClass);
        return converter.enforce();
    }

    /**
     * 获取转换器
     * 
     * @param value
     * @param targetBeanType
     * @param targetFiledType
     * @return
     */
    public static <S, T> Converter<S, T> getConverter(S value, Type targetBeanType, Type targetFiledType) {
        Class<?> extractClass = Types.extractClass(targetFiledType, targetBeanType);
        NestedConverter nestedConverter = NESTED_CONVERTERS.stream().filter(item -> item.support(value, extractClass))
                .findFirst().orElse(DEFAULT_NESTED_CONVERTER);
        Converter<S, Object> converter = (source) -> nestedConverter.convert(source, targetBeanType, targetFiledType);
        return converter.enforce();
    }

    /**
     * 转化为目标类型
     *
     * @param targetBeanType
     * @param targetType
     * @param value
     * @return
     * @throws ConversionException
     */
    public static <S,T> T convert(S value, Type targetBeanType, Type targetType) throws ConversionException {
        if (value == null) {
            return null;
        }
        Converter<S, T> converter = getConverter(value, targetBeanType, targetType);
        return converter.convert(value);
    }

    /**
     * 默认属性转化器
     */
    public static class DefaultNestedConverter implements NestedConverter {

        @Override
        public boolean support(Object sourceValue, Class<?> sourceClass, Class<?> targetClass) {
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
                return Beans.cast(sourceValue, sourceValue.getClass());
            } else if (targetClassType.isInterface() && targetClassType.isAssignableFrom(sourceType)) {
                return Beans.cast(sourceValue, sourceValue.getClass());
            } else if (Modifier.isAbstract(targetClassType.getModifiers())
                    && targetClassType.isAssignableFrom(sourceValue.getClass())) {
                return Beans.cast(sourceValue, sourceValue.getClass());
            } else if (Types.isBean(sourceType) && Types.isBean(targetClassType)) {
                return Beans.cast(sourceValue, Types.getComponentType(targetBeanType, targetFiledType), true);
            } else {
                return BaseConverters.convert(sourceValue, targetClassType);
            }
        }
    }
}
