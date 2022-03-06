package com.sprint.common.converter.exception;

/**
 * @author hongfeng-li
 * @version 1.0
 * @title ConversionException
 * @desc 转换异常类
 * @date 2019年12月25日
 */
public class ConversionException extends Exception {

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
