package com.bearead.hystrix;

import com.bearead.hystrix.annotation.API;
import com.bearead.hystrix.annotation.Server;
import com.bearead.hystrix.bean.APIRequestMethod;
import com.bearead.hystrix.bean.APIState;
import com.bearead.hystrix.exception.BreakerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * api断路器切面
 * @author kation
 */
@Aspect
@Component
public class APIHystrixAOP {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private APIHystrixExecutor apiHystrixExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    @Pointcut("@annotation(com.bearead.hystrix.annotation.API)")
    public void pointcutAPI(){

    }

    @Around("pointcutAPI()")
    public Object apiHystrix(ProceedingJoinPoint pjp) throws Throwable{

        //获取方法上注解中表明的api
        API apiAnnotation = this.getAPIAnnotation(pjp);
        Server serverAnnotation = this.getServerAnnotation(pjp);

        String server = serverAnnotation.server();
        String api = apiAnnotation.api();
        APIRequestMethod requestMethod = apiAnnotation.requestMethod();

        APIState state = apiHystrixExecutor.isCircuitreaker(server, api, requestMethod);
        if(state == APIState.OPEN){//接口断路，抛出异常
            BreakerException breaker = new BreakerException("接口已经断路 [server=" + server + ",api="+ api+ "]");
            logger.info(breaker.getMessage(), breaker);
            return this.fallback(pjp, breaker);
        }
        Object result = pjp.proceed();//执行方法
        if(state == APIState.HALF_OPEN){//如果状态处于恢复中的状态，则关闭断路器
            apiHystrixExecutor.closeCircuitBreakers(server, api, requestMethod);
        }
        return result;

    }

    @AfterThrowing(pointcut = "pointcutAPI()", throwing = "e")
    public Object apiException(JoinPoint point, Throwable e) throws Throwable{
        this.logExceptions(point, e);
        return this.fallback(point, e);
    }

    /**
     * 服务降级策略
     * @return
     */
    private Object fallback(JoinPoint point, Throwable e) throws Throwable{
        Server serverAnnotation = this.getServerAnnotation(point);
        Class<?> fallback = serverAnnotation.fallback();
        if(fallback == void.class){//未定义降级策略则抛出原有的异常
            throw e;
        }

        //执行服务器降级策略
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method targetMethod = methodSignature.getMethod();
        Object fallbackObject = applicationContext.getBean(fallback);
        try {
            if (point.getArgs() != null && point.getArgs().length > 0) {
                Object[] arrParam = point.getArgs();
                Class[] argsClass = new Class[arrParam.length];
                for (int i = 0; i < arrParam.length; i++) {
                    argsClass[i] = arrParam[i].getClass();
                }
                Method method = fallback.getMethod(targetMethod.getName(), argsClass);
                return method.invoke(fallbackObject, point.getArgs());
            } else {
                Method method = fallback.getMethod(targetMethod.getName());
                return method.invoke(fallbackObject);
            }
        } catch (Throwable fallbackException){
            //降级策略异常，则抛出异常给调用者
            throw fallbackException.getCause();
        }
    }

    /**
     * 记录异常
     * @param point
     * @param e
     */
    private void logExceptions(JoinPoint point, Throwable e){
        API apiAnnotation = this.getAPIAnnotation(point);
        Server serverAnnotation = this.getServerAnnotation(point);
        String server = serverAnnotation.server();
        String api = apiAnnotation.api();
        APIRequestMethod requestMethod = apiAnnotation.requestMethod();
        apiHystrixExecutor.addAPIWrongCallRecord(server, api, requestMethod, e);
    }

    /**
     * 获取连接点中的API注解相关信息
     * @param point
     * @return
     * @throws RuntimeException
     */
    private API getAPIAnnotation(JoinPoint point) throws RuntimeException{
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method targetMethod = methodSignature.getMethod();
        if(!targetMethod.isAnnotationPresent(API.class)) {
            logger.error("@API注解错误! ["+ targetMethod.getName() +"]");
            throw new RuntimeException("@API注解错误!");
        }
        //获取方法上注解中表明的api
        API apiAnnotation = targetMethod.getAnnotation(API.class);
        return apiAnnotation;
    }

    /**
     * 获取连接点中的Server注解相关信息
     * @param point
     * @return
     * @throws RuntimeException
     */
    private Server getServerAnnotation(JoinPoint point) throws RuntimeException{
        Class<?> targetClass =point.getTarget().getClass();
        if(!targetClass.isAnnotationPresent(Server.class)){
            logger.error("@Server注解错误! ["+ targetClass.getName() +"]");
            throw new RuntimeException("@Server注解错误!");
        }
        Server serverAnnotation = targetClass.getAnnotation(Server.class);
        return serverAnnotation;
    }


}
