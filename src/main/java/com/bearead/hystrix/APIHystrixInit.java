package com.bearead.hystrix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * APIHystrix 初始化
 * @author kation
 */
@Configuration
public class APIHystrixInit {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Bean("apiHystrixExecutor")
    @Scope("singleton")
    public APIHystrixExecutor apiHystrixExecutor(@Autowired APIHystrixConfig apiHystrixConfig){
        APIManager apiManager = new APIManager();
        APIHystrixExecutor executor = new APIHystrixExecutor(apiHystrixConfig, apiManager);
        logger.info("APIHystrixInit 初始化成功");
        return executor;
    }

}
