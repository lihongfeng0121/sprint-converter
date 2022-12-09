package com.sprint.common.converter.exception;

/**
 * Json异常
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2021年02月05日
 */
public class JsonException extends ConversionException {

    private static final long serialVersionUID = -4053019964196284727L;

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

}