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
     * 名称
     */
    @ApiModelProperty(value = "名称 ", dataType = "String")
    private String demandName;

    /**
     * ERP物料编码
     */
    @ApiModelProperty(value = "ERP物料编码 ", dataType = "String")
    private String erpProductCode;

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
     * 计划完成时间
     */
    @ApiModelProperty(value = "计划完成时间 ", dataType = "Date")
    private Date planEndTime;

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
     * 提单日期
     */
    @ApiModelProperty(value = "提单日期 ", dataType = "Date")
    private Date submitOrderTime;
    /**
     * 备注字段
     */
    protected String remark;
}
