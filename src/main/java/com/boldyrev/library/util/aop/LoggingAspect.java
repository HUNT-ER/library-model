package com.boldyrev.library.util.aop;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.boldyrev.library.services..*)")
    private void allServicesMethods() {
    }

    @Around("allServicesMethods()")
    public Object logExecutionTimeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        log.debug("Method: {} - starts. Args: {}", joinPoint.getSignature(),
            Arrays.toString(joinPoint.getArgs()));

        Object result = joinPoint.proceed();

        log.debug("Method:{} - ends. Args: {}, Execution time: {}", joinPoint.getSignature(),
            Arrays.toString(joinPoint.getArgs()), System.currentTimeMillis() - start);

        return result;
    }
}
