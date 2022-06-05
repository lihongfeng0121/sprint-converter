package com.sprint.common.converter.test.bean;

/**
 * @author hongfeng.li
 * @since 2021/2/11
 */
public class TestBean<T> {
    private T obj;

    public TestBean() {
    }

    public TestBean(T obj) {
        this.obj = obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public T getObj() {
        return obj;
    }

    @Override
    public String toString() {
        return "TestBean{" + "obj=" + obj + '}';
    }
}
