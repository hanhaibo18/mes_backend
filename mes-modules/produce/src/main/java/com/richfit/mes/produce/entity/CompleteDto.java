package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.ForgControlRecord;
import com.richfit.mes.common.model.produce.LayingOff;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackCompleteExtra;
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
}
