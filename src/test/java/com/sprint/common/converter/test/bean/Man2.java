package com.sprint.common.converter.test.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hongfeng.li
 * @since 2023/4/23
 */
public class Man2 {

    @JsonProperty("name")
    protected String name2;

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    @Override
    public String toString() {
        return "Man2{" +
                "name2='" + name2 + '\'' +
                '}';
    }
}
