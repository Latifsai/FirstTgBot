package io.proj3ct.LatifFirstSpringBot.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(public * io.proj3ct.LatifFirstSpringBot.service.*.*(..))")
    public void serviceLog() {
    }

    @Before("serviceLog()")
    public void doBeforeServiceLog(JoinPoint joinPoint) {
        log.info("RUN SERVICE:\n" +
                        "SERVICE_METHOD : {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
    }


    @AfterReturning(returning = "returnObject", pointcut = "serviceLog()")
    public void doAfterReturning(Object returnObject) {
        log.info("\nReturn value: {}\n" +
                        "END OF REQUEST",
                returnObject);
    }

    @AfterThrowing(throwing = "ex", pointcut = "serviceLog()")
    public void throwsException(JoinPoint jp, Exception ex) {
        log.info("Request throw an exception. Cause - {}. {}",
                Arrays.toString(jp.getArgs()), ex.getMessage());
    }
}
