package com.sprint.common.converter.conversion.nested.bean.annotation;

import com.sprint.common.converter.conversion.nested.bean.Access;

import java.lang.annotation.*;

/**
 * Bean属性信息
 *
 * @author hongfeng.li
 * @version 1.0
 * @since 2021年02月05日
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertyInfo {

    String USE_DEFAULT_NAME = "";
    int INDEX_UNKNOWN = -1;

    String value() default USE_DEFAULT_NAME;

    int index() default INDEX_UNKNOWN;

    Access access() default Access.AUTO;
}
