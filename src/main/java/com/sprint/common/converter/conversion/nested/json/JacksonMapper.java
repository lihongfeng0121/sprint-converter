package com.sprint.common.converter.conversion.nested.json;

/**
 * @author hongfeng.li
 * @since 2023/7/27
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.sprint.common.converter.conversion.nested.json.databind.JacksonDateFormat;
import com.sprint.common.converter.util.Beans;
import com.sprint.common.converter.util.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.TimeZone;

/**
 * {@link ObjectMapper} 包装，<a href="https://github.com/FasterXML/jackson-databind/wiki/JacksonFeatures">Jackson Features
 * 参考</a>，内部改造如下
 * <ul>
 * <li>默认构造方法，进行了一些配置的默认设置，兼容早期的JsonMapper实现</li>
 * <li>关闭WRITE_NULL_MAP_VALUES特性，当Map中存在value等于null时不进行处理</li>
 * <li>关闭FAIL_ON_EMPTY_BEANS特征，当遇到一个对象无法进行序列化（没有对应类型的序列化器，以及没有任何注解用于识别如何序列化），则输出null值代替抛异常</li>
 * <li>关闭FAIL_ON_UNKNOWN_PROPERTIES特征，当解序列化时，如果遇到未知字段(JSON中的key在Object中不存在)，忽略代替抛异常</li>
 * <li>开启ALLOW_COMMENTS特征，解析JSON过程中，可以处理JSON中 // 和 /* 注释</li>
 * <li>开启ALLOW_YAML_COMMENTS特征，解析JSON过程中，支持按 # 开头的YARM注释风格</li>
 * <li>开启ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER特征，允许使用反斜杠机制进行转义</li>
 * <li>开启ALLOW_NON_NUMERIC_NUMBERS特征，允许识别NaN</li>
 * <li>开启ALLOW_NUMERIC_LEADING_ZEROS特征，允许JSON中整数以多个0开始</li>
 * <li>开启ALLOW_UNQUOTED_CONTROL_CHARS特征，允许字符串值包含非引号外的其他控制字符</li>
 * <li>开启ALLOW_UNQUOTED_FIELD_NAMES特征，允许使用非双引号属性名</li>
 * <li>开启ALLOW_SINGLE_QUOTES特征，允许单引号形式的属性名和字符串值</li>
 * </ul>
 *
 * @author Daniel Li
 * @since 04 May 2017
 */
public class JacksonMapper {

    private static final ClassLoader moduleClassLoader = JacksonConverter.class.getClassLoader();

    public static <T extends ObjectMapper> T configure(T current) {
        return configure(current, true, JacksonDateFormat.PATTERN_YYYYMMDDHHMMSSSSS);
    }

    public static <T extends ObjectMapper> T configure(T current, boolean timestampPriority, String formatPattern) {
        current.setTimeZone(TimeZone.getDefault());
        current.setDateFormat(new JacksonDateFormat(formatPattern));
        if (timestampPriority) {
            current.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        }
        current.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false) // 默认为true
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false) // 默认为true
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // 默认为true
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true) // 默认为false
                .configure(JsonParser.Feature.ALLOW_COMMENTS, true) // 默认为false
                .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true) // 默认为false
                .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true) // 默认为false
                .configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true) // 默认为false
                .configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true) // 默认为false
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true) // 默认为false
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true) // 默认为false
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) // 默认为false
                .configure(JsonParser.Feature.IGNORE_UNDEFINED, true) // 默认为false
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        registerWellKnownModulesIfAvailable(current);

        SimpleFilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false);
        current.setFilterProvider(filters);
        return current;
    }

    @SuppressWarnings("unchecked")
    private static void registerWellKnownModulesIfAvailable(ObjectMapper objectMapper) {
        // Java 7 java.nio.file.Path class present?
        if (Types.isPresent("java.nio.file.Path", moduleClassLoader)) {
            try {
                Class<? extends Module> jdk7Module = (Class<? extends Module>) Types
                        .forName("com.fasterxml.jackson.datatype.jdk7.Jdk7Module", moduleClassLoader);
                objectMapper.registerModule(Beans.instance(jdk7Module));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-jdk7 not available
            }
        }

        // Java 8 java.util.Optional class present?
        if (Types.isPresent("java.util.Optional", moduleClassLoader)) {
            try {
                Class<? extends Module> jdk8Module = (Class<? extends Module>) Types
                        .forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", moduleClassLoader);
                objectMapper.registerModule(Beans.instance(jdk8Module));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-jdk8 not available
            }
        }

        // Java 8 java.time package present?
        if (Types.isPresent("java.time.LocalDate", moduleClassLoader)) {
            try {
                Class<? extends Module> javaTimeModule = (Class<? extends Module>) Types
                        .forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", moduleClassLoader);
                objectMapper.registerModule(Beans.instance(javaTimeModule));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-jsr310 not available
            }
        }

        // Joda-Time present?
        if (Types.isPresent("org.joda.time.LocalDate", moduleClassLoader)) {
            try {
                Class<? extends Module> jodaModule = (Class<? extends Module>) Types
                        .forName("com.fasterxml.jackson.datatype.joda.JodaModule", moduleClassLoader);
                objectMapper.registerModule(Beans.instance(jodaModule));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-joda not available
            }
        }

        // Kotlin present?
        if (Types.isPresent("kotlin.Unit", moduleClassLoader)) {
            try {
                Class<? extends Module> kotlinModule = (Class<? extends Module>) Types
                        .forName("com.fasterxml.jackson.module.kotlin.KotlinModule", moduleClassLoader);
                objectMapper.registerModule(Beans.instance(kotlinModule));
            } catch (ClassNotFoundException ex) {
                // jackson-module-kotlin not available
            }
        }
    }

    public static List<Module> findModules() {
        return findModules(null);
    }

    public static List<Module> findModules(ClassLoader classLoader) {
        ArrayList<Module> modules = new ArrayList<>();
        ServiceLoader<Module> loader = (classLoader == null) ? ServiceLoader.load(Module.class)
                : ServiceLoader.load(Module.class, classLoader);
        for (Module module : loader) {
            modules.add(module);
        }
        return modules;
    }
}
