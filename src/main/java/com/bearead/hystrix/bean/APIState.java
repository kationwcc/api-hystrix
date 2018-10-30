package com.bearead.hystrix.bean;

/**
 * 断路器状态
 * OPEN         打开状态，不可直接调用接口
 * CLOSED       关闭状态，直接调用接口
 * HALF_OPEN    处于打开和关闭状态之间，但接口曾经关闭并且进入了尝试恢复逻辑时处于此状态，可调用接口
 * @author kation
 */
public enum APIState {

    OPEN,
    CLOSED,
    HALF_OPEN;


}
