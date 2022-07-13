package com.richfit.mes.base.entity;

import lombok.Data;

/**
 * @ClassName: RouterCheckDto.java
 * @Author: mafeng02
 * @Description: 检测内容DTO类
 * @CreateTime: 2022年07月13日 06:42:00
 */
@Data
public class RouterCheckQualityDto {

    private String isImport;
    private String productName;
    private String routerNo;
    private String optNo;
    private String optName;
    private String name;

}
