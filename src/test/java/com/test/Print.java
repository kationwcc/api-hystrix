package com.test;

import com.bearead.hystrix.bean.APIRequestMethod;
import com.bearead.hystrix.annotation.API;
import com.bearead.hystrix.annotation.Server;


@Server(server = "test-server", fallback = PrintFallBack.class)
public interface Print {

    @API(api = "/test/api", requestMethod = APIRequestMethod.GET)
    void print(String info) throws RuntimeException;


}
