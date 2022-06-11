package com.sprint.common.converter.util;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.TypeReference;
import com.sprint.common.converter.conversion.nested.bean.Beans;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * BeanOptional
 *
 * @author hongfeng.li
 * @since 2021/3/17
 */
public class BeanOptional<T> {

    private static final BeanOptional<?> EMPTY = new BeanOptional<>();

    private final T value;

    private BeanOptional() {
        this.value = null;
    }

    private BeanOptional(T value) {
        this.value = Objects.requireNonNull(value);
        Assert.isTrue(Types.isBean(value.getClass()) || Types.isMap(value.getClass()), "value is not bean or map");
    }

    public static <T> BeanOptional<T> empty() {
        @SuppressWarnings("unchecked")
        BeanOptional<T> t = (BeanOptional<T>) EMPTY;
        return t;
    }


    /**
     * Returns an {@code BeanOptional} with the specified present non-null value.
     *
     * @param <T>   the class of the value
     * @param value the value to be present, which must be non-null
     * @return an {@code BeanOptional} with the value present
     * @throws NullPointerException if value is null
     */
    public static <T> BeanOptional<T> of(T value) {
        return new BeanOptional<>(value);
    }

    /**
     * Returns an {@code BeanOptional} describing the specified value, if non-null,
     * otherwise returns an empty {@code Optional}.
     *
     * @param <T>   the class of the value
     * @param value the possibly-null value to describe
     * @return an {@code BeanOptional} with a present value if the specified value
     * is non-null, otherwise an empty {@code Optional}
     */
    public static <T> BeanOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * 获取bean
     *
     * @return
     */
    public Object getNullable() {
        return value;
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
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
    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null)
            consumer.accept(value);
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code Optional} describing the value, otherwise return an
     * empty {@code Optional}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code Optional} describing the value of this {@code Optional}
     * if a value is present and the value matches the given predicate,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the predicate is null
     */
    public BeanOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }


    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code BeanOptional} describing the
     * result.  Otherwise return an empty {@code BeanOptional}.
     *
     * @param <U>    The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return an {@code BeanOptional} describing the result of applying a mapping
     * function to the value of this {@code BeanOptional}, if a value is present,
     * otherwise an empty {@code BeanOptional}
     * @throws NullPointerException if the mapping function is null
     * @apiNote This method supports post-processing on optional values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of file names, selects one that has
     * not yet been processed, and then opens that file, returning an
     * {@code Optional<FileInputStream>}:
     *
     * <pre>{@code
     *     Optional<FileInputStream> fis =
     *         names.stream().filter(name -> !isProcessedYet(name))
     *                       .findFirst()
     *                       .map(name -> new FileInputStream(name));
     * }</pre>
     * <p>
     * Here, {@code findFirst} returns an {@code BeanOptional<String>}, and then
     * {@code map} returns an {@code BeanOptional<FileInputStream>} for the desired
     * file if one exists.
     */
    public <U> BeanOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return BeanOptional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is present, apply the provided type cast to it,
     *
     * @param type target type
     * @param <U>  The type of the result of the mapping function
     * @return an {@code BeanOptional} describing the result of applying a mapping
     * function to the value of this {@code BeanOptional}, if a value is present,
     * otherwise an empty {@code BeanOptional}
     */
    public <U> BeanOptional<U> castMap(Type type, String... ignoreProperties) {
        return map(item -> Beans.cast(item, type, true, ignoreProperties));
    }

    /**
     * If a value is present, apply the provided type cast to it,
     *
     * @param type target type
     * @param <U>  The type of the result of the mapping function
     * @return an {@code BeanOptional} describing the result of applying a mapping
     * function to the value of this {@code BeanOptional}, if a value is present,
     * otherwise an empty {@code BeanOptional}
     */
    public <U> BeanOptional<U> castMap(Class<U> type, String... ignoreProperties) {
        return map(item -> Beans.cast(item, type, ignoreProperties));
    }

    /**
     * If a value is present, apply the provided type cast to it,
     *
     * @param type target type
     * @param <U>  The type of the result of the mapping function
     * @return an {@code BeanOptional} describing the result of applying a mapping
     * function to the value of this {@code BeanOptional}, if a value is present,
     * otherwise an empty {@code BeanOptional}
     */
    public <U> BeanOptional<U> castMap(TypeReference<U> type, String... ignoreProperties) {
        return map(item -> Beans.cast(item, type, true, ignoreProperties));
    }

    /**
     * If a value is present, apply the provided {@code BeanOptional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code BeanOptional}.  This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already an {@code BeanOptional},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code BeanOptional}.
     *
     * @param <U>    The type parameter to the {@code BeanOptional} returned by
     * @param mapper a mapping function to apply to the value, if present
     *               the mapping function
     * @return the result of applying an {@code BeanOptional}-bearing mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null or returns
     *                              a null result
     */
    public <U> BeanOptional<U> flatMap(Function<? super T, BeanOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
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
    public T orElse(T other) {
        return value != null ? value : other;
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
    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
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
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * toOptional
     *
     * @return Optional
     */
    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    /**
     * 获取bean 属性
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return Beans.getProperty(get(), key);
    }


    /**
     * 获取bean 属性
     *
     * @param key
     * @return
     */
    public Optional<Object> getOptional(String key) {
        return Optional.of(get()).map(item -> Beans.getProperty(value, key));
    }

    /**
     * 获取bean 属性
     *
     * @param key
     * @return
     */
    public Optional<Object> safeGetOptional(String key) {
        return Optional.ofNullable(getNullable()).map(item -> Beans.getProperty(value, key));
    }

    /**
     * 获取bean 属性
     *
     * @param key 属性
     * @return 属性值
     */
    public Object safeGet(String key) {
        return value == null ? null : Beans.getProperty(value, key);
    }

    private Object doGet(String key, Type type, Supplier<Object> defaultSupplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(defaultSupplier);
    }

    private Object doSafeGet(String key, Type type, Supplier<Object> defaultSupplier) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(defaultSupplier);
    }

    public <P> List<P> getList(String key, Class<P> clazz) {
        return Miscs.cast(doGet(key, ParameterizedTypeImpl.make(List.class, new Type[]{clazz}, null), () -> null));
    }

    public <P> List<P> safeGetList(String key, Class<P> clazz) {
        return Miscs.cast(doSafeGet(key, ParameterizedTypeImpl.make(List.class, new Type[]{clazz}, null), () -> null));
    }

    public <P> Set<P> getSet(String key, Class<P> clazz) {
        return Miscs.cast(doGet(key, ParameterizedTypeImpl.make(Set.class, new Type[]{clazz}, null), () -> null));
    }

    public <P> Set<P> safeGetSet(String key, Class<P> clazz) {
        return Miscs.cast(doSafeGet(key, ParameterizedTypeImpl.make(Set.class, new Type[]{clazz}, null), () -> null));
    }

    public <K, V> Map<K, V> getMap(String key, Class<K> keyClazz, Class<V> valClazz) {
        return Miscs.cast(doGet(key, ParameterizedTypeImpl.make(Map.class, new Type[]{keyClazz, valClazz}, null), () -> null));
    }

    public <K, V> Map<K, V> safeGetMap(String key, Class<K> keyClazz, Class<V> valClazz) {
        return Miscs.cast(doSafeGet(key, ParameterizedTypeImpl.make(Map.class, new Type[]{keyClazz, valClazz}, null), () -> null));
    }

    public <P> P get(String key, Class<P> clazz, Supplier<P> defaultSupplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, clazz)).orElseGet(defaultSupplier);
    }

    public <P> P safeGet(String key, Class<P> clazz, Supplier<P> defaultSupplier) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, clazz)).orElseGet(defaultSupplier);
    }

    public <P> P get(String key, Class<P> clazz) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, clazz)).orElse(null);
    }

    public <P> P safeGet(String key, Class<P> clazz) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, clazz)).orElse(null);
    }

    public <P> P get(String key, TypeReference<P> type, Supplier<P> defaultSupplier) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(defaultSupplier);
    }

    public <P> P safeGet(String key, TypeReference<P> type, Supplier<P> defaultSupplier) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, type)).orElseGet(defaultSupplier);
    }

    public <P> P get(String key, TypeReference<P> type) {
        return Optional.ofNullable(get(key)).map(val -> AnyConverter.convert(val, type)).orElse(null);
    }

    public <P> P safeGet(String key, TypeReference<P> type) {
        return Optional.ofNullable(safeGet(key)).map(val -> AnyConverter.convert(val, type)).orElse(null);
    }

    public String getString(String key) {
        return get(key, String.class);
    }

    public String getString(String key, String defaultValue) {
        return get(key, String.class, () -> defaultValue);
    }

    public int getIntValue(String key) {
        return get(key, Integer.TYPE, () -> 0);
    }

    public int getIntValue(String key, int defaultValue) {
        return get(key, Integer.TYPE, () -> defaultValue);
    }

    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    public long getLongValue(String key) {
        return get(key, Long.TYPE, () -> 0L);
    }

    public long getLongValue(String key, long defaultValue) {
        return get(key, Long.TYPE, () -> defaultValue);
    }

    public Long getLong(String key) {
        return get(key, Long.class);
    }

    public double getDoubleValue(String key) {
        return get(key, Double.TYPE, () -> 0D);
    }

    public double getDoubleValue(String key, double defaultValue) {
        return get(key, Double.TYPE, () -> defaultValue);
    }

    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    public Date getDate(String key) {
        return get(key, Date.class);
    }

    public Date getDate(String key, Date defaultVal) {
        return get(key, Date.class, () -> defaultVal);
    }

    public Timestamp getTimestamp(String key) {
        return get(key, Timestamp.class);
    }

    public Timestamp getTimestamp(String key, Timestamp defaultVal) {
        return get(key, Timestamp.class, () -> defaultVal);
    }

    public LocalDate getLocalDate(String key) {
        return get(key, LocalDate.class);
    }

    public LocalDate getLocalDate(String key, LocalDate defaultVal) {
        return get(key, LocalDate.class, () -> defaultVal);
    }


    public LocalDateTime getLocalDateTime(String key) {
        return get(key, LocalDateTime.class);
    }

    public LocalDateTime getLocalDateTime(String key, LocalDateTime defaultVal) {
        return get(key, LocalDateTime.class, () -> defaultVal);
    }

    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    public Boolean getBooleanValue(String key) {
        return get(key, Boolean.TYPE, () -> Boolean.FALSE);
    }

    public Boolean getBoolean(String key, Boolean defaultVal) {
        return get(key, Boolean.class, () -> defaultVal);
    }

    public String safeGetString(String key) {
        return safeGet(key, String.class);
    }

    public String safeGetString(String key, String defaultValue) {
        return safeGet(key, String.class, () -> defaultValue);
    }

    public int safeGetIntValue(String key) {
        return safeGet(key, Integer.TYPE, () -> 0);
    }

    public int safeGetIntValue(String key, int defaultValue) {
        return safeGet(key, Integer.TYPE, () -> defaultValue);
    }

    public Integer safeGetInteger(String key) {
        return safeGet(key, Integer.class);
    }

    public long safeGetLongValue(String key) {
        return safeGet(key, Long.TYPE, () -> 0L);
    }

    public long safeGetLongValue(String key, long defaultValue) {
        return safeGet(key, Long.TYPE, () -> defaultValue);
    }

    public Long safeGetLong(String key) {
        return safeGet(key, Long.class);
    }

    public double safeGetDoubleValue(String key) {
        return safeGet(key, Double.TYPE, () -> 0D);
    }

    public double safeGetDoubleValue(String key, double defaultValue) {
        return safeGet(key, Double.TYPE, () -> defaultValue);
    }

    public Double safeGetDouble(String key) {
        return safeGet(key, Double.class);
    }

    public Date safeGetDate(String key) {
        return safeGet(key, Date.class);
    }

    public Date safeGetDate(String key, Date defaultVal) {
        return safeGet(key, Date.class, () -> defaultVal);
    }

    public Timestamp safeGetTimestamp(String key) {
        return safeGet(key, Timestamp.class);
    }

    public Timestamp safeGetTimestamp(String key, Timestamp defaultVal) {
        return safeGet(key, Timestamp.class, () -> defaultVal);
    }

    public LocalDate safeGetLocalDate(String key) {
        return safeGet(key, LocalDate.class);
    }

    public LocalDate safeGetLocalDate(String key, LocalDate defaultVal) {
        return safeGet(key, LocalDate.class, () -> defaultVal);
    }


    public LocalDateTime safeGetLocalDateTime(String key) {
        return safeGet(key, LocalDateTime.class);
    }

    public LocalDateTime safeGetLocalDateTime(String key, LocalDateTime defaultVal) {
        return safeGet(key, LocalDateTime.class, () -> defaultVal);
    }

    public Boolean safeGetBoolean(String key) {
        return safeGet(key, Boolean.class);
    }

    public Boolean safeGetBooleanValue(String key) {
        return safeGet(key, Boolean.TYPE, () -> Boolean.FALSE);
    }

    public Boolean safeGetBoolean(String key, Boolean defaultVal) {
        return safeGet(key, Boolean.class, () -> defaultVal);
    }


    /**
     * Indicates whether some other object is "equal to" this BeanOptional. The
     * other object is considered equal if:
     * <ul>
     * <li>it is also an {@code BeanOptional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BeanOptional)) {
            return false;
        }

        BeanOptional<?> other = (BeanOptional<?>) obj;
        return Objects.equals(value, other.value);
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this BeanOptional suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @return the string representation of this instance
     * @implSpec If a value is present the result must include its string
     * representation in the result. Empty and present Optionals must be
     * unambiguously differentiable.
     */
    @Override
    public String toString() {
        return value != null
                ? String.format("BeanOptional[%s]", value)
                : "BeanOptional.empty";
    }
}
