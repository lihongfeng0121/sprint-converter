package com.sprint.common.converter.test.bean;

/**
 * @author hongfeng.li
 * @since 2021/2/11
 */
public class TestBean<T> {
    private T str;

    public TestBean() {
    }

    public TestBean(T str) {
        this.str = str;
    }

    public void setStr(T str) {
        this.str = str;
    }

    public T getStr() {
        return str;
    }

    @Override
    public String toString() {
        return "TestBean{" + "str=" + str + '}';
    }
}
