package com.bearead.hystrix.bean;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * api服务
 */
public class APIServer {

    /**
     * 服务名称
     */
    private String server;

    /**
     * 服务所提供的api
     */
    private Set<APIContent> apiContentSet = new CopyOnWriteArraySet<>();


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Set<APIContent> getApiContentSet() {
        return apiContentSet;
    }

    public void setApiContentSet(Set<APIContent> apiContentSet) {
        this.apiContentSet = apiContentSet;
    }
}
