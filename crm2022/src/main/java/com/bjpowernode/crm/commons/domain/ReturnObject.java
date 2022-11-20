package com.bjpowernode.crm.commons.domain;

// 封装返回信息的pojo
public class ReturnObject {
    private String code;
    private String message;

    private Object retData;

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
