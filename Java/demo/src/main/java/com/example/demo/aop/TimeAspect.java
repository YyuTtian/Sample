package com.example.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect // 当前类为切面类
@Component
@Order(1) // 切面类的执行顺序（前置通知：数字越小先执行; 后置通知：数字越小越后执行）
public class TimeAspect {

    //     @Around：环绕通知，此注解标注的通知方法在目标方法前、后都被执行
    //     @Before：前置通知，此注解标注的通知方法在目标方法前被执行
    //     @After ：后置通知，此注解标注的通知方法在目标方法后被执行，无论是否有异常都会执行
    //     @AfterReturning ： 返回后通知，此注解标注的通知方法在目标方法后被执行，有异常不会执行
    //     @AfterThrowing ： 异常后通知，此注解标注的通知方法发生异常后执行

    // 切入点方法（公共的切入点表达式）
    // execution(访问修饰符?  返回值  包名.类名.?方法名(方法参数) throws 异常?)
    // 其中带?的表示可以省略的部分
    //      访问修饰符：可省略（比如: public、protected）
    //      包名.类名： 可省略
    //      throws 异常：可省略（注意是方法上声明抛出的异常，不是实际抛出的异常）
    // * 单个独立的任意符号，可以通配任意返回值、包名、类名、方法名、任意类型的一个参数，也可以通配包、类、方法名的一部分
    // .. 多个连续的任意符号，可以通配任意层级的包，或任意类型、任意个数的参数

    // 方法的访问修饰符可以省略
    // 返回值可以使用*号代替（任意返回值类型）
    // 包名可以使用*号代替，代表任意包（一层包使用一个*）
    // 使用..配置包名，标识此包以及此包下的所有子包
    // 类名可以使用*号代替，标识任意类
    // 方法名可以使用*号代替，表示任意方法
    // 可以使用 * 配置参数，一个任意类型的参数
    // 可以使用.. 配置参数，任意个任意类型的参数
    @Pointcut("execution(* com.example.demo.*.*(..))")
    private void pt() {

    }

    // 前置通知（引用切入点）
    @Before("pt()")
    public void before(JoinPoint joinPoint) {
        log.info("before ...");

    }

    // 环绕通知
    @Around("pt()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("around before ...");

        // 记录方法执行开始时间
        long begin = System.currentTimeMillis();

        // 调用目标对象的原始方法执行
        Object result = proceedingJoinPoint.proceed();
        // 原始方法在执行时：发生异常 后续代码不在执行
        log.info("around after ...");
        // 记录方法执行结束时间
        long end = System.currentTimeMillis();
        // 计算方法执行耗时
        log.info("方法执行耗时: {}毫秒", end - begin);
        return result;
    }

    // 后置通知
    @After("pt()")
    public void after(JoinPoint joinPoint) {
        log.info("after ...");
    }

    // 返回后通知（程序在正常执行的情况下，会执行的后置通知）
    @AfterReturning("pt()")
    public void afterReturning(JoinPoint joinPoint) {
        log.info("afterReturning ...");
    }

    // 异常通知（程序在出现异常的情况下，执行的后置通知）
    @AfterThrowing("pt()")
    public void afterThrowing(JoinPoint joinPoint) {
        log.info("afterThrowing ...");
    }
}
