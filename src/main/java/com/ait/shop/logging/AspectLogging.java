package com.ait.shop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AspectLogging {

    private final Logger logger = LoggerFactory.getLogger(AspectLogging.class);

    @Pointcut("execution(* com.ait.shop.service.*ServiceImpl.*(..))")
    public void anyMethodInServiceLayer() {}

    @Before("anyMethodInServiceLayer()")
    public void beforeAnyMethodInServiceLayer(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.debug("Method {} of the class {} called with arguments: {}",
                 methodName, className, Arrays.toString(args));
    }

    // Нужно аккуратно относиться к логированию аргументов.
    // 1. Аргумент может быть очень большим объектом.
    // 2. Аргумент может содержать секреты.

    // Вместо @After более информативно применять связку @AfterReturning + @AfterThrowing
    @After("anyMethodInServiceLayer()")
    public void afterAnyMethodInServiceLayer(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class {} finished its work",
                methodName, className);
    }

    @AfterReturning(
            pointcut = "anyMethodInServiceLayer()",
            returning = "result"
    )
    public void afterReturningAnyMethodInServiceLayer(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class {} returned result: {}",
                methodName, className, result);
    }

    @AfterThrowing(
            pointcut = "anyMethodInServiceLayer()",
            throwing = "e"
    )
    public void afterThrowingAnyMethodInServiceLayer(JoinPoint joinPoint, Exception e) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class {} threw an exception",
                methodName, className, e);
    }
}
