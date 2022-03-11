package com.sprint.common.converter.test;

import com.sprint.common.converter.AnyConverter;
import com.sprint.common.converter.Converter;
import com.sprint.common.converter.TypeReference;
import com.sprint.common.converter.conversion.nested.bean.Beans;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.test.bean.*;
import com.sprint.common.converter.util.Assert;
import com.sprint.common.converter.util.Types;
import org.junit.Test;

import java.beans.Transient;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author hongfeng.li
 * @since 2021/11/25
 */
public class TestConverter {

    @Test
    public void testCustomMapConvert() {
        CustomMap<String, Long> map2 = new CustomMap<>();
        map2.setAddress("beijing");
        map2.setAge("29");
        map2.setName("zhangsan");
        map2.put("hahaha", System.currentTimeMillis());
        CustomMap2<String, Date> map3 = AnyConverter.convert(map2, new TypeReference<CustomMap2<String, Date>>() {
        });
        Assert.isTrue(Objects.equals(map3.size(), map2.size()), "map 条数发生变化，failed!");
        Assert.isTrue(Objects.equals(map3.get("hahaha").getTime(), map2.get("hahaha")), "map value long->date，failed!");
        Map<String, Date> map4 = AnyConverter.convert(map2, new TypeReference<Map<String, Date>>() {
        });
        Assert.isTrue(Objects.equals(Beans.toMap(map4).size(), 4), "to map 条数错误，failed!");
        System.out.println("test1:CustomMapConvert success");
    }

    @Test
    public void testBeanConvert() {
        TypeBean<Object> bean = new TypeBean<>();
        bean.setName("zhangsan");
        System.out.println(bean);
        System.out.println(AnyConverter.convert(bean, TypeBean.class));

        System.out.println("----------------test beans---------------------");

        TypeBean<List<List<String>>> bean1 = new TypeBean<>();
        bean1.setData(Collections.singletonList(Collections.singletonList("1231314")));
        bean1.setName("zhangsan");
        bean1.setList(Collections.singletonList(Collections.singletonList(Collections.singletonList("1000"))));
        TypeBean.Inner inner1 = new TypeBean.Inner();
        inner1.setInner("100000");
        bean1.setListList(Collections.singletonList(Collections.singletonList(inner1)));
        bean1.setHouse(Collections.singletonList("yanchengyuan"));
        //bean1.setArray("[\"1231231\"]");

        TypeBean2<List<List<Integer>>> bean2 = AnyConverter.convert(bean1,
                new TypeReference<TypeBean2<List<List<Integer>>>>() {
                });

        long tms = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            TypeBean2<List<List<Integer>>> item = AnyConverter.convert(bean1,
                    new TypeReference<TypeBean2<List<List<Integer>>>>() {
                    });
        }

        System.out.println("test first bean convert total cost:" + (System.currentTimeMillis() - tms));

        tms = System.currentTimeMillis();
        TypeBean2<List<List<Integer>>> bean3 = AnyConverter.convert(bean2,
                new TypeReference<TypeBean2<List<List<Integer>>>>() {
                });
        System.out.println("test bean convert total cost:" + (System.currentTimeMillis() - tms));
        Object obj = Beans.getProperty(bean2, "list[0]");
        Object list = Beans.getProperty(bean2, "list");

        System.out.println(list + "------" + obj);

        System.out.println(bean1);
        System.out.println(bean2);

        Beans.setProperty(bean1, "inner.test.test", "2123131");

        System.out.println(bean1);

        System.out.println("test bean convert total cost:" + (System.currentTimeMillis() - tms));
    }

    @Test
    public void testBeanConvert1() {
        HashMap<String, Object> obj = new HashMap<>();
        Beans.setProperty(new TypeReference<HashMap<String, Object>>() {
        }.getType(), obj, "test.inner.test", "hahahah", true, false);
        System.out.println(obj);
        Map<String, Object> map = AnyConverter.convert(obj, new TypeReference<Map<String, Object>>() {
        });
        System.out.println(map);
    }

    @Test
    public void testBaseConvert() {
        long tms = System.currentTimeMillis();
        System.out.println(AnyConverter.convert(1, Boolean.class));
        System.out.println(AnyConverter.convert("TRUE", Boolean.class));
        System.out.println(AnyConverter.convert("是", Boolean.class));
        System.out.println(AnyConverter.convert("Y", Boolean.class));
        System.out.println(AnyConverter.convert("1", Integer.class));
        System.out.println(AnyConverter.convert("1", Integer.TYPE));
        System.out.println(AnyConverter.convert("1.10", Integer.class));
        System.out.println(AnyConverter.convert("1.10", Integer.TYPE));
        System.out.println(AnyConverter.convert("1.10", Double.class));
        System.out.println(AnyConverter.convert("1.10", Double.TYPE));
        System.out.println(AnyConverter.convert("1.10", Float.class));
        System.out.println(AnyConverter.convert("1.10", Float.TYPE));
        System.out.println(AnyConverter.convert("1.10", BigInteger.class));
        System.out.println(AnyConverter.convert("1.10", BigDecimal.class));
        System.out.println(AnyConverter.convert("2021-01-01 10:10:10", Date.class));
        System.out.println(AnyConverter.convert("2021-01-01 10:10:10", java.sql.Date.class));
        System.out.println(AnyConverter.convert("2021-01-01 10:10:10", Timestamp.class));
        System.out.println(AnyConverter.convert("2021-01-01 10:10:10", LocalDate.class));
        System.out.println(AnyConverter.convert("2021-01-01 10:10:10", LocalDateTime.class));
        System.out.println(
                AnyConverter.convert(AnyConverter.convert("2021-01-01 10:10:10", LocalDateTime.class), Long.class));
        System.out.println(AnyConverter.convert(Collections.singletonList("12313.4"), BigDecimal.class));
        System.out.println(AnyConverter.convert("12313.4", BigDecimal.class));
        System.out.println(AnyConverter.convert("12313.4", Double.class));
        System.out.println(AnyConverter.convert("12313.4", Integer.class));
        System.out.println(AnyConverter.convert("2021-11-12", LocalDate.class));
        System.out.println(AnyConverter.convert("2021-11-12", Date.class));
        System.out.println(AnyConverter.convert("2021-11-12", Year.class));

        System.out.println("test base convert total cost:" + (System.currentTimeMillis() - tms));
    }

    @Test
    public void testConstructor() {
        System.out.println(AnyConverter.convert(1, AtomicReference.class));
        System.out.println(AnyConverter.convert("ceshi", TestBean.class));

        TypeBean<List<TestBean<String>>> bean = new TypeBean<>();
        bean.setData(Collections.singletonList(new TestBean<>("12131231")));
        TypeBean<TestBean<Integer[]>> bean1 = AnyConverter.convert(bean,
                new TypeReference<TypeBean<TestBean<Integer[]>>>() {
                });
        System.out.println(bean1);
        TypeBean<List<TestBean<String>>> bean2 = AnyConverter.convert(bean1,
                new TypeReference<TypeBean<List<TestBean<String>>>>() {
                });
        System.out.println(bean2);

    }

    @Test
    public void testPathConverter() throws ConversionException {
        Converter<String, Timestamp> converter = AnyConverter.getConverter(String.class, Long.class, Timestamp.class);
        Converter<Long, Timestamp> converter2 = AnyConverter.getConverter(Long.class, String.class, Long.class,
                Timestamp.class);
        Long ts = System.currentTimeMillis();
        String tsStr = String.valueOf(ts);
        Timestamp timestamp = converter.convert(tsStr);
        Timestamp timestamp2 = converter2.convert(ts);
        Timestamp timestamp3 = AnyConverter.convert(tsStr, Timestamp.class);
        Timestamp timestamp4 = AnyConverter.convert(tsStr, Long.class, Timestamp.class);
        System.out.println(ts);
        System.out.println(timestamp);
        System.out.println(timestamp2);
        System.out.println(timestamp3);
        System.out.println(timestamp4);
        System.out.println(timestamp.getTime());
        System.out.println(timestamp2.getTime());
        System.out.println(timestamp3.getTime());
        System.out.println(timestamp4.getTime());
    }

    @Test
    public void test() {
        Converter<Number, String> nc = String::valueOf;
        Converter<String, String> nc2 = nc.compose((Converter<CharSequence, Number>) CharSequence::length);
        Converter<Number, BigDecimal> nc3 = nc.andThen(BigDecimal::new);
        Map<String, Object> res = AnyConverter.convert("{\"name\":\"zhangsan\"}", Map.class);
        System.out.println(res);
    }

    public static class MMap<V> extends HashMap<V, List<V>> {

    }

    @Test
    public void test2() {
        TestBean<Map<String, String>> bean = new TestBean<>();
        bean.setStr(Collections.singletonMap("12121", "23123145"));
        TestBean<MMap<String>> res = AnyConverter.convert(bean, new TypeReference<TestBean<MMap<String>>>() {
        });
        System.out.println(res);
    }

    @Test
    public void testMap() {
        Type type = new TypeReference<MMap<String>>(){}.getType();
        Type[] types = Types.getMapKVType(null, type);
        System.out.println(Arrays.toString(types));
    }

    @Test
    public void test2Object(){
        Object ob = AnyConverter.convert(Arrays.asList(Collections.singletonMap("a","10")), Object.class);
        System.out.println(ob);
    }
}
