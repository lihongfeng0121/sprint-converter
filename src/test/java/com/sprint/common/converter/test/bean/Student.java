package com.sprint.common.converter.test.bean;

/**
 * @author hongfeng.li
 * @since 2022/4/28
 */
public class Student extends Man {

    private String level;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Student{" +
                "level='" + level + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
