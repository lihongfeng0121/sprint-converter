package com.sprint.common.converter;

import com.sprint.common.converter.exception.ConversionException;

/**
 * @author hongfeng.li
 * @version 1.0
 * @title ErrorHandler
 * @desc 异常处理类
 * @since 2021年02月05日
 */
@FunctionalInterface
public interface ErrorHandler<S, T> {

    T handle(Throwable ex, S source) throws ConversionException;
}
