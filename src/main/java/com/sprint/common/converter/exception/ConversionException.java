package com.sprint.common.converter.exception;

/**
 * 转换异常类
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2019年12月25日
 */
public class ConversionException extends RuntimeException {

    private static final long serialVersionUID = -6242450642491108243L;

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }
}
