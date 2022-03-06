package com.sprint.common.converter.conversion.nested.bean;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title BeansException
 * @desc Bean转换异常
 * @since 2021年02月05日
 */
public class BeansException extends RuntimeException {

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