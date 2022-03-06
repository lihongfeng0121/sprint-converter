package com.sprint.common.converter.exception;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title NotSupportConvertException
 * @desc 转换异常类
 * @since 2019年12月25日
 */
public class NotSupportConvertException extends ConversionException {

    private static final String FORMAT = "not support %s -> %s";
    private static final long serialVersionUID = -1923092816144168582L;

    private final Class<?> sourceType;
    private final Class<?> targetType;

    public NotSupportConvertException(Class<?> sourceType, Class<?> targetType, Throwable cause) {
        super(String.format(FORMAT, sourceType.getName(), targetType.getName()), cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public NotSupportConvertException(Class<?> sourceType, Class<?> targetType) {
        super(String.format(FORMAT, sourceType.getName(), targetType.getName()));
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public Class<?> getTargetType() {
        return targetType;
    }
}
