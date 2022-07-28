package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: IngredientApplicationDto.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月26日 15:50:00
 */
@Data
public class IngredientApplicationDto {
    @ApiModelProperty(value = "申请单号", dataType = "String")
    private String sqd;
    @ApiModelProperty(value = "工厂编码", dataType = "String")
    private String gc;
    @ApiModelProperty(value = "车间Id", dataType = "String")
    private String cj;
    @ApiModelProperty(value = "车间名称", dataType = "String")
    private String cjName;
    @ApiModelProperty(value = "工位ID", dataType = "String")
    private String gw;
    @ApiModelProperty(value = "工位名称", dataType = "String")
    private String gwName;
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String gx;
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String gxName;
    @ApiModelProperty(value = "生产订单编号", dataType = "String")
    private String scdd;
    @ApiModelProperty(value = "跟单Id", dataType = "String")
    private String gd;
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String cp;
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String cpName;
    @ApiModelProperty(value = "优先级", dataType = "int")
    private int yxj;
    @ApiModelProperty(value = "派工时间(yyyyMMddHHmmSS)", dataType = "String")
    private String pgsj;
    @ApiModelProperty(value = "申请物料信息", dataType = "String")
    private List<LineList> lineList;
}
