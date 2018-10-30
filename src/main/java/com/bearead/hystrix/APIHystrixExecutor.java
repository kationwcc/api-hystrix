package com.bearead.hystrix;

import com.bearead.hystrix.bean.APIRequestMethod;
import com.bearead.hystrix.bean.APIServer;
import com.bearead.hystrix.bean.APIState;

import java.util.Map;

/**
 * api断路器执行器
 * @author kation
 */
public class APIHystrixExecutor {

    public static APIHystrixConfig apiHystrixConfig;

    private APIManager apiManager;

    public APIHystrixExecutor(APIHystrixConfig apiHystrixConfig, APIManager apiManager) {
        this.apiHystrixConfig = apiHystrixConfig;
        this.apiManager = apiManager;
    }

    /**
     * 闭合断路器
     * @param server
     * @param api
     * @param requestMethod
     */
    public void closeCircuitBreakers(String server, String api, APIRequestMethod requestMethod){
        apiManager.closeCircuitBreakers(server, api, requestMethod);
    }

    /**
     * 针对服务和api地址添加错误调用记录
     * @param server
     * @param api
     * @param requestMethod
     * @param e
     * @return
     */
    public APIState addAPIWrongCallRecord(String server, String api, APIRequestMethod requestMethod, Throwable e){
        return apiManager.addAPIWrongCallRecord(server, api, requestMethod, e);
    }

    /**
     * 获取api状态
     * @param server
     * @param api
     * @param requestMethod
     * @return
     */
    public APIState isCircuitreaker(String server, String api, APIRequestMethod requestMethod){
        return apiManager.isCircuitreaker(server, api, requestMethod);
    }

    /**
     * 获取api各节点状态
     * @return
     */
    public Map<String, APIServer> getApiTable() {
        return apiManager.getApiTable();
    }


}
