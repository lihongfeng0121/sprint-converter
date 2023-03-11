package com.sprint.common.converter.test;

import org.junit.After;
import org.junit.Before;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hongfeng.li
 * @since 2023/3/10
 */
public class BaseTest {


    @FunctionalInterface
    public interface Action {
        Object call() throws Throwable;
    }

    @Before
    public void before() {
    }

    @After
    public void after() {
        AtomicInteger successCount = SUCCESS_COUNT_MAP.getOrDefault(current, new AtomicInteger(0));
        AtomicInteger failedCount = FAILED_COUNT_MAP.getOrDefault(current, new AtomicInteger(0));
        System.out.printf("<" + current + ">result success count %s, failed count %s.<" + current + "/>%n", successCount, failedCount);
    }

    private static final Map<String, AtomicInteger> SUCCESS_COUNT_MAP = new LinkedHashMap<>();
    private static final Map<String, AtomicInteger> FAILED_COUNT_MAP = new LinkedHashMap<>();

    private static String current = "";

    protected static void test(String group, String name, Action action) {
        current = group;
        System.out.printf("<" + group + ">%n    <func>%s</func>%n", name);
        boolean success = false;
        String msg = "success";
        long costTs = 0;
        long ts = System.currentTimeMillis();
        try {
            Object exe = action.call();
            System.out.printf("    <result>%s</result>%n", exe);
            success = true;
            SUCCESS_COUNT_MAP.computeIfAbsent(current, (k) -> new AtomicInteger(0)).incrementAndGet();
        } catch (Throwable e) {
            FAILED_COUNT_MAP.computeIfAbsent(current, (k) -> new AtomicInteger(0)).incrementAndGet();
            msg = e.getMessage();
        }finally {
            costTs = System.currentTimeMillis() - ts;
        }
        System.out.printf("    <cost>%s</cost>%n", costTs);
        System.out.printf("    <success>%s</success>%n    <msg>%s</msg>%n</" + group + ">%n", success, msg);
    }
}
