package com.sprint.common.converter.test.bean;

import java.util.HashMap;

/**
 * 自定义map,map内 存在自定义属性
 * 
 * @author hongfeng.li
 * @since 2021/1/25
 */
public class CustomMap<K, V> extends HashMap<K, V> {

    private String name;
    private String address;
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "CustomMap{" + "name='" + name + '\'' + ", address='" + address + '\'' + ", age='" + age + '\'' + '}';
    }
}
