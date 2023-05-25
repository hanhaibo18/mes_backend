package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.*;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: QueryWorkingTimeVo.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年07月13日 09:52:00
 */
@Data
public class QueryWorkingTimeVo {
    @ApiModelProperty(value = "工序工时", dataType = "TrackItem")
    private Assign assign;
    @ApiModelProperty(value = "报工记录", dataType = "List<TrackComplete>")
    private List<TrackComplete> trackCompleteList;
    @ApiModelProperty(value = "检验人", dataType = "String")
    private String qcPersonId;
    @ApiModelProperty(value = "检验车间", dataType = "String")
    private String qualityCheckBranch;
    @ApiModelProperty(value = "下料信息", dataType = "LayingOff")
    private LayingOff layingOff;
    @ApiModelProperty(value = "锻造信息", dataType = "List<ForgControlRecord>")
    private List<ForgControlRecord> forgControlRecordList;
    @ApiModelProperty(value = "锻造试棒信息")
    private String barForge;
    @ApiModelProperty(value = "锻造备注信息")
    private String ForgeRemark;
    @ApiModelProperty(value = "原材料信息", dataType = "List<RawMaterialRecord>")
    private List<RawMaterialRecord> rawMaterialRecordList;
    @ApiModelProperty(value = "扣箱工序报工记录信息", dataType = "Knockout")
    private Knockout knockout;
    @ApiModelProperty(value = "造型/制芯工序报工信息", dataType = "ModelingCore")
    private ModelingCore modelingCore;
    @ApiModelProperty(value = "炼钢作业记录", dataType = "RecordsOfSteelmakingOperations")
    private RecordsOfSteelmakingOperations recordsOfSteelmakingOperations;
    @ApiModelProperty(value = "浇注作业记录", dataType = "RecordsOfPourOperations")
    private RecordsOfPourOperations recordsOfPourOperations;
    @ApiModelProperty(value = "浇注温度", dataType = "String")
    private String pourTemperature;
}
