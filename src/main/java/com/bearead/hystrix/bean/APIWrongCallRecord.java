package com.bearead.hystrix.bean;

import java.util.Date;

/**
 * 具体的api错误调用相关信息
 */
public class APIWrongCallRecord {

    /**
     * 调用的错误message
     */
    private String message;

    /**
     * 错误产生时间
     */
    private Date date;

    public APIWrongCallRecord(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
