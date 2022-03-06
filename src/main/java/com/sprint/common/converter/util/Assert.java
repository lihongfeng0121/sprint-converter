package com.sprint.common.converter.util;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title Assert
 * @desc Assert
 * @since 2021年02月05日
 */
public class Assert {

    /**
     * 表达式应为true
     *
     * @param expression
     * @param message
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 不能为空
     *
     * @param object
     * @param message
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 状态判断
     *
     * @param expression
     * @param message
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
}
