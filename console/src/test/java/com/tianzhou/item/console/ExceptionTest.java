package com.tianzhou.item.console;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ExceptionTest {

    @Test
    void testTryCatch1() {
        try {
            int a = 1 / 0;
            System.out.println("try中执行");
        } catch (ArithmeticException e) {
            System.out.println("捕获异常：" + e);
            throw new RuntimeException("sss");
        } finally {
            System.out.println("finally中执行");
        }
    }

    @Test
    void testTryCatch2() {
        try {
            int a = 1 / 0;
            System.out.println("try中执行");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testTryCatch3() {
        try {
            int a = 1 / 0;
            System.out.println("try中执行");
        } catch (Exception e) {
            log.error("发生异常", e);
        }
    }

    @Test
    void testTryCatch4() {
        try {
            System.out.println("try中执行");
        } catch (Exception e) {
            System.out.println("catch中执行");
        } finally {
            System.out.println("finally中执行");
        }
    }

    @Test
    void testReturnInTry() {
        System.out.println(func());
    }

    private int func() {
        try {
            return 100;
        } finally {
            System.out.println("即使return，finally仍然执行");
            return 200;
        }
    }

    @Test
    void testE() {
        int i = 2 / 0;
    }

    private int badFunc() {
        try {
            int i = 1 / 0;
            return 2;
        } finally {
            System.out.println("saaa");
        }
    }

    @Test
    void demo() {
        try {
            badFunc();
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
    }
}
