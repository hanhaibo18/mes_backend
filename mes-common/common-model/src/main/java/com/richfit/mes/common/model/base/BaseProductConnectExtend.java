package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品交接单-扩展表
 * @author wcy
 * @TableName base_product_connect_extend
 */
@TableName(value = "base_product_connect_extend")
@Data
public class BaseProductConnectExtend implements Serializable {
    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 交接单id
     */
    @ApiModelProperty(value = "交接单id")
    private String connectId;

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
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号")
    private String productNo;

    /**
     * 需求数量
     */
    @ApiModelProperty(value = "需求数量")
    private Integer demandNumber;

    /**
     * 累计送货数量
     */
    @ApiModelProperty(value = "累计送货数量")
    private Integer sendNumber;

    /**
     * 累计接受数量
     */
    @ApiModelProperty(value = "累计接受数量")
    private Integer receiveNumber;

    /**
     * 本次送货数量
     */
    @ApiModelProperty(value = "本次送货数量")
    private Integer number;

    /**
     * 已发数量
     */
    @ApiModelProperty(value = "已发数量")
    private Integer alreadyNumber;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 重量
     **/
    @ApiModelProperty(value = "重量")
    private String weight;

    /**
     * 来源
     **/
    @ApiModelProperty(value = "来源")
    private String source;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String extend;


    /**
     * BOM需求数量
     */
    @ApiModelProperty(value = "BOM需求数量")
    @TableField(exist = false)
    private Integer bomDemandNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}