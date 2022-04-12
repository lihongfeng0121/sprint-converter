package com.sprint.common.converter;

/**
 * @author hongfeng.li
 * @since 2022/4/7
 */
public interface AroundHandler<S, T> {

    /**
     * 前置处理
     *
     * @param source 源
     * @return 源
     */
    default S before(S source) {
        return source;
    }

    /**
     * 后置处理
     *
     * @param source 源
     * @param target 目标
     * @return 目标
     */
    default T after(S source, T target) {
        return target;
    }
}
