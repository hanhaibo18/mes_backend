package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 产品交接单据接收汇总
 *
 * @author wangchenyu
 * @TableName base_product_receipt_detail
 */
@TableName(value = "base_product_receipt_detail")
@Data
public class BaseProductReceiptDetail implements Serializable {
    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 交接单Id
     */
    @ApiModelProperty(value = "交接单Id")
    private String connectId;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "工作号")
    private String workNo;

    /**
     * 产品图号
     */
    @ApiModelProperty(value = "产品图号")
    private String drawNo;

    /**
     * 零部件图号
     */
    @ApiModelProperty(value = "零部件图号")
    private String partDrawingNo;

    /**
     * 零部件名称
     */
    @ApiModelProperty(value = "零部件名称")
    private String partName;

    /**
     * 配送日期
     */
    @ApiModelProperty(value = "配送日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date receiveDate;

    /**
     * 需求数量
     */
    @ApiModelProperty(value = "需求数量")
    private Integer demandNumber;

    /**
     * 接收数量
     */
    @ApiModelProperty(value = "接收数量")
    private Integer deliverNumber;

    /**
     * 交接单数量
     */
    @ApiModelProperty(value = "交接单数量")
    private Integer number;

    /**
     * 是否齐套
     */
    @ApiModelProperty(value = "是否齐套::1::是；2：：否")
    @TableField(exist = false)
    private Integer isKitting;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}