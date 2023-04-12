package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.ForgControlRecord;
import com.richfit.mes.common.model.produce.LayingOff;
import com.richfit.mes.common.model.produce.TrackComplete;
import io.swagger.annotations.ApiModelProperty;
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
}
