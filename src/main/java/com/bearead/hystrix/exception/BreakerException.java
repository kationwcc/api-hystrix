package com.bearead.hystrix.exception;

/**
 * 断路器异常
 * @author kation
 */
public class BreakerException extends RuntimeException {

    public BreakerException() {

    }

    public BreakerException(String message) {
        super(message);
    }


}
