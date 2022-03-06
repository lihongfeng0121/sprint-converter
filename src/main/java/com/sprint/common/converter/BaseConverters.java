package com.sprint.common.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sprint.common.converter.conversion.dynamic.DynamicConverters;
import com.sprint.common.converter.conversion.specific.SpecificConverters;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.ConversionExceptionWrapper;
import com.sprint.common.converter.exception.ConvertErrorException;
import com.sprint.common.converter.exception.NotSupportConvertException;

/**
 * @author hongfeng-li
 * @version 1.0
 * @title Converts
 * @desc 转换器
 * @date 2019年12月25日
 */
public final class BaseConverters {

    private static final Logger logger = LoggerFactory.getLogger(BaseConverters.class);

    private BaseConverters() {
    }

    /**
     * 获取转换器
     * 
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Class<T> targetClass) {
        if (Object.class == targetClass) {
            return Converter.enforcer();
        }
        Converter<?, ?> converter = SpecificConverters.getConverter(sourceClass, targetClass);
        if (converter == null) {
            converter = DynamicConverters.getConverter(sourceClass, targetClass);
        }

        if (converter == null) {
            return null;
        }
        return converter.enforce();
    }

    /**
     * 是否支持转化
     *
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static boolean isSupport(Class<?> sourceClass, Class<?> targetClass) {
        return getConverter(sourceClass, targetClass) != null;
    }

    /**
     * 忽略转换异常
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T convertIgnore(Object source, Class<T> targetClass) {
        try {
            return convert(source, targetClass, (ex, var1) -> null);
        } catch (Exception e) {
            logger.warn(String.format("not support [%s][%s] -> %s", source.getClass(), source, targetClass.getName()),
                    e);
            return null;
        }
    }

    /**
     * 忽略 运行时异常
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T convertRuntime(Object source, Class<T> targetClass) {
        try {
            return convert(source, targetClass, (ex, var1) -> null);
        } catch (ConversionException e) {
            logger.error(String.format("not support [%s][%s] -> %s", source.getClass(), source, targetClass.getName()),
                    e);
            throw ConversionExceptionWrapper.wrapper(e);
        }
    }

    /**
     * 转换
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     * @throws ConversionException
     */
    public static <T> T convert(Object source, Class<T> targetClass) throws ConversionException {
        return convert(source, targetClass, (ex, var1) -> {
            throw new ConversionException(
                    String.format("not support [%s][%s] -> %s", source.getClass(), source, targetClass.getName()), ex);
        });
    }

    /**
     * 转换
     *
     * @param source
     * @param targetClass
     * @param errorHandler
     * @param <T>
     * @return
     * @throws ConversionException
     */
    public static <T> T convert(Object source, Class<T> targetClass, ErrorHandler<Object, T> errorHandler)
            throws ConversionException {
        if (source == null) {
            return null;
        }
        if (targetClass == null) {
            throw new IllegalArgumentException("Target type is missing");
        }

        Class<?> sourceClass = source.getClass();
        Converter<?, T> converter = getConverter(sourceClass, targetClass);

        if (converter == null) {
            NotSupportConvertException ex = new NotSupportConvertException(sourceClass, targetClass);
            if (errorHandler == null) {
                logger.error(
                        String.format("not support [%s][%s] -> %s", source.getClass(), source, targetClass.getName()));
                throw ex;
            } else {
                logger.warn(String.format("not support [%s][%s] -> %s, use error handler.", source.getClass(), source,
                        targetClass.getName()));
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
                    logger.error(String.format("not support [%s][%s] -> %s", source.getClass(), source,
                            targetClass.getName()), ex);
                    throw ex;
                } else {
                    logger.warn(String.format("not support [%s][%s] -> %s, use error handler.", source.getClass(),
                            source, targetClass.getName()), ex);
                    return errorHandler.handle(ex, source);
                }
            }
        }
    }
}
