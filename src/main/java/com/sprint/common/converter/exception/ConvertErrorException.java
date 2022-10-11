package com.sprint.common.converter.exception;

/**
 * 转换异常类
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2019年12月25日
 */
public class ConvertErrorException extends ConversionException {

    private static final String FORMAT = "convert %s -> %s error, source val:%s";
    private static final long serialVersionUID = 8602175919841132114L;

    private final Object source;
    private final Class<?> targetType;

    public ConvertErrorException(Object source, Class<?> targetType, Throwable cause) {
        super(String.format(FORMAT, source.getClass().getName(), targetType.getName(), source), cause);
        this.source = source;
        this.targetType = targetType;
    }

    public Object getSource() {
        return source;
    }

    public Class<?> getTargetType() {
        return targetType;
    }
}
