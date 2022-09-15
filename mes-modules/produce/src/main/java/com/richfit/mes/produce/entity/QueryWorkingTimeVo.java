package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackComplete;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: QueryWorkingTimeVo.java
 * @Author: Hou XinYu
 * @Description: TODO
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
}
