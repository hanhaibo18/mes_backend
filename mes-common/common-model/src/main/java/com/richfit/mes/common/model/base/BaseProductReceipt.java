package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品交接单据
 *
 * @author wangchenyu
 *
 * @TableName base_product_connect
 */
@TableName(value = "base_product_receipt")
@Data
public class BaseProductReceipt implements Serializable {
    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 交接单号
     */
    @ApiModelProperty(value = "交接单号")
    private String connectNo;

    /**
     * 配套钻机
     */
    @ApiModelProperty(value = "配套钻机")
    private String driNo;

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
     * 项目bom
     */
    @ApiModelProperty(value = "项目bom")
    private String bomId;

    /**
     * 项目bom名称
     */
    @ApiModelProperty(value = "项目bom名称")
    private String bomName;

    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号")
    private String productNo;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String prodDesc;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;

    /**
     * 校验员
     */
    @ApiModelProperty(value = "校验员")
    private String checkUser;

    /**
     * 校验日期
     */
    @ApiModelProperty(value = "校验日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkDate;

    /**
     * 提交人
     */
    @ApiModelProperty(value = "提交人")
    private String createBy;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String modifyBy;

    /**
     * 车间编码
     */
    @ApiModelProperty(value = "车间编码")
    private String branchCode;

    /**
     * 验收人
     */
    @ApiModelProperty(value = "验收人")
    private String receiveUser;

    /**
     * 验收单位：默认钻机分公司
     */
    @ApiModelProperty(value = "验收单位：默认钻机分公司")
    private String receiveUnit;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date modifyDate;

    /**
     * 交接状态：0：待接受；1：已接受；2：已拒收
     */
    @ApiModelProperty(value = "交接状态：0：待接受；1：已接受；2：已拒收")
    private String status;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}