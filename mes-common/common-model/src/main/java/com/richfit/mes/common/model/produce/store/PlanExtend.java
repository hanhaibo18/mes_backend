package com.richfit.mes.common.model.produce.store;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * @Author: hujia
 * @Date: 2023/2/15 9:21
 */
@Data
@ApiModel(value = "计划管理")
public class PlanExtend {

    private static final long serialVersionUID = -1472432735506772167L;
    @TableId(type = IdType.ASSIGN_UUID)
    protected String id;

    @ApiModelProperty(value = "生产计划id")
    private String planId;

    @ApiModelProperty(value = "需求表id")
    private String demandId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "实样数量")
    private Integer sampleNum;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "重量")
    private String weight;

    @ApiModelProperty(value = "需求日期")
    private Date demandTime;

    @ApiModelProperty(value = "提单人")
    private String submitBy;

    @ApiModelProperty(value = "提单单位")
    private String submitOrderOrg;

    @ApiModelProperty(value = "提单日期")
    private Date submitOrderTime;

    @ApiModelProperty(value = "单重KG")
    private String pieceWeight;

    @ApiModelProperty(value = "钢水KG")
    private String steelWaterWeight;

    @ApiModelProperty(value = "ERP物料编码")
    private String erpProductCode;

    @ApiModelProperty(value = "锭型")
    private String ingotCase;
    @ApiModelProperty(value = "毛坯类型 0锻件,1铸件,2钢锭")
    private String workblankType;
    @ApiModelProperty(value = "加工车间 ", dataType = "String")
    private String inchargeWorkshop;

    @ApiModelProperty(value = "加工车间名称 ", dataType = "String")
    private String inchargeWorkshopName;
}
