package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 产品交接单据
 *
 * @TableName base_product_connect
 */
@TableName(value = "base_product_connect")
@Data
public class BaseProductConnect implements Serializable {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 交接单号
     */
    private String connectNo;

    /**
     * 配套钻机
     */
    private String driNo;

    /**
     * 工作号
     */
    private String workNo;

    /**
     * 产品图号
     */
    private String drawNo;

    /**
     * 项目bom
     */
    private String bomId;

    /**
     * 产品编号
     */
    private String productNo;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 校验员
     */
    private String checkUser;

    /**
     * 校验日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkDate;

    /**
     * 提交人
     */
    private String createBy;

    /**
     * 车间编码
     */
    private String branchCode;

    /**
     * 验收人
     */
    private String receiveUser;

    /**
     * 验收单位：默认钻机分公司
     */
    private String receiveUnit;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}