package com.test;

import com.alibaba.fastjson.JSON;
import com.bearead.hystrix.APIHystrixExecutor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {



    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
        APIHystrixExecutor executor = context.getBean(APIHystrixExecutor.class);

        Print print = (Print) context.getBean("printImpl");
        for (int i = 0; i < 10 ; i++ ){
            try {
                System.out.println("-------------------------------------------------------------------------------------" + i);
                print.print("112233");
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        System.out.println(JSON.toJSONString(executor.getApiTable()));
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("-------------------------------------------------------------------------------------");

        try {
            Thread.sleep(2000);
            PrintImpl.isError = false;
            print.print("112233");
        } catch (RuntimeException e){
            // e.getMessage();
        } catch (Exception e){
            e.getMessage();
        }



        System.out.println(JSON.toJSONString(executor.getApiTable()));


    }


}
