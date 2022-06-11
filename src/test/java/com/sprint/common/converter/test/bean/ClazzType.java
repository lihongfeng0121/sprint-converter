package com.sprint.common.converter.test.bean;

/**
 * @author hongfeng.li
 * @since 2022/6/11
 */
public class ClazzType<P, T> {

    private P p;
    private T t;

    public P getP() {
        return p;
    }

    public void setP(P p) {
        this.p = p;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
