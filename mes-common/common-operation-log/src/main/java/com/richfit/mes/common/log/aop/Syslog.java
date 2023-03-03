package com.richfit.mes.common.log.aop;

import java.io.Serializable;

public class Syslog implements Serializable {
    private static final long serialVersionUID = 7611945253072005072L;
    private String id;  //我用的全宇宙唯一的子串串、也是直接用的工具类

    private String username; //用户名

    private String operation; //操作

    private String method; //方法名

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}