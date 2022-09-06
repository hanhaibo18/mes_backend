package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: renzewen
 * @Date: 2022/9/6 9:21
 */
@Data
@ApiModel(value = "探头信息")
public class ProbeInfo{

    private static final long serialVersionUID = -1472432735506772177L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "探伤记录关联id")
    private String recordId;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "频率")
    private String frequency;

    @ApiModelProperty(value = "角度")
    private String angle;

    @ApiModelProperty(value = "横波/纵波")
    private String waveShape;

    @ApiModelProperty(value = "前沿")
    private String leadingEdge;

    @ApiModelProperty(value = "排序号")
    private Integer serialNum;
}
