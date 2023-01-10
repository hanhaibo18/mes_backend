package com.richfit.mes.produce.entity.heat;

import com.richfit.mes.common.model.produce.TrackComplete;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: HeatCompleteDto.java
 * @Author: renzewen
 * @Description: 新增报工信息
 * @CreateTime: 2023年1月9日 13:54:00
 */
@Data
public class HeatCompleteDto {

    public static final Integer IS_UPDATE = 1;
    @ApiModelProperty(value = "报工信息", dataType = "List<TrackComplete>")
    private List<TrackComplete> trackCompleteList;
    @ApiModelProperty(value = "工序Ids", dataType = "List<String>")
    private List<String> tiIds;
    @ApiModelProperty(value = "预装炉id", dataType = "String")
    private String prechargeFurnaceId;
    @ApiModelProperty(value = "isUpdate", dataType = "String")
    private String isUpdate;
}
