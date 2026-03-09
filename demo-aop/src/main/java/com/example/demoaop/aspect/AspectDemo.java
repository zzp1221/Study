package com.example.demoaop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
@Slf4j
@Aspect
@Component
public class AspectDemo {
    @Pointcut("execution(* com.example.demoaop.controller.*.*(..))")
    private void pt() {}

    @Around("pt()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info(joinPoint.getSignature()+"时间"+(endTime-startTime)+"ms");
        return result;
    }

    @Before("execution(* com.example.demoaop.controller.*.*(..))")
    public void dobefore(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("do before");
    }
    @After("execution(* com.example.demoaop.controller.*.*(..))")
    public void after(JoinPoint joinPoint) {
        log.info("do after");
    }
}
