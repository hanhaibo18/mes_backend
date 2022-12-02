package com.richfit.mes.produce.entity.quality;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @ClassName: DisqualificationVo.java
 * @Author: renzewen
 * @CreateTime: 2022年11月30日 17:20:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InspectionPowerVo{
    @ApiModelProperty(value = "委托单号", dataType = "String")
    private String orderNo;
    @ApiModelProperty(value = "委托单位", dataType = "String")
    private String inspectionDepart;
    @ApiModelProperty(value = "委托单状态（逗号隔开）", dataType = "String")
    private String status;
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawNo;
    @ApiModelProperty(value = "样品名称", dataType = "String")
    private String sampleName;
    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;
    @ApiModelProperty(value = "指派状态", dataType = "String")
    private String assignStatus;


    @ApiModelProperty(value = "组织机构编码", dataType = "String")
    private String branchCode;
    @ApiModelProperty(value = "组织机构id", dataType = "String")
    private String tenantId;
    @ApiModelProperty(value = "跟单号", dataType = "String")
    private String trackNo;
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;
    @ApiModelProperty(value = "委托单类型（0、有源 1、无源）", dataType = "String")
    private String isExistHeadInfo;
    @ApiModelProperty(value = "审核状态（未审核0、已审核1）", dataType = "String")
    private String isAudit;
    @ApiModelProperty(value = "开工状态（0、待报工  1、已完工）", dataType = "String")
    private String isDoing;
    @ApiModelProperty(value = "检测类型", dataType = "String")
    private String tempType;
    private String order;
    private String orderCol;
    private int page;
    private int limit;
}
