package com.bearead.hystrix.annotation;

import com.bearead.hystrix.bean.APIRequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义api访问注解
 * @author kation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface API {

    String api() default "";
    APIRequestMethod requestMethod() default APIRequestMethod.GET;


}
