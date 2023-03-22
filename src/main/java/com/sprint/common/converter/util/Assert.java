package com.sprint.common.converter.util;

import java.util.Objects;

/**
 * Assert
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class Assert {


    /**
     * 两个应该是相等
     *
     * @param obj1    o1
     * @param obj2    o2
     * @param message message
     */
    public static void equal(Object obj1, Object obj2, String message) {
        isTrue(Objects.equals(obj1, obj2), message);
    }

    /**
     * 表达式应为true
     *
     * @param expression expression
     * @param message    message
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 不能为空
     *
     * @param object  object
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
     * @param message    message
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
}
