package com.sprint.common.converter.exception;

/**
 * 转换异常类
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class ConversionExceptionWrapper extends RuntimeException {

    private static final long serialVersionUID = -7929713462157623641L;

    private final ConversionException conversionException;

    public ConversionExceptionWrapper(ConversionException conversionException) {
        super(conversionException.getMessage(), conversionException);
        this.conversionException = conversionException;
    }

    public ConversionException getConversionException() {
        return conversionException;
    }

    public static ConversionExceptionWrapper wrapper(ConversionException conversionException) {
        return new ConversionExceptionWrapper(conversionException);
    }
}
