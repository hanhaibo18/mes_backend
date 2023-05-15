package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: OutsourceComplete.java
 * @Author: Hou XinYu
 * @Description: 外协报工DTO
 * @CreateTime: 2023年02月07日 10:12:00
 */
@Data
public class OutsourceCompleteDto {
    @ApiModelProperty(value = "跟单IdList")
    private List<String> trackHeadId;
    @ApiModelProperty(value = "optName,optNo")
    private List<OutsourceDto> outsourceDtoList;
    @ApiModelProperty(value = "产品编号List")
    private List<String> prodNoList;
    @ApiModelProperty(value = "报工信息")
    private TrackComplete trackComplete;
    @ApiModelProperty(value = "ItemList")
    private List<TrackItem> trackItemList;
    @ApiModelProperty(value = "branchCode")
    private String branchCode;
}
