package com.sprint.common.converter.test;

import com.sprint.common.converter.*;
import com.sprint.common.converter.conversion.nested.bean.introspection.PropertyAccess;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.exception.JsonException;
import com.sprint.common.converter.test.bean.*;
import com.sprint.common.converter.util.Properties;
import com.sprint.common.converter.util.*;
import org.junit.Test;

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
public class TestConverter extends BaseTest {


    public static class ClazzTypeImpl extends ClazzType<Student, Student2> {
    }

    public static class ClazzTypeImpl2 extends ClazzTypeImpl {
    }

    /**
     * 测试范型解析
     */
    @Test
    public void testTypeUtil() {
        test("测试范型解析", "Types#getClassSuperclassType", () -> Arrays.toString(Types.getClassSuperclassType(ClazzTypeImpl.class)));
        test("测试范型解析", "Types#getClassSuperclassType", () -> Arrays.toString(Types.getClassSuperclassType(ClazzTypeImpl2.class)));
        test("测试范型解析", "GenericsResolver#resolve", () -> Arrays.toString(GenericsResolver.of(ClazzType.class).resolve(ClazzTypeImpl.class)));
        test("测试范型解析", "GenericsResolver#resolve", () -> Arrays.toString(GenericsResolver.of(ClazzType.class).resolve(ClazzTypeImpl2.class)));
        test("测试范型解析", "Types.getClassSuperclassType", () -> Arrays.toString(Types.getClassSuperclassType(MMap.class)));
        test("测试范型解析", "GenericsResolver#resolve", () -> Arrays.toString(GenericsResolver.of(Map.class).resolve(new TypeReference<MMap<String>>() {
        })));
    }

    @Test
    public void testReferCustomMapConvert() {
        TestBean<Map<String, String>> bean = new TestBean<>();
        bean.setObj(Collections.singletonMap("12121", "23123145"));
        test("自定义MAP转换", "AnyConverter#convert", () -> AnyConverter.convert(bean, new TypeReference<TestBean<MMap<String>>>() {
        }));
        Map<String, String> map = new HashMap<>();
        map.put("name", "zhangsan");

        test("自定义MAP转换", "AnyConverter#convert", () -> AnyConverter.convert(map, Student.class));

        CustomMap<String, Long> customMap = new CustomMap<>();
        customMap.setAddress("beijing");
        customMap.setAge("29");
        customMap.setName("zhangsan");
        customMap.put("hahaha", System.currentTimeMillis());

        test("自定义MAP转换", "测试自定义map转自定义map", () -> {
            CustomMap2<String, Date> customMap2 = AnyConverter.convert(customMap, new TypeReference<CustomMap2<String, Date>>() {
            });
            Assert.isTrue(Objects.equals(customMap2.size(), customMap.size()), "map 条数发生变化，failed!");
            Assert.isTrue(Objects.equals(customMap2.get("hahaha").getTime(), customMap.get("hahaha")), "map value long->date，failed!");
            return customMap2;
        });

        test("自定义MAP转换", "测试自定义map转Map", () -> {
            Map<String, Date> map4 = AnyConverter.convert(customMap, new TypeReference<Map<String, Date>>() {
            });
            Assert.isTrue(Objects.equals(Beans.toMap(map4).size(), 4), "to map 条数错误，failed!");
            return map4;
        });
    }


    @Test
    public void testBeanConvert() {
        TypeBean<Object> bean = new TypeBean<>();
        bean.setName("zhangsan");
        bean.setArray("zhegshiyige");
        test("自定义MAP转换", "测试自定义map转Map", () -> bean);

        test("Bean转换", "测试自定义map转Map", () -> AnyConverter.convert(bean, TypeBean.class));
        test("Bean转换", "测试自定义map转Map", () -> AnyConverter.convert(bean, TypeBean2.class));

        TypeBean<List<List<String>>> bean1 = new TypeBean<>();
        bean1.setData(Collections.singletonList(Collections.singletonList("1231314")));
        bean1.setName("zhangsan");
        bean1.setList(Collections.singletonList(Collections.singletonList(Collections.singletonList("1000"))));

        TypeBean.Inner inner1 = new TypeBean.Inner();
        inner1.setInner("100000");
        bean1.setListList(Collections.singletonList(Collections.singletonList(inner1)));
        bean1.setHouse(Collections.singletonList("yanchengyuan"));
        bean1.setArray("[\"1231231\"]");
        TypeBean2<List<List<Integer>>> bean2 = AnyConverter.convert(bean1,
                new TypeReference<TypeBean2<List<List<Integer>>>>() {
                });

        test("Bean转换", "测试转换1性能", () -> {
            AnyConverter.convert(bean1,
                    new TypeReference<TypeBean2<List<List<Integer>>>>() {
                    });
            return "";
        });

        test("Bean转换", "测试转换100000性能", () -> {
            for (int i = 0; i < 100000; i++) {
                TypeBean2<List<List<Integer>>> item = AnyConverter.convert(bean1,
                        new TypeReference<TypeBean2<List<List<Integer>>>>() {
                        });
            }
            return "";
        });
        test("Bean转换", "TypeBean2<List<List<Integer>>> -> TypeBean2<List<List<Integer>>>", () -> AnyConverter.convert(bean2, new TypeReference<TypeBean2<List<List<Integer>>>>() {
        }));

        test("Bean转换", "Beans.getProperty list", () -> Beans.getProperty(bean2, "list"));
        test("Bean转换", "Beans.getProperty list[0]", () -> Beans.getProperty(bean2, "list[0]"));


        test("Bean转换", "Beans多级set/get", () -> {
            String value = "2123131";
            Beans.setProperty(bean1, "inner.test.test", "2123131");
            Object property = Beans.getProperty(bean1, "inner.test.test");
            Assert.isTrue(Objects.equals(value, property), "属性不一致");
            return bean1;
        });

        test("Bean转换", "AnyConverter Str -> Map", () -> AnyConverter.convert("{\"name\":\"zhangsan\"}", TypeReference.STR_OBJ_MAP));


        TypeBean<List<TestBean<String>>> singletonList = new TypeBean<>();
        singletonList.setData(Collections.singletonList(new TestBean<>("12131231")));

        test("Bean转换", "AnyConverter singletonList -> singletonArray", () -> AnyConverter.convert(singletonList,
                new TypeReference<TypeBean<TestBean<Integer[]>>>() {
                }));

        test("Bean转换", "AnyConverter singletonList -> singleton", () -> AnyConverter.convert(bean,
                new TypeReference<TypeBean<List<TestBean<String>>>>() {
                }));
    }

    @Test
    public void testSetBeanProperty() {
        test("Bean转换(MAP)", "Beans多级set/get", () -> {
            HashMap<String, Object> obj = new HashMap<>();
            Beans.setProperty(new TypeReference<HashMap<String, Object>>() {
            }.getType(), obj, "test.inner.test", "hahahah", true, false);
            Map<String, Object> map = AnyConverter.convert(obj, new TypeReference<Map<String, Object>>() {
            });

            Object property = Beans.getProperty(map, "test.inner.test");
            Assert.isTrue(Objects.equals("hahahah", property), "属性不一致");

            return obj;
        });

    }

    @Test
    public void testBaseConvert() {
        test("Base转换", "AnyConverter null -> boolean", () -> AnyConverter.convert(null, boolean.class));
        test("Base转换", "AnyConverter null -> int", () -> AnyConverter.convert(null, int.class));
        test("Base转换", "AnyConverter null -> double", () -> AnyConverter.convert(null, double.class));
        test("Base转换", "AnyConverter int -> boolean", () -> AnyConverter.convert(1, Boolean.class));
        test("Base转换", "AnyConverter long -> boolean", () -> AnyConverter.convert(1L, Boolean.class));
        test("Base转换", "AnyConverter double -> boolean", () -> AnyConverter.convert(1D, Boolean.class));
        test("Base转换", "AnyConverter flot -> boolean", () -> AnyConverter.convert(1F, Boolean.class));
        test("Base转换", "AnyConverter String -> boolean", () -> AnyConverter.convert("TRUE", Boolean.class));
        test("Base转换", "AnyConverter String -> boolean", () -> AnyConverter.convert("是", Boolean.class));
        test("Base转换", "AnyConverter String -> boolean", () -> AnyConverter.convert("Y", Boolean.class));
        test("Base转换", "AnyConverter String -> Integer", () -> AnyConverter.convert("1", Integer.class));
        test("Base转换", "AnyConverter String -> int", () -> AnyConverter.convert("1", Integer.TYPE));
        test("Base转换", "AnyConverter String -> Integer", () -> AnyConverter.convert("1.10", Integer.class));
        test("Base转换", "AnyConverter String -> int", () -> AnyConverter.convert("1.10", Integer.TYPE));
        test("Base转换", "AnyConverter String -> Double", () -> AnyConverter.convert("1.10", Double.class));
        test("Base转换", "AnyConverter String -> double", () -> AnyConverter.convert("1.10", Double.TYPE));
        test("Base转换", "AnyConverter String -> Float", () -> AnyConverter.convert("1.10", Float.class));
        test("Base转换", "AnyConverter String -> float", () -> AnyConverter.convert("1.10", Float.TYPE));
        test("Base转换", "AnyConverter String -> BigInteger", () -> AnyConverter.convert("1.10", BigInteger.class));
        test("Base转换", "AnyConverter String -> BigDecimal", () -> AnyConverter.convert("1.10", BigDecimal.class));
        test("Base转换", "AnyConverter String -> Date", () -> AnyConverter.convert("2021-01-01 10:10:10", Date.class));
        test("Base转换", "AnyConverter String -> java.sql.Date", () -> AnyConverter.convert("2021-01-01 10:10:10", java.sql.Date.class));
        test("Base转换", "AnyConverter String -> Timestamp", () -> AnyConverter.convert("2021-01-01 10:10:10", Timestamp.class));
        test("Base转换", "AnyConverter String -> LocalDate", () -> AnyConverter.convert("2021-01-01 10:10:10", LocalDate.class));
        test("Base转换", "AnyConverter String -> LocalDateTime", () -> AnyConverter.convert("2021-01-01 10:10:10", LocalDateTime.class));
        test("Base转换", "AnyConverter String -> LocalDateTime", () -> AnyConverter.convert(AnyConverter.convert("2021-01-01 10:10:10", LocalDateTime.class), Long.class));
        test("Base转换", "AnyConverter List<String> -> BigDecimal", () -> AnyConverter.convert(Collections.singletonList("12313.4"), BigDecimal.class));
        test("Base转换", "AnyConverter String -> BigDecimal", () -> AnyConverter.convert("12313.4", BigDecimal.class));
        test("Base转换", "AnyConverter String -> Double", () -> AnyConverter.convert("12313.4", Double.class));
        test("Base转换", "AnyConverter String -> Integer", () -> AnyConverter.convert("12313.4", Integer.class));
        test("Base转换", "AnyConverter String -> LocalDate", () -> AnyConverter.convert("2021-11-12", LocalDate.class));
        test("Base转换", "AnyConverter String -> Date", () -> AnyConverter.convert("2021-11-12", Date.class));
        test("Base转换", "AnyConverter String -> Year", () -> AnyConverter.convert("2021-11-12", Year.class));
    }

    @Test
    public void testConstructor() throws JsonException {
        test("测试构造函数转换", "AnyConverter int -> AtomicReference", () -> AnyConverter.convert(1, AtomicReference.class));
        test("测试构造函数转换", "AnyConverter String -> TestBean", () -> AnyConverter.convert("ceshi", TestBean.class));
    }

    @Test
    public void testPathConverter() throws ConversionException {
        test("测试多级路径转换", "testPathConverter", () -> AnyConverter.convert(Collections.singletonMap("local", AnyConverter.convert("2021-01-01 10:10:10", LocalDateTime.class, Long.class)), String.class));
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
        test("测试String转数组", "testString", () -> {
            Byte[] bytes = AnyConverter.convert(aa, Byte[].class);
            String convert = AnyConverter.convert(bytes, String.class);
            Character[] chars = AnyConverter.convert(aa, Character[].class);
            String convert2 = AnyConverter.convert(chars, String.class);
            Assert.isTrue(Objects.equals(convert, convert2), "转换异常");
            return true;
        });
    }

    @Test
    public void testSimpleBean2Bean() throws JsonException {
        Student student = new Student();
        student.setName("zhangsan");
        student.setLevel("一年级");

        test("简单类型的Bean转换", "bean1->bean2", () -> {
            Student2 student2 = BeanConverter.convert(student, Student2.class);
            Assert.isTrue(Objects.equals(Jsons.toJsonString(student), Jsons.toJsonString(student2)), "转换异常");
            return true;
        });
    }

    @Test
    public void testJson() {
        test("JSON转换", "str->json", () -> {
            JsonObject parse1 = AnyConverter.convert("{\"name\":\"2022-12-01\"}", JsonObject.class);
            JsonObject parse2 = JsonObject.parse("{\"name\":\"2022-12-01\"}");
            Assert.equal(parse1, parse2, "转换异常");
            ObjectValue name1 = parse1.getObjectValue("name");
            ObjectValue name2 = parse2.getObjectValue("name");
            Assert.equal(Objects.requireNonNull(name1), Objects.requireNonNull(name2), "转换异常");
            JsonArray values = AnyConverter.convert(parse2, JsonArray.class);
            Assert.isTrue(values.size() == 1, "转换异常");
            return true;
        });
    }

    @Test
    public void testObjectValue() {
        JsonObject parse = JsonObject.parse("{\"name\":\"2022-12-01\"}");
        ObjectValue name = parse.getObjectValue("name");
        System.out.println(parse);
        System.out.println(name);
        System.out.println(AnyConverter.convert(name, Date.class));

        Optional<JsonArray> values = AnyConverter.convert(parse, new TypeReference<Optional<JsonArray>>() {
        });
        System.out.println(values.toString());
        Optional<ObjectValue> convert = AnyConverter.convert(values, new TypeReference<Optional<ObjectValue>>() {
        });
        System.out.println(convert);

        ObjectValue objectValue = AnyConverter.convert(name, ObjectValue.class);
        System.out.println(objectValue);
    }

    @Test
    public void testByteConverter() {
        char[] convert = AnyConverter.convert(new Character[]{'1', '2'}, new TypeReference<char[]>() {
        });

    }

    @Test
    public void testInterface() {
        List<PropertyAccess> readPropertyAccess = Properties.getReadPropertyAccessFromClass(People.class);
        System.out.println(readPropertyAccess);
        TypeDescriptor of = TypeDescriptor.of(People.class);
        System.out.println(of.isInterface());
        System.out.println(of.isBean());
        Man man = new Man();
        man.setName("lihongfeng");
        People convert = AnyConverter.convert(man, People.class);
        People convert2 = AnyConverter.convert(man, People.class);
        System.out.println(convert.getAge());
        System.out.println(convert.getName());
        System.out.println(convert);
        System.out.println(Jsons.toJsonString(convert));
        System.out.println(convert.equals(convert2));
        convert.setName("zhangsan");
        System.out.println(Jsons.toJsonString(convert));
    }

    @Test
    public void test() {
        AnyConverter.registerConverter(Integer.class, Timestamp.class, new Converter<Integer, Timestamp>() {
            @Override
            public Timestamp convert(Integer source) throws ConversionException {
                return new Timestamp(source.longValue());
            }
        });

        Timestamp convert = AnyConverter.convert(11201185, Timestamp.class);
        System.out.println(convert);
    }
}
