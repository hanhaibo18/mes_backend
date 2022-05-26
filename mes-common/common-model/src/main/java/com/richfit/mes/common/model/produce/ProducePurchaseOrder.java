package com.richfit.mes.common.model.produce;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: xinYu.hou
 * @Date: 2022/1/10 10:57
 **/
@Data
@Accessors(chain = true)
@TableName("produce_purchase_order")
public class ProducePurchaseOrder extends BaseEntity<ProducePurchaseOrder> {

    private String orderNo;

    private String materialNo;

    private String materialRemark;

    private String drawingNo;

    private Integer number;

    private Date purchaseDate;

    private Date deliveryDate;

    private String projectNo;

    private String materialCode;

    private String orderType;

    private String unit;

    private String lgort;

    private String lifnr;

    private String isSubmit;

    private String isMaterial;

    private String description;

    private String tenantId;

    private String branchCode;

    private String priority;

    private static final long serialVersionUID = 1L;
}
