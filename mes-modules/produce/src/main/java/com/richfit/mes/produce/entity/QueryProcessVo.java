package com.richfit.mes.produce.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: queryProcessVo.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年06月15日
 */
@Data
public class QueryProcessVo {

    @ApiModelProperty(value = "id", dataType = "String")
    private String id;
    @ApiModelProperty(value = "跟单Id", dataType = "String")
    private String trackHeadId;
    @ApiModelProperty(value = "工序ID", dataType = "String")
    public String optId;
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;
    @ApiModelProperty(value = "工序版本", dataType = "String")
    private String optVer;
    @ApiModelProperty(value = "工序状态", dataType = "String")
    @TableField
    private String optState;
    @ApiModelProperty(value = "是否是当前工序", dataType = "Integer")
    private Integer isCurrent;
    @ApiModelProperty(value = "准结工时", dataType = "Double")
    private Double prepareEndHours;
    @ApiModelProperty(value = "单件工时", dataType = "Double")
    private Double singlePieceHours;
    @ApiModelProperty(value = "是否并行", dataType = "Double")
    private Integer optParallelType;
    @ApiModelProperty(value = "是否派工", dataType = "String")
    public String isDispatching;
    @ApiModelProperty(value = "工序状态")
    private Integer isDoing;

    @ApiModelProperty(value = "实施温度℃（热工）", dataType = "String")
    private String tempWork;

    @ApiModelProperty(value = "保温时间h（热工）", dataType = "String")
    private String holdTime;

    @ApiModelProperty(value = "冷却方式（热工）", dataType = "String")
    private String coolType;
}
