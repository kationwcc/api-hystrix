package com.bearead.hystrix;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * api断路器中的配置类
 * @author kation
 */
@Component("apiHystrixConfig")
public class APIHystrixConfig {

    /**
     * 最大错误数量/次
     */
    @Value("${api.hystrix.fail-max-count:50}")
    private int failMaxCount;

    /**
     * 错误时间段/秒
     */
    @Value("${api.hystrix.fail-time-slot:180}")
    private int failTimeSlot;

    /**
     * 超时时间/毫秒
     */
    @Value("${api.hystrix.time-out:5000}")
    private int timeOut;

    /**
     * 尝试时间/毫秒
     */
    @Value("${api.hystrix.try-again:30000}")
    private int tryAgain;


    public int getFailMaxCount() {
        return failMaxCount;
    }

    public int getFailTimeSlot() {
        return failTimeSlot;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public int getTryAgain() {
        return tryAgain;
    }

    public static long getMaxFailTimeSlot() {
        int failTimeSlot = APIHystrixExecutor.apiHystrixConfig.getFailTimeSlot();
        long maxTime = System.currentTimeMillis() - (long)(failTimeSlot * 1000);
        return maxTime;
    }

}
