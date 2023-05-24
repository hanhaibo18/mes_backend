package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: CompleteDto.java
 * @Author: Hou XinYu
 * @Description: 新增报工信息
 * @CreateTime: 2022年07月12日 13:54:00
 */
@Data
public class CompleteDto {
    @ApiModelProperty(value = "报工信息", dataType = "List<TrackComplete>")
    private List<TrackComplete> trackCompleteList;
    @ApiModelProperty(value = "质检人员", dataType = "String")
    private String qcPersonId;
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String tiId;
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String assignId;
    @ApiModelProperty(value = "跟单ID", dataType = "String")
    private String trackId;
    @ApiModelProperty(value = "跟单号", dataType = "String")
    private String trackNo;
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String prodNo;
    @ApiModelProperty(value = "报工人", dataType = "String")
    private String completeBy;
    @ApiModelProperty(value = "报工额外信息", dataType = "List<TrackCompleteExtra>")
    private List<TrackCompleteExtra> trackCompleteExtraList;
    @ApiModelProperty(value = "下料信息", dataType = "LayingOff")
    private LayingOff layingOff;
    @ApiModelProperty(value = "锻造信息", dataType = "ForgControlRecord")
    private List<ForgControlRecord> forgControlRecordList;
    @ApiModelProperty(value = "锻造试棒信息")
    private String barForge;
    @ApiModelProperty(value = "锻造备注信息")
    private String ForgeRemark;
    @ApiModelProperty(value = "1、机加 2、装配 ")
    private String classes;
    @ApiModelProperty(value = "原材料消耗信息", dataType = "RawMaterialRecord")
    private List<RawMaterialRecord> rawMaterialRecordList;
    @ApiModelProperty(value = "扣箱工序报工记录信息", dataType = "Knockout")
    private Knockout knockout;
    @ApiModelProperty(value = "造型/制芯工序报工信息", dataType = "ModelingCore")
    private ModelingCore modelingCore;
    @ApiModelProperty(value = "下工序装炉", dataType = "Boolean")
    private Boolean nextFurnace;
}
