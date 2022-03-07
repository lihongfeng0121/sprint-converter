package com.sprint.common.converter.test.bean;

/**
 * @author hongfeng.li
 * @since 2021/1/25
 */
public class Inner2 {
    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "Inner2{" + "test='" + test + '\'' + '}';
    }
}
