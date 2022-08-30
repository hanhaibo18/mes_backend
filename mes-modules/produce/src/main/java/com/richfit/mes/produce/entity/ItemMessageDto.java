package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ItemMessageDto.java
 * @Author: Hou XinYu
 * @Description: 工序信息
 * @CreateTime: 2022年08月26日 17:21:00
 */
@Data
public class ItemMessageDto {
    @ApiModelProperty(value = "图号")
    private String drawingNo;
    @ApiModelProperty(value = "序号")
    private String serialNumber;
    @ApiModelProperty(value = "工序名称")
    private String optName;
    @ApiModelProperty(value = "版本")
    private String version;
    @ApiModelProperty(value = "有没有图纸")
    private String isDrawingNo;
    @ApiModelProperty(value = "工序类型")
    private String itemType;
    @ApiModelProperty(value = "PDM工序类型")
    private String pdmItemType;
    @ApiModelProperty(value = "并行工序")
    private Integer optParallelType;
    /**
     * 准结时间
     */
    @ApiModelProperty(value = "准结时间", dataType = "Double")
    private Double prepareEndHours;
    /**
     * 单件工时
     */
    @ApiModelProperty(value = "单件工时", dataType = "Double")
    private Double singlePieceHours;
    /**
     * 是否调度确认
     */
    @ApiModelProperty(value = "是否调度确认", dataType = "Integer")
    private Integer isExistScheduleCheck;
    /**
     * 是否质检确认
     */
    @ApiModelProperty(value = "是否质检确认", dataType = "Integer")
    private Integer isExistQualityCheck;
    @ApiModelProperty(value = "工序指导")
    private String notice;
}
