package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@Data
public class DemandExcel {
    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称", dataType = "String")
    private String projectName;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;

    /**
     * 图号
     */
    @ApiModelProperty(value = "图号 ", dataType = "String")
    private String drawNo;

    /**
     * 零件名称
     */
    @ApiModelProperty(value = "零件名称 ", dataType = "String")
    private String demandName;


    @ApiModelProperty(value = "产品名称 ", dataType = "String")
    private String productName;

    /**
     * 材质
     */
    @ApiModelProperty(value = " 材质", dataType = "String")
    private String texture;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量 ", dataType = "Integer")
    private Integer num;

    /**
     * 毛坯类型
     */
    @ApiModelProperty(value = "毛坯类型 ", dataType = "String")
    private String workblankType;


    /**
     * 加工单位
     */
    @ApiModelProperty(value = "加工单位 ", dataType = "String")
    private String inchargeOrg;


    /**
     * 需求日期
     */
    @ApiModelProperty(value = "需求日期 ", dataType = "Date")
    private Date demandTime;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号 ", dataType = "String")
    private String versionNum;

    /**
     * 备注字段
     */
    protected String remark;
}
