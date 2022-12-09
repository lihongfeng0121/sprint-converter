package com.sprint.common.converter.exception;

/**
 * Beans 转换异常
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class BeansException extends ConversionException {

    private static final long serialVersionUID = 3415606102477064509L;

    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(Throwable cause) {
        super(cause);
    }

    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}