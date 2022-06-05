package com.sprint.common.converter.util;

/**
 * @author hongfeng.li
 * @since 2022/6/5
 */
public class MiscUtils {

    /**
     * 获取数据元素
     *
     * @param ts           数组
     * @param i            位置
     * @param defaultValue 默认值
     * @param <T>          数组元素类型
     * @return 数组元素
     */
    public static <T> T at(T[] ts, int i, T defaultValue) {
        if (ts == null) {
            return defaultValue;
        }
        if (i < 0) {
            i += ts.length;
        }
        return ts.length > i ? ts[i] : defaultValue;
    }

    /**
     * 类型转化
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> T cast(Object object) {
        return (T) object;
    }
}
