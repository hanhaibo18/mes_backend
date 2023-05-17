package com.richfit.mes.common.model.produce;

import java.io.Serializable;
import java.util.Date;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * produce_hot_demand
 *
 * @author
 */
@Data
public class HotDemand extends BaseEntity<HotDemand> implements Serializable {

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称", dataType = "String")
    private String projectName;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;

    /**
     * 图号
     */
    @ApiModelProperty(value = "图号 ", dataType = "String")
    private String drawNo;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称 ", dataType = "String")
    private String demandName;

    /**
     * ERP物料编码
     */
    @ApiModelProperty(value = "ERP物料编码 ", dataType = "String")
    private String erpProductCode;

    /**
     * 材质
     */
    @ApiModelProperty(value = " 材质", dataType = "String")
    private String texture;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量 ", dataType = "Integer")
    private Integer num;

    /**
     * 毛坯类型 0锻件,1铸件,2钢锭
     */
    @ApiModelProperty(value = "毛坯类型 0锻件,1铸件,2钢锭 ", dataType = "String")
    private String workblankType;

    /**
     * 加工单位id
     */
    @ApiModelProperty(value = " 加工单位id", dataType = "String")
    private String inchargeOrgId;

    /**
     * 加工单位
     */
    @ApiModelProperty(value = "加工单位 ", dataType = "String")
    private String inchargeOrg;

    /**
     * 计划完成时间
     */
    @ApiModelProperty(value = "计划完成时间 ", dataType = "Date")
    private Date planEndTime;

    /**
     * 需求日期
     */
    @ApiModelProperty(value = "需求日期 ", dataType = "Date")
    private Date demandTime;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号 ", dataType = "String")
    private String versionNum;

    /**
     * 提单日期
     */
    @ApiModelProperty(value = "提单日期 ", dataType = "Date")
    private Date submitOrderTime;

    /**
     * 提单单位id
     */
    @ApiModelProperty(value = "提单单位id ", dataType = "String")
    private String submitOrderOrgId;

    /**
     * 提单单位
     */
    @ApiModelProperty(value = "提单单位 ", dataType = "String")
    private String submitOrderOrg;

    /**
     * 提单人id
     */
    @ApiModelProperty(value = "提单人id ", dataType = "String")
    private String submitById;

    /**
     * 提单人
     */
    @ApiModelProperty(value = "提单人 ", dataType = "String")
    private String submitBy;


    /**
     * 提报状态 0 :未提报  1 :已提报
     */
    @ApiModelProperty(value = "提报状态 0 :未提报  1 :已提报 ", dataType = "Integer")
    private Integer submitState;

    /**
     * 删除标识  0 :未删除  1 :已删除
     */
    @ApiModelProperty(value = "删除标识  0 :未删除  1 :已删除 ", dataType = "Integer")
    private Integer isDelete;

    /**
     * 排产状态 0: 未排产   1 :已排产
     */
    @ApiModelProperty(value = "排产状态 0: 未排产   1 :已排产 ", dataType = "Integer")
    private Integer produceState;

    /**
     * 生产批准状态 0 :未批准 ,1 已批准
     */
    @ApiModelProperty(value = "生产批准状态 0 :未批准 ,1 已批准 ", dataType = "Integer")
    private Integer produceRatifyState;

    /**
     * 凭证号
     */
    @ApiModelProperty(value = " 凭证号", dataType = "String")
    private String voucherNo;

    /**
     * 行号
     */
    @ApiModelProperty(value = "行号 ", dataType = "Integer")
    private Integer rowNum;

    /**
     * 库存数量
     */
    @ApiModelProperty(value = "库存数量 ", dataType = "Integer")
    private Integer repertoryNum;

    /**
     * 计划数量
     */
    @ApiModelProperty(value = "计划数量 ", dataType = "Integer")
    private Integer planNum;

    /**
     * 生产部门
     */
    @ApiModelProperty(value = "生产部门", dataType = "String")
    private String produceOrg;

    /**
     * 下发时间
     */
    @ApiModelProperty(value = "下发时间 ", dataType = "Date")
    private Date issueTime;

    /**
     * 是否为长周期 0 :否  1 是
     */
    @ApiModelProperty(value = "是否为长周期 0 :否  1 是 ", dataType = "Integer")
    private Integer isLongPeriod;

    /**
     * 是否有模型 0 :否  1 是',
     */
    @ApiModelProperty(value = "是否有模型 0 :否  1 是', ", dataType = "Integer")
    private Integer isExistModel;

    /**
     * 是否有工艺 0 :否  1 是',
     */
    @ApiModelProperty(value = "是否有工艺 0 :否  1 是', ", dataType = "Integer")
    private Integer isExistProcess;

    /**
     * 是否为外协件:0 :否  1 是',
     */
    @ApiModelProperty(value = "是否为外协件:0 :否  1 是', ", dataType = "Integer")
    private Integer isOutsource;

    /**
     * 是否有库存 0 :否  1 是',
     */
    @ApiModelProperty(value = " 是否有库存 0 :否  1 是',", dataType = "Integer")
    private Integer isExistRepertory;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构", dataType = "String")
    private String branchCode;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", dataType = "String")
    private String tenantId;

    @ApiModelProperty(value = "是否生成过关键计划节点0 未生成  1已生成", dataType = "Integer")
    private Integer isExistPlanNode;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级: 高 ,中, 低", dataType = "String")
    private String priority;

    @ApiModelProperty(value = "单重KG")
    private String pieceWeight;

    @ApiModelProperty(value = "钢水KG")
    private String steelWaterWeight;

    @ApiModelProperty(value = "重量")
    private String weight;

    @ApiModelProperty(value = "计划id")
    private String planId;

    @ApiModelProperty(value = "模型车间计划id")
    private String planIdModel;

    @ApiModelProperty(value = "交付数量", dataType = "Integer")
    private Integer deliveryNum = 0;

    @ApiModelProperty(value = "锭型")
    private String ingotCase;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "凭证号")
    private String evidenceNo;
    private static final long serialVersionUID = 1L;
}