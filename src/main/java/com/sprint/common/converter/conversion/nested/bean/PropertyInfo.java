package com.sprint.common.converter.conversion.nested.bean;

import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;

import java.util.Objects;

/**
 * 属性信息
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
public class PropertyInfo {

    /**
     * 属性信息
     */
    private PropertyAccess propertyAccess;
    /**
     * 属性名字
     */
    private String name;

    /**
     * 数组（集合）位置
     */
    private int index;

    /**
     * 权限
     */
    private Access access;

    public PropertyAccess getPropertyAccess() {
        return propertyAccess;
    }

    public void setPropertyAccess(PropertyAccess propertyAccess) {
        this.propertyAccess = propertyAccess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PropertyInfo that = (PropertyInfo) o;
        return index == that.index && Objects.equals(name, that.name) && access == that.access;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, index, access);
    }

    @Override
    public String toString() {
        return "PropertyInfo{" + "name='" + name + '\'' + ", index=" + index + ", access=" + access + '}';
    }
}
