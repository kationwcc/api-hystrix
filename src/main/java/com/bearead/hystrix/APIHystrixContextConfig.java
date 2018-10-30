package com.bearead.hystrix;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * api断路器依赖的spring相关配置
 * @author kation
 */
@Configuration
@EnableAspectJAutoProxy
public class APIHystrixContextConfig {

}
