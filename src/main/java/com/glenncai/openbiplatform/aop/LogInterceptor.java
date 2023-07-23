package com.glenncai.openbiplatform.aop;

import static com.glenncai.openbiplatform.utils.NetUtils.getClientIpAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

/**
 * This class is for log interceptor to log request info
 *
 * @author Glenn Cai
 * @version 1.0 07/23/2023
 */
@Aspect
@Component
@Slf4j
public class LogInterceptor {

  @Around("execution(* com.glenncai.openbiplatform.controller.*.*(..))")
  public Object doInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
    // Starts a StopWatch to time the method execution
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    // Get the request url
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
    String requestUrl = request.getRequestURI();

    // Get client IP
    String clientIp = getClientIpAddress(request);

    // Generate unique request id
    String requestId = UUID.randomUUID().toString();

    // Get request arguments
    Object[] args = joinPoint.getArgs();
    String reqParam = "[" + StringUtils.join(args, ", ") + "]";

    // Print request log
    log.info("request start... request id: {}, url: {}, client ip: {}, request params: {}",
             requestId, requestUrl, clientIp, reqParam);

    // Execute the method
    Object result = joinPoint.proceed();

    // Stop the StopWatch
    stopWatch.stop();
    long totalTimeMillis = stopWatch.getTotalTimeMillis();

    // Print response log
    log.info("request end... request id: {}, cost: {}ms", requestId, totalTimeMillis);

    return result;
  }
}
