package com.sprint.common.converter.util;

import com.sprint.common.converter.TypeReference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author hongfeng.li
 * @since 2023/2/2
 */
public final class ObjectValue extends AbstractValue {

    private static final ObjectValue EMPTY = new ObjectValue(null);

    private final Object value;

    public ObjectValue(Object value) {
        this.value = value instanceof ObjectValue ? ((ObjectValue) value).getValue() : value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public static ObjectValue empty() {
        return EMPTY;
    }

    /**
     * Returns an {@code ObjectValue} with the specified present non-null value.
     *
     * @param value the value to be present, which must be non-null
     * @return an {@code ObjectValue} with the value present
     * @throws NullPointerException if value is null
     */
    public static ObjectValue ofNonNull(Object value) {
        return new ObjectValue(Objects.requireNonNull(value));
    }

    /**
     * Returns an {@code ObjectValue} describing the specified value, if non-null,
     * otherwise returns an empty {@code ObjectValue}.
     *
     * @param value the possibly-null value to describe
     * @return an {@code ObjectValue} with a present value if the specified value
     * is non-null, otherwise an empty {@code ObjectValue}
     */
    public static ObjectValue ofNullable(Object value) {
        return value == null ? empty() : ofNonNull(value);
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    public void ifPresent(Consumer<Object> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param targetType the target type class.
     * @param <T>        type of the target type.
     * @param consumer   block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    public <T> void ifPresent(Class<T> targetType, Consumer<Object> consumer) {
        if (value != null) {
            consumer.accept(getValue(targetType));
        }
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param targetType the target type class.
     * @param <T>        type of the target type.
     * @param consumer   block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    public <T> void ifPresent(TypeReference<T> targetType, Consumer<Object> consumer) {
        if (value != null) {
            consumer.accept(getValue(targetType));
        }
    }


    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code ObjectValue} describing the value, otherwise return an
     * empty {@code ObjectValue}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code ObjectValue} describing the value of this {@code ObjectValue}
     * if a value is present and the value matches the given predicate,
     * otherwise an empty {@code ObjectValue}
     * @throws NullPointerException if the predicate is null
     */
    public ObjectValue filter(Predicate<Object> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }


    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code ObjectValue} describing the
     * result.  Otherwise return an empty {@code ObjectValue}.
     *
     * @param mapper a mapping function to apply to the value, if present
     * @return an {@code ObjectValue} describing the result of applying a mapping
     * function to the value of this {@code ObjectValue}, if a value is present,
     * otherwise an empty {@code ObjectValue}
     * @throws NullPointerException if the mapping function is null
     */
    public ObjectValue map(Function<Object, Object> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            return ObjectValue.ofNullable(mapper.apply(value));
        }
    }


    /**
     * If a value is present, apply the provided {@code ObjectValue}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code ObjectValue}.  This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already an {@code ObjectValue},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code ObjectValue}.
     *
     * @param mapper a mapping function to apply to the value, if present
     *               the mapping function
     * @return the result of applying an {@code ObjectValue}-bearing mapping
     * function to the value of this {@code ObjectValue}, if a value is present,
     * otherwise an empty {@code ObjectValue}
     * @throws NullPointerException if the mapping function is null or returns
     *                              a null result
     */
    public ObjectValue flatMap(Function<Object, ObjectValue> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     *              be null
     * @return the value, if present, otherwise {@code other}
     */
    public Object orElse(Object other) {
        return value != null ? value : other;
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param type  the target type class.
     * @param <T>   type of the target type.
     * @param other the value to be returned if there is no value present, may
     *              be null
     * @return the value, if present, otherwise {@code other}
     */
    public <T> T orElse(Class<T> type, T other) {
        return value != null ? getValue(type) : other;
    }


    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param type  the target type class.
     * @param <T>   type of the target type.
     * @param other the value to be returned if there is no value present, may
     *              be null
     * @return the value, if present, otherwise {@code other}
     */
    public <T> T orElse(TypeReference<T> type, T other) {
        return value != null ? getValue(type) : other;
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value
     *              is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     *                              null
     */
    public Object orElseGet(Supplier<Object> other) {
        return value != null ? value : other.get();
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param type  the target type class.
     * @param <T>   type of the target type.
     * @param other a {@code Supplier} whose result is returned if no value
     *              is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     *                              null
     */
    public <T> T orElseGet(Class<T> type, Supplier<T> other) {
        return value != null ? getValue(type) : other.get();
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param type  the target type class.
     * @param <T>   type of the target type.
     * @param other a {@code Supplier} whose result is returned if no value
     *              is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     *                              null
     */
    public <T> T orElseGet(TypeReference<T> type, Supplier<T> other) {
        return value != null ? getValue(type) : other.get();
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     *                          be thrown
     * @return the present value
     * @throws X                    if there is no value present
     * @throws NullPointerException if no value is present and
     *                              {@code exceptionSupplier} is null
     */
    public <X extends Throwable> Object orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @param type              the target type class.
     * @param <T>               type of the target type.
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     *                          be thrown
     * @return the present value
     * @throws X                    if there is no value present
     * @throws NullPointerException if no value is present and
     *                              {@code exceptionSupplier} is null
     */
    public <X extends Throwable, T> T orElseThrow(Class<T> type, Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return getValue(type);
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @param type              the target type class.
     * @param <T>               type of the target type.
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     *                          be thrown
     * @return the present value
     * @throws X                    if there is no value present
     * @throws NullPointerException if no value is present and
     *                              {@code exceptionSupplier} is null
     */
    public <X extends Throwable, T> T orElseThrow(TypeReference<T> type, Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return getValue(type);
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * to optional
     *
     * @param targetType type of the target type.
     * @param <T>        type of the target type.
     * @return the optional of target type.
     */
    public <T> Optional<T> toOptional(Class<T> targetType) {
        return Optional.ofNullable(getValue(targetType));
    }

    /**
     * to bean optional
     *
     * @param targetType type of the target type.
     * @param <T>        type of the target type.
     * @return the optional of target type.
     */
    public <T> BeanOptional<T> toBeanOptional(Class<T> targetType) {
        return BeanOptional.ofNullable(getValue(targetType));
    }
}
