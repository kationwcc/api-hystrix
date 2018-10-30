package com.bearead.hystrix.bean;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * api详情
 * @author kation
 */
public class APIContent {


    /**
     * api接口状态
     */
    private APIState state = APIState.CLOSED;

    /**
     * 断路时间,当state=CLOSED时,此属性为null
     */
    private Date hystrixDate;

    /**
     * api地址
     */
    private String api;

    /**
     * 请求类型
     */
    private APIRequestMethod apiRequestMethod;

    /**
     * api错误调用记录
     */
    private ConcurrentLinkedDeque<APIWrongCallRecord> wrongCall = new ConcurrentLinkedDeque<>();


    public APIState getState() {
        return state;
    }

    public void setState(APIState state) {
        this.state = state;
    }

    public Date getHystrixDate() {
        return hystrixDate;
    }

    public void setHystrixDate(Date hystrixDate) {
        this.hystrixDate = hystrixDate;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public APIRequestMethod getApiRequestMethod() {
        return apiRequestMethod;
    }

    public void setApiRequestMethod(APIRequestMethod apiRequestMethod) {
        this.apiRequestMethod = apiRequestMethod;
    }

    public ConcurrentLinkedDeque<APIWrongCallRecord> getWrongCall() {
        return wrongCall;
    }

    public void setWrongCall(ConcurrentLinkedDeque<APIWrongCallRecord> wrongCall) {
        this.wrongCall = wrongCall;
    }
}
