package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: ForDispatchingDto.java
 * @Author: Hou XinYu
 * @Description: 未派工查询参数
 * @CreateTime: 2022年09月28日 10:23:00
 */
@Data
public class ForDispatchingDto extends QueryPageDto {
    @ApiModelProperty(value = "siteId", dataType = "String")
    private String siteId;
    @ApiModelProperty(value = "跟单编号", dataType = "String")
    private String trackNo;
    @ApiModelProperty(value = "图号", dataType = "String")
    private String routerNo;
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;
    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;
    @ApiModelProperty(value = "状态", dataType = "String")
    private String state;
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String userId;
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;
    @ApiModelProperty(value = "车间类型", dataType = "String")
    private String classes;
    @ApiModelProperty(value = "实施温度℃（热工）", dataType = "String")
    private String tempWork;
    @ApiModelProperty(value = "实施温度℃（热工）扩展", dataType = "String")
    private String tempWork1;
    @ApiModelProperty(value = "预装炉id", dataType = "String")
    private String prechargeFurnaceId;

    @ApiModelProperty(value = "排序字段", dataType = "String")
    private String orderCol;
    @ApiModelProperty(value = "排序方式   asc 升序 ,  desc  降序", dataType = "String")
    private String order;
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @ApiModelProperty(value = "装炉编号", dataType = "String")
    private Long id;
    @ApiModelProperty(value = "材质", dataType = "String")
    private String texture;
    @ApiModelProperty(value = "零件名称", dataType = "String")
    private String materialName;
    @ApiModelProperty(value = "工序类型", dataType = "String")
    private String optType;

    @ApiModelProperty(value = "派工状态::0:未派工，1::已派工", dataType = "Integer")
    private Integer assignStatus;
    @ApiModelProperty(value = "毛坯:: 0锻件,1铸件,2钢锭", dataType = "String")
    private String workblankType;
    @ApiModelProperty(value = "记录状态 0 未生成记录，3已生成记录， 1 审核通过,2 审核未通过", dataType = "String")
    private String recordStatus;
    @ApiModelProperty(value = "0:未派工,-1 = 未开工 1= 已开工 2 = 已完工", dataType = "String")
    private String furnaceStatus;

}
