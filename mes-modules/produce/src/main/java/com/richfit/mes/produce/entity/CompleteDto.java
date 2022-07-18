package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.TrackComplete;
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
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String trackId;
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String trackNo;
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String prodNo;
    @ApiModelProperty(value = "报工人", dataType = "String")
    private String completeBy;
}