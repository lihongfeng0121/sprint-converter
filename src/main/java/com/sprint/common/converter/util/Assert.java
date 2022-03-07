package com.sprint.common.converter.util;

/**
 * Assert
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class Assert {

    /**
     * 表达式应为true
     *
     * @param expression expression
     * @param message message
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 不能为空
     *
     * @param object object
     * @param message message
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 状态判断
     *
     * @param expression expression
     * @param message message
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
}
