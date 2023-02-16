package com.sprint.common.converter;

import com.sprint.common.converter.conversion.dynamic.DynamicConverters;
import com.sprint.common.converter.conversion.specific.SpecificConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.ConvertErrorException;
import com.sprint.common.converter.exception.NotSupportConvertException;
import com.sprint.common.converter.util.Defaults;
import com.sprint.common.converter.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础数据类型转换器
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public final class BaseConverter {

    private static final Logger logger = LoggerFactory.getLogger(BaseConverter.class);

    private BaseConverter() {
    }

    /**
     * 获取转换器
     *
     * @param sourceClass 源类型
     * @param targetClass 目标类型
     * @param <S>         s
     * @param <T>         t
     * @return Converter
     */
    public static <S, T> Converter<S, T> converter(Class<S> sourceClass, Class<T> targetClass) {
        if (Types.isObjectType(targetClass)) {
            return Converter.enforcer();
        }
        Converter<?, ?> converter = SpecificConverters.getConverter(sourceClass, targetClass);
        if (converter == null) {
            converter = DynamicConverters.getConverter(sourceClass, targetClass);
        }
        if (converter == null) {
            return null;
        }
        return converter.enforce(sourceClass, targetClass).onNullGet(() -> Defaults.defaultValue(targetClass));
    }

    /**
     * 是否支持转化
     *
     * @param sourceClass 源类型
     * @param targetClass 目标类型
     * @return 是否支持
     */
    public static boolean isSupport(Class<?> sourceClass, Class<?> targetClass) {
        return converter(sourceClass, targetClass) != null;
    }

    /**
     * 忽略转换异常
     *
     * @param source      源对象
     * @param targetClass 目标类型
     * @param <T>         t
     * @return target
     */
    public static <T> T convertIgnore(Object source, Class<T> targetClass) {
        try {
            return convert(source, targetClass, (ex, var1) -> Defaults.defaultValue(targetClass));
        } catch (Exception e) {
            logger.warn(String.format("not support [%s][%s] -> %s", source.getClass(), source, targetClass.getName()),
                    e);
            return Defaults.defaultValue(targetClass);
        }
    }

    /**
     * 转换
     *
     * @param source      源
     * @param targetClass 目标类型
     * @param <T>         t
     * @return target
     * @throws ConversionException ex
     */
    public static <T> T convert(Object source, Class<T> targetClass) throws ConversionException {
        return convert(source, targetClass, null);
    }

    /**
     * 基础类型异常处理
     *
     * @param sourceType sourceType
     * @param targetType targetType
     * @param <S>        s
     * @param <T>        t
     * @return 异常处理
     */
    public static <S, T> ErrorHandler<S, T> baseErrorHandler(Class<?> sourceType, Class<?> targetType) {
        return (ex, s) -> {
            if (BaseConverter.isSupport(sourceType, targetType)) {
                return Converter.enforce(convert(s, targetType));
            } else {
                throw new NotSupportConvertException(sourceType, targetType, ex);
            }
        };
    }

    /**
     * 转换
     *
     * @param source       源
     * @param targetClass  target类型
     * @param errorHandler 异常处理
     * @param <T>          t
     * @return target
     * @throws ConversionException ex
     */
    public static <T> T convert(Object source, Class<T> targetClass, ErrorHandler<Object, T> errorHandler)
            throws ConversionException {
        if (targetClass == null) {
            throw new IllegalArgumentException("Target type is missing");
        }
        if (source == null) {
            return Defaults.defaultValue(targetClass);
        }
        Class<?> sourceClass = source.getClass();
        Converter<?, T> converter = converter(sourceClass, targetClass);

        if (converter == null) {
            NotSupportConvertException ex = new NotSupportConvertException(sourceClass, targetClass);
            if (errorHandler == null) {
                logger.error(
                        String.format("not support %s -> %s, source val:%s", source.getClass().getName(),
                                targetClass.getName(), source));
                throw ex;
            } else {
                logger.warn(String.format("not support %s -> %s, source val:%s, use error handler.", source.getClass().getName(),
                        targetClass.getName(), source));
                return errorHandler.handle(ex, source);
            }
        } else {
            try {
                Converter<Object, T> enforce = converter.enforce();
                return enforce.convert(source);
            } catch (Exception e) {
                ConvertErrorException ex;
                if (e instanceof ConvertErrorException) {
                    ex = (ConvertErrorException) e;
                } else {
                    ex = new ConvertErrorException(source, targetClass, e);
                }

                if (errorHandler == null) {
                    logger.error(String.format("convert %s -> %s error, source val:%s", source.getClass().getName(),
                            targetClass.getName(), source), ex);
                    throw ex;
                } else {
                    logger.warn(String.format("convert %s -> %s error, source val:%s, use error handler.", source.getClass().getName(),
                            targetClass.getName(), source), ex);
                    return errorHandler.handle(ex, source);
                }
            }
        }
    }
}
