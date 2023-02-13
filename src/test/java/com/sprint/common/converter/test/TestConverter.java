package com.sprint.common.converter.test;

import com.sprint.common.converter.*;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.JsonException;
import com.sprint.common.converter.test.bean.*;
import com.sprint.common.converter.util.*;
import org.junit.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * @author hongfeng.li
 * @since 2021/11/25
 */
public class TestConverter {


    public static class ClazzTypeImpl extends ClazzType<Student, Student2> {
    }

    public static class ClazzTypeImpl2 extends ClazzTypeImpl {
    }

    @Test
    public void testTypes() {
        System.out.println(Arrays.toString(Types.getClassSuperclassType(ClazzTypeImpl.class)));
        System.out.println(Arrays.toString(Types.getClassSuperclassType(ClazzTypeImpl2.class)));
        System.out.println(Arrays.toString(GenericsResolver.of(ClazzType.class).resolve(ClazzTypeImpl.class)));
        System.out.println(Arrays.toString(GenericsResolver.of(ClazzType.class).resolve(ClazzTypeImpl2.class)));
        System.out.println(Arrays.toString(Types.getClassSuperclassType(MMap.class)));
        System.out.println(Arrays.toString(GenericsResolver.of(Map.class).resolve(new TypeReference<MMap<String>>() {
        })));
    }

    /**
     * 测试范型解析
     */
    @Test
    public void testGenericsResolver() {
        Type type = new TypeReference<MMap<String>>() {
        }.getType();
        Type[] types = Types.getMapKVType(null, type);
        System.out.println(Arrays.toString(types));
        GenericsResolver resolver = GenericsResolver.of(Map.class);
        System.out.println(Arrays.toString(resolver.resolve(type)));
    }

    @Test
    public void testReferCustomMapConvert() {
        TestBean<Map<String, String>> bean = new TestBean<>();
        bean.setObj(Collections.singletonMap("12121", "23123145"));
        TestBean<MMap<String>> res = AnyConverter.convert(bean, new TypeReference<TestBean<MMap<String>>>() {
        });
        System.out.println(res);
    }

    /**
     * 测试自定义Map转换
     */
    @Test
    public void testCustomMapConvert() {
        CustomMap<String, Long> customMap = new CustomMap<>();
        customMap.setAddress("beijing");
        customMap.setAge("29");
        customMap.setName("zhangsan");
        customMap.put("hahaha", System.currentTimeMillis());

        {//测试自定义map转自定义map
            CustomMap2<String, Date> map = AnyConverter.convert(customMap, new TypeReference<CustomMap2<String, Date>>() {
            });
            Assert.isTrue(Objects.equals(map.size(), customMap.size()), "map 条数发生变化，failed!");
            Assert.isTrue(Objects.equals(map.get("hahaha").getTime(), customMap.get("hahaha")), "map value long->date，failed!");
        }

        {//测试自定义map转Map
            Map<String, Date> map4 = AnyConverter.convert(customMap, new TypeReference<Map<String, Date>>() {
            });

            Assert.isTrue(Objects.equals(Beans.toMap(map4).size(), 4), "to map 条数错误，failed!");
        }
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

        System.out.println("test first 100000 bean convert total cost:" + (System.currentTimeMillis() - tms));

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

        System.out.println("----------------test json to bean---------------------");

        Map<String, Object> res = AnyConverter.convert("{\"name\":\"zhangsan\"}", TypeReference.STR_OBJ_MAP);
        System.out.println(res);
    }

    @Test
    public void testSetBeanProperty() {
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
        System.out.println(AnyConverter.convert(1L, Boolean.class));
        System.out.println(AnyConverter.convert(1D, Boolean.class));
        System.out.println(AnyConverter.convert(1F, Boolean.class));
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

        System.out.println("test base convert success, total cost:" + (System.currentTimeMillis() - tms));
    }

    @Test
    public void testConstructor() throws JsonException {
        //测试构造函数转换
        System.out.println(AnyConverter.convert(1, AtomicReference.class));
        //测试构造函数转换
        System.out.println(AnyConverter.convert("ceshi", TestBean.class));

        TypeBean<List<TestBean<String>>> bean = new TypeBean<>();
        bean.setData(Collections.singletonList(new TestBean<>("12131231")));

        System.out.println("bean ->" + Jsons.toJsonString(bean));

        TypeBean<TestBean<Integer[]>> bean1 = AnyConverter.convert(bean,
                new TypeReference<TypeBean<TestBean<Integer[]>>>() {
                });
        System.out.println("bean1 ->" + Jsons.toJsonString(bean1));
        TypeBean<List<TestBean<String>>> bean2 = AnyConverter.convert(bean,
                new TypeReference<TypeBean<List<TestBean<String>>>>() {
                });
        System.out.println("bean2 ->" + Jsons.toJsonString(bean2));
    }

    @Test
    public void testPathConverter() throws ConversionException {
        Object ob = AnyConverter.convert(Collections.singletonMap("local", AnyConverter.convert("2021-01-01 10:10:10", LocalDateTime.class, Long.class)), String.class);
        System.out.println(ob);

        Converter<String, Timestamp> converter = AnyConverter.converter(String.class, Long.class, Timestamp.class);
        Converter<Long, Timestamp> converter2 = AnyConverter.converter(Long.class, String.class, Long.class,
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
    public void testConverterFunction() throws ConversionException {
        Converter<Number, String> nc = String::valueOf;
        Converter<String, String> nc2 = nc.compose((Converter<CharSequence, Number>) CharSequence::length);
        Converter<Number, BigDecimal> nc3 = nc.andThen(BigDecimal::new);
        System.out.println(nc3.convert(10));
        System.out.println(nc2.convert("我是五个字"));
        double ss = Stream.of("2", "12.6").map(Objects.requireNonNull(BaseConverter.converter(String.class, Double.TYPE)).asfunc()).reduce(Double::sum).get();
        System.out.println(ss);
    }

    @Test
    public void testString() throws ConversionException {
        String aa = "zhangsan";
        long ts = System.currentTimeMillis();
        System.out.println(Arrays.toString(AnyConverter.convert(aa, byte[].class)));
        System.out.println(System.currentTimeMillis() - ts);
        System.out.println(Arrays.toString(AnyConverter.convert(aa, char[].class)));
        System.out.println(System.currentTimeMillis() - ts);
        byte[] bytes = BaseConverter.convert(aa, byte[].class);
        System.out.println(Arrays.toString(bytes));
        System.out.println(System.currentTimeMillis() - ts);
        System.out.println(BaseConverter.convert(bytes, String.class));
        System.out.println(System.currentTimeMillis() - ts);
        //System.out.println(Arrays.toString(AnyConverter.convert(bytes, char[].class)));
    }

    @Test
    public void testSimpleBean2Bean() throws JsonException {
        Student student = new Student();
        student.setName("zhangsan");
        student.setLevel("一年级");
        Student2 student2 = BeanConverter.convert(student, Student2.class);
        System.out.println("student1 ->" + Jsons.toJsonString(student));
        System.out.println("student2 ->" + Jsons.toJsonString(student2));
    }

    @Test
    public void testJson() {
        JsonObject parse = JsonObject.parse("{\"name\":\"2022-12-01\"}");
        ObjectValue name = parse.getObjectValue("name");
        System.out.println(parse);
        System.out.println(name);
        System.out.println(name.getLocalDateTime().toString());

        JsonArray values = AnyConverter.convert(parse, JsonArray.class);
        System.out.println(values.toString());
    }

    @Test
    public void testObjectValue() {
        JsonObject parse = JsonObject.parse("{\"name\":\"2022-12-01\"}");
        ObjectValue name = parse.getObjectValue("name");
        System.out.println(parse);
        System.out.println(name);
        System.out.println(AnyConverter.convert(name, Date.class));

        JsonArray values = AnyConverter.convert(parse, JsonArray.class);
        System.out.println(values.toString());
    }
}
