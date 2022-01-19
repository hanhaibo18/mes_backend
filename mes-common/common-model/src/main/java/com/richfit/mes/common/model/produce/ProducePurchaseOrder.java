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
public class ProducePurchaseOrder{

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String orderNo;

    private String materialNo;

    private String materialRemark;

    private String drawingNo;

    private Integer number;

    private Date purchaseDate;

    private Date deliveryDate;

    private Date changeOn;

    private String changeBy;

    private String werks;

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

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    protected String createBy;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    protected Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String modifyBy;

    /**
     * 更新日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date modifyTime;

    /**
     * 备注字段
     */
    protected String remark;

    private static final long serialVersionUID = 1L;
}
