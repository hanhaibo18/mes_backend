package com.tc.mes.plm.entity.dto;

import lombok.Data;

/**
 *  pdm 订单bom
 */
@Data
public class OrderBomDto {

    /**
     *  工作号
     */
    private String workNo;
    /**
     *  父项目id
     */
    private String parentItemId;
    /**
     *  父项目名字
     */
    private String parentName;
    /**
     *  父转速id
     */
    private String parentRevId;
    /**
     *  父订单id
     */
    private String parentOrderId;
    /**
     *  子项目id
     */
    private String childItemId;
    /**
     *  子项目名字
     */
    private String childName;
    /**
     *  子转速id
     */
    private String childRevId;
    /**
     *  子订单id
     */
    private String childOrderId;
    /**
     *  子数量
     */
    private String childNum;
    /**
     *  生产用途
     */
    private String productionSpe;
    /**
     *  技术描述
     */
    private String techDesc;

}
