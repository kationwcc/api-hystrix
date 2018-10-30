package com.test;

import org.springframework.stereotype.Component;

@Component("printFallBack")
public class PrintFallBack implements Print{

    @Override
    public void print(String info) throws RuntimeException{

        System.out.println("---进入fallback---");
        throw new RuntimeException("next");
    }

}
