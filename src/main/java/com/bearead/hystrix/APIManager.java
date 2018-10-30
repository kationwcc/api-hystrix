package com.bearead.hystrix;

import com.bearead.hystrix.bean.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * api管理者，处理各个api的相关状态
 * @author kation
 */
public class APIManager {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * api服务列表
     */
    private Map<String, APIServer> apiTable = new ConcurrentHashMap<>();

    /**
     * 针对服务和api地址添加错误调用记录
     * @param server
     * @param api
     * @param requestMethod
     * @param e
     * @return
     */
    public APIState addAPIWrongCallRecord(String server, String api, APIRequestMethod requestMethod, Throwable e){
        APIContent apiContent = this.getAPIContent(server, api, requestMethod);
        synchronized (apiContent){
            APIWrongCallRecord callRecord = new APIWrongCallRecord(e.getMessage(), new Date());
            apiContent.getWrongCall().addFirst(callRecord);
            logger.info("api添加错误信息 [api="+ apiContent.getApi()+ "]");
            check(apiContent);
            return apiContent.getState();
        }
    }

    /**
     * 闭合断路器
     * @param server
     * @param api
     * @param requestMethod
     */
    public void closeCircuitBreakers(String server, String api, APIRequestMethod requestMethod) {
        APIContent apiContent = this.getAPIContent(server, api, requestMethod);
        synchronized (apiContent){
            if(apiContent.getState() == APIState.HALF_OPEN){
                logger.info("api进入恢复正常状态 [api="+ apiContent.getApi()+ "]");
                flush(apiContent);
                this.closeCircuitBreakers(apiContent);
            }
        }
    }

    /**
     * 获取api状态
     * @param server
     * @param api
     * @param requestMethod
     * @return
     */
    public APIState isCircuitreaker(String server, String api, APIRequestMethod requestMethod) {
        APIContent apiContent = this.getAPIContent(server, api, requestMethod);
        synchronized (apiContent){
            checkTryAgain(apiContent);
            return apiContent.getState();
        }
    }

    public Map<String, APIServer> getApiTable() {
        return apiTable;
    }

    /**
     * 根据server和api获取APIContent
     * 如果不存在这个APIContent，则创建一个新的APIContent
     * @param server
     * @param api
     * @param requestMethod
     * @return
     */
    private APIContent getAPIContent(String server, String api, APIRequestMethod requestMethod){
        APIServer apiServer = this.getAPIServer(server);
        APIContent content = null;
        for (APIContent contentEnt : apiServer.getApiContentSet()){
            if(contentEnt.getApi().equals(api)
                    && contentEnt.getApiRequestMethod() == requestMethod){
                content = contentEnt;
                break;
            }
        }
        if(content == null){
            content = this.newAPIContent(api, requestMethod);
            apiServer.getApiContentSet().add(content);
        }
        return content;
    }

    /**
     * 根据server获取APIServer对象
     * 如果不存在这个APIServer，则创建一个新的APIServer
     * @param server
     * @return APIServer
     */
    private APIServer getAPIServer(String server){
        APIServer apiServer;
        if(!apiTable.containsKey(server)){
            apiServer = this.newAPIServer(server);
            apiTable.put(server, apiServer);
        } else {
            apiServer = apiTable.get(server);
        }
        return apiServer;
    }

    /**
     * 获取一个新的APIServer对象
     * @param server
     * @return
     */
    private APIServer newAPIServer(String server){
        APIServer apiServer = new APIServer();
        apiServer.setServer(server);
        return apiServer;
    }

    /**
     * 获取一个新的APIContent对象
     * @param api
     * @param requestMethod
     * @return
     */
    private APIContent newAPIContent(String api, APIRequestMethod requestMethod){
        APIContent content = new APIContent();
        content.setApi(api);
        content.setApiRequestMethod(requestMethod);
        return content;
    }

    /**
     * 闭合断路器
     * @param apiContent
     */
    private void closeCircuitBreakers(APIContent apiContent){
        apiContent.setState(APIState.CLOSED);
    }

    /**
     * 检查接口
     * 1.错误调用数量等于最大错误数量，调用检查时间段逻辑
     * 2.错误调用数量大于最大错误数量，调用检查时间段逻辑，并且扣除队列中最后的一个元素
     * @param apiContent
     * @author: kation
     */
    private void check(APIContent apiContent){
        int maxCount = APIHystrixExecutor.apiHystrixConfig.getFailMaxCount();
        ConcurrentLinkedDeque<APIWrongCallRecord> wrongCall = apiContent.getWrongCall();
        if(apiContent.getWrongCall().size() > maxCount) {
            checkTime(apiContent);
            wrongCall.removeLast();
        } else if (wrongCall.size() == maxCount) {
            checkTime(apiContent);
        }

    }

    /**
     * 检查时间段
     * 最早的错误时间在监听错误时间段内时，切换接口为断路状态
     * @param apiContent
     * @author: kation
     */
    private void checkTime(APIContent apiContent){
        ConcurrentLinkedDeque<APIWrongCallRecord> wrongCall = apiContent.getWrongCall();
        long maxTime = APIHystrixExecutor.apiHystrixConfig.getMaxFailTimeSlot();
        if(wrongCall.getLast().getDate().getTime() >= maxTime){
            apiContent.setHystrixDate(new Date());
            apiContent.setState(APIState.OPEN);
            logger.info("api进入断路状态 [api="+ apiContent.getApi()+ "]");
        }
        flush(apiContent);
    }

    /**
     * 刷新API错误调用记录，去除错误时间在错误时间段之外的记录
     * @param apiContent
     * @author: kation
     */
    private void flush(APIContent apiContent){
        long maxTime = APIHystrixExecutor.apiHystrixConfig.getMaxFailTimeSlot();
        for (APIWrongCallRecord callRecord : apiContent.getWrongCall()){
            if(callRecord.getDate().getTime() < maxTime){
                apiContent.getWrongCall().remove(callRecord);
            }
        }
    }

    /**
     * 当api断路之后，尝试恢复的逻辑
     * 错误尝试恢复条件
     * 1.断路器为开启状态(OPEN)
     * 2.断路器开始时间 + 错误尝试恢复时间 < 当前时间
     * @param apiContent
     * @author: kation
     */
    private void checkTryAgain(APIContent apiContent){
        if(apiContent.getState() == APIState.OPEN){
            long hystrixDateTime = apiContent.getHystrixDate().getTime();
            long tryAgainTime = hystrixDateTime + APIHystrixExecutor.apiHystrixConfig.getTryAgain();
            if(tryAgainTime < System.currentTimeMillis()){
                apiContent.setState(APIState.HALF_OPEN);
                logger.info("api进入恢复状态 [api="+ apiContent.getApi() + "]");
            }
        }
    }


}
