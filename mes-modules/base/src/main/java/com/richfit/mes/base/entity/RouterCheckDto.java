package com.richfit.mes.base.entity;

import lombok.Data;



/**
 * @ClassName: RouterCheckDto.java
 * @Author: mafeng02
 * @Description: 检测内容DTO类
 * @CreateTime: 2022年07月13日 06:42:00
 */
@Data
public class RouterCheckDto {

    private String isImport;
    private String productName;
    private String routerNo;
    private String optNo;
    private String optName;
    private String orderNo;
    private String name;
    private String propertyUnit;
    private String propertySymbol;
    private String propertyLowerlimit;
    private String propertyUplimit;
    private String propertyTestmethod;
    private String propertyInputtype;
    private String  isNull;
    private String propertyDefaultvalue;

}
