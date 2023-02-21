package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:21
 */
@Data
@ApiModel(value = "计划管理")
public class Plan extends BaseEntity<Plan> {

    private static final long serialVersionUID = -1472432735506772177L;

    @ApiModelProperty(value = "项目号")
    private String projectNo;

    @ApiModelProperty(value = "计划编号")
    private String projCode;

    @ApiModelProperty(value = "工作号")
    private String workNo;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单交货日期")
    private Date orderDeliveryDate;

    @ApiModelProperty(value = "关联项目BOM的id")
    private String projectBom;

    @ApiModelProperty(value = "关联项目BOM的workno")
    private String projectBomWork;

    @ApiModelProperty(value = "关联项目BOM的名称")
    private String projectBomName;

    @ApiModelProperty(value = "bom分组选择")
    private String projectBomGroup;

    @ApiModelProperty(value = "图号")
    private String drawNo;

    @ApiModelProperty(value = "计划数量")
    private Integer projNum = 0;

    @ApiModelProperty(value = "库存数量")
    private Integer storeNumber = 0;

    @ApiModelProperty(value = "在制数量")
    private Integer processNum = 0;

    @ApiModelProperty(value = "交付数量")
    private Integer deliveryNum = 0;

    @ApiModelProperty(value = "缺件数量")
    private Integer missingNum = 0;

    @ApiModelProperty(value = "跟单数量")
    private Integer trackHeadNumber = 0;

    @ApiModelProperty(value = "单机数量")
    private Integer singleNumber = 0;

    @ApiModelProperty(value = "已完成跟单数量")
    private Integer trackHeadFinishNumber = 0;

    @ApiModelProperty(value = "跟单数量")
    private Integer optNumber = 0;

    @ApiModelProperty(value = "已完成跟单数量")
    private Integer optFinishNumber = 0;

    @ApiModelProperty(value = "排序号")
    private Integer sortNo;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "优先级 0低 1中 2高 ")
    private String priority;

    @ApiModelProperty(value = "计划类型 1新制  2 返修")
    private Integer projType;

    @ApiModelProperty(value = "所属机构")
    private String branchCode;

    @ApiModelProperty(value = "加工车间")
    private String inchargeOrg;

    @ApiModelProperty(value = "加工车间名称")
    private String inchargeOrgName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "预警状态")
    private Integer alarmStatus;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "图号名称")
    private String drawNoName;

    @ApiModelProperty(value = "加工类型")
    private String drawNoType;

    @ApiModelProperty(value = "材质")
    private String texture;

    @ApiModelProperty(value = "原计划id")
    private String originalPlanId;

    @ApiModelProperty(value = "原计划编号")
    private String originalProjCode;


    @ApiModelProperty(value = "总台数")
    private Integer totalNumber = 0;

    @ApiModelProperty(value = "毛坯")
    private String blank;

    @ApiModelProperty(value = "编制人员")
    private String prepareBy;

    @ApiModelProperty(value = "批准人")
    private String approvalBy;

    @ApiModelProperty(value = "批准时间")
    private Date approvalTime;

    @ApiModelProperty(value = "审核人")
    private String auditBy;

    @ApiModelProperty(value = "物料承制单位")
    private String materialProductionUnit;

    @ApiModelProperty(value = "铆焊承制单位")
    private String rivetingWeldingUnit;

    @ApiModelProperty(value = "装配承制单位")
    private String assemblyContractorUnit;

    @ApiModelProperty(value = "总装承制单位")
    private String finalAssemblyContractorUnit;

    @TableField(exist = false)
    @ApiModelProperty(value = "工艺状态")
    private Integer processStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "已拆分数量")
    private Integer plannedNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "跟单ids")
    private List<String> trackHeadIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否导入")
    private String isExport;

    /**
     * -------------计划表扩展字段-----------------------------
     */
    
    @TableField(exist = false)
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @TableField(exist = false)
    @ApiModelProperty(value = "实样数量")
    private Integer sampleNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品名称")
    private String productName;

    @TableField(exist = false)
    @ApiModelProperty(value = "重量")
    private String weight;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求日期")
    private Date demandTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "提单人")
    private String submitBy;

    @TableField(exist = false)
    @ApiModelProperty(value = "提单单位")
    private String submitOrderOrg;

    @TableField(exist = false)
    @ApiModelProperty(value = "提单日期")
    private Date submitOrderTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "单重KG")
    private String pieceWeight;

    @TableField(exist = false)
    @ApiModelProperty(value = "钢水KG")
    private String steelWaterWeight;
}
