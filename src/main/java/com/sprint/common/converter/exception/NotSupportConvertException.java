package com.sprint.common.converter.exception;

import java.lang.reflect.Type;

/**
 * 转换异常类
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2019年12月25日
 */
public class NotSupportConvertException extends ConversionException {

    private static final String FORMAT = "not support %s -> %s";
    private static final long serialVersionUID = -1923092816144168582L;

    private final Type sourceType;
    private final Type targetType;

    public NotSupportConvertException(Class<?> sourceType, Class<?> targetType, Throwable cause) {
        super(String.format(FORMAT, sourceType.getTypeName(), targetType.getTypeName()));
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public NotSupportConvertException(Type sourceType, Type targetType) {
        super(String.format(FORMAT, sourceType.getTypeName(), targetType.getTypeName()));
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public Type getSourceType() {
        return sourceType;
    }

    public Type getTargetType() {
        return targetType;
    }
}
