package com.sprint.common.converter.conversion.nested.json;

/**
 * Json异常
 *
 * @author hongfeng-li
 * @version 1.0
 * @since 2021年02月05日
 */
public class JsonException extends Exception {

    private static final long serialVersionUID = -4053019964196284727L;

    public JsonException() {
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}