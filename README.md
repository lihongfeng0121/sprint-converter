# AnyConverter

## Project Activity

This project is based on Java reflection, which is less efficient than hard coding. This project is suitable for
flexible property resolution, such as Map transformation, Bean/Map property acquisition, form transformation Bean, form
property acquisition, etc.

This tool is not recommended if beans are rolled over each other. Recommended use:

- [mapstruct](https://github.com/mapstruct/mapstruct)

## Why AnyConverter?

A mapping framework is useful in a layered architecture where you are creating layers of abstraction by encapsulating
changes to particular data objects vs. propagating these objects to other layers (i.e. external service data objects,
domain objects, data transfer objects, internal service data objects).

1.Mapping between data objects has traditionally been addressed by hand coding value object assemblers (or converters)
that copy data between the objects. Most programmers will develop some sort of custom mapping framework and spend
countless hours and thousands of lines of code mapping to and from their different data object.

This type of code for such conversions is rather boring to write, so why not do it automatically?

## What is AnyConverter?

AnyConverter is a Java Bean/Type/Map/Json to Java Bean/Type/Map/Json converter. It supports the recursive transformation
of a Bean/Map into another Bean and is a robust, versatile, flexible, reusable, and configurable open source mapping
framework.

You can also use it in Map Reduce for various types of conversion to speed up coding.

## Getting the Distribution

If you are using Maven, simply copy-paste this dependency to your project.

```XML

<dependency>
    <groupId>io.github.lihongfeng0121</groupId>
    <artifactId>sprint-converter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Simple Example

```Java
        AnyConverter.convert(1, Boolean.class);
        AnyConverter.convert("TRUE", Boolean.class);
        AnyConverter.convert("是", Boolean.class);
        AnyConverter.convert("Y", Boolean.class);
        AnyConverter.convert("1", Integer.class);
        AnyConverter.convert("1", Integer.TYPE);
        AnyConverter.convert(Collections.singletonList("12313.4"), BigDecimal.class);
        AnyConverter.convert("12313.4", BigDecimal.class);
        AnyConverter.convert("12313.4", Double.class);
        AnyConverter.convert("12313.4", Integer.class);

        TypeBean<Object> bean1 = new TypeBean<>();
        bean.setName("zhangsan");
        System.out.println(AnyConverter.convert(bean1, TypeBean2.class));

        TypeBean2<List<List<Integer>>> bean2 = AnyConverter.convert(bean1,
        new TypeReference<TypeBean2<List<List<Integer>>>>() {
        });

        double ss = Stream.of("2", "12.6").map(AnyConverter.converter(String.class, Double.TYPE).asfunc()).reduce(Double::sum).get();
```

© 2022 GitHub, Inc. Terms Privacy Security Status Docs Contact GitHub Pricing API Training Blog About
