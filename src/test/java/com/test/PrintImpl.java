package com.test;

import com.bearead.hystrix.bean.APIRequestMethod;
import com.bearead.hystrix.annotation.API;
import com.bearead.hystrix.annotation.Server;
import org.springframework.stereotype.Component;

@Server(server = "test-server", fallback = PrintFallBack.class)
@Component("printImpl")
public class PrintImpl implements Print{

    public static boolean isError = true;

    @API(api = "/test/api", requestMethod = APIRequestMethod.GET)
    @Override
    public void print(String info) throws RuntimeException{
        System.out.println(info);

        if(isError){
            throw new RuntimeException("kation test error");
        }
    }


}
