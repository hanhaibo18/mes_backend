package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/11 8:46
 */
@Data
public class Order extends BaseEntity<Order> {

    private String orderSn;
    private String materialCode;
    private int orderNum;
    private Date orderDate;
    private Date deliveryDate;
    private int orderType;
    private String priority;
    private String branchCode;
    private String inchargeOrg;
    private int status;
    private String tenantId;
    private String materialDesc;

    @TableField(exist = false)
    private String branchName;

    @TableField(exist = false)
    private String inchargeOrgName;

    @TableField(exist = false)  //已计划数量
    private int projNum;

    @TableField(exist = false)  //已交数量
    private int storeNum;
}
