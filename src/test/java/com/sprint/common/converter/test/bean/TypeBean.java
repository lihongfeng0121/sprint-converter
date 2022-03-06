package com.sprint.common.converter.test.bean;

import java.util.List;

/**
 * @author hongfeng.li
 * @since 2022/1/25
 */
public class TypeBean<T> {

    private String array;
    private T data;
    private List<T> list;
    private List<List<Inner>> listList;
    private String name;
    private Inner inner;
    private List<String> house;

    public String getArray() {
        return array;
    }

    public void setArray(String array) {
        this.array = array;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<List<Inner>> getListList() {
        return listList;
    }

    public void setListList(List<List<Inner>> listList) {
        this.listList = listList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Inner getInner() {
        return inner;
    }

    public void setInner(Inner inner) {
        this.inner = inner;
    }

    public List<String> getHouse() {
        return house;
    }

    public void setHouse(List<String> house) {
        this.house = house;
    }

    public static class Inner {
        private String inner;
        private Inner2 test;

        public String getInner() {
            return inner;
        }

        public void setInner(String inner) {
            this.inner = inner;
        }

        public Inner2 getTest() {
            return test;
        }

        public void setTest(Inner2 test) {
            this.test = test;
        }

        @Override
        public String toString() {
            return "Inner{" + "inner='" + inner + '\'' + ", test=" + test + '}';
        }
    }

    @Override
    public String toString() {
        return "TypeBean{" + "array='" + array + '\'' + ", data=" + data + ", list=" + list + ", listList=" + listList
                + ", name='" + name + '\'' + ", inner=" + inner + ", house=" + house + '}';
    }
}
