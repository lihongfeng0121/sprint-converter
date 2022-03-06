package com.sprint.common.converter.conversion.nested.bean;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title FilterCallback
 * @desc FilterCallback
 * @since 2021年02月05日
 */
@FunctionalInterface
public interface FilterCallback {
    /**
     * 过滤
     *
     * @param prop 属性名
     * @param obj 对象
     * @return
     */
    Object filter(String prop, Object obj);
}
