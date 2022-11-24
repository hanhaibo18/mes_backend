package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/11 8:46
 */
@Data
@ApiModel(value = "订单管理")
public class Order extends BaseEntity<Order> {

    private static final long serialVersionUID = 4043017035595029309L;
    @ApiModelProperty(value = "订单号")
    private String orderSn;

    @ApiModelProperty(value = "物料编号")
    private String materialCode;

    @ApiModelProperty(value = "订单数量")
    private Integer orderNum;

    @ApiModelProperty(value = "下单日期")
    private Date orderDate;

    @ApiModelProperty(value = "交付日期")
    private Date deliveryDate;

    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

    @ApiModelProperty(value = "优先级  高 中 低")
    private String priority;

    @ApiModelProperty(value = "工厂编码")
    private String branchCode;

    @ApiModelProperty(value = "负责车间")
    private String inChargeOrg;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "物料描述")
    private String materialDesc;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂名称")
    private String branchName;

    @TableField(exist = false)
    @ApiModelProperty(value = "负责车间名称")
    private String inchargeOrgName;

    @TableField(exist = false)
    @ApiModelProperty(value = "已计划数量")
    private Integer projNum;

    @ApiModelProperty(value = "已交数量")
    private Integer storeNum;

    @ApiModelProperty(value = "控制者")
    private String controller;

    @ApiModelProperty(value = "生产状态（0未开始，1以开始，2已完成）")
    private Integer production;
}
