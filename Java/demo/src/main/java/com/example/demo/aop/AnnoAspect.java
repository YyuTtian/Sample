package com.example.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect // 当前类为切面类
@Component
@Order(1) // 切面类的执行顺序（前置通知：数字越小先执行; 后置通知：数字越小越后执行）
public class AnnoAspect {

    @Pointcut("@annotation(com.example.demo.anno.MyLog)")
    private void pt() {
    }

    // 在需要的方法上加上MyLog注解
    // 环绕通知
    @Around("pt()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // 操作类名
        String className = joinPoint.getTarget().getClass().getName();

        // 操作方法名
        String methodName = joinPoint.getSignature().getName();

        // 操作方法参数
        Object[] args = joinPoint.getArgs();
        String methodParams = Arrays.toString(args);

        // 记录方法执行开始时间
        long begin = System.currentTimeMillis();
        // 调用目标对象的原始方法执行
        Object result = joinPoint.proceed();
        // 原始方法在执行时：发生异常 后续代码不在执行

        // 记录方法执行结束时间
        long end = System.currentTimeMillis();
        // 计算方法执行耗时
        log.info("方法执行耗时: {}毫秒", end - begin);
        return result;
    }
}
