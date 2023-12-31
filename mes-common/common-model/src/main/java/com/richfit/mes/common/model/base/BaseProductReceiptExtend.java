package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品交接单-扩展表
 *
 * @author wcy
 * @TableName base_product_connect_extend
 */
@TableName(value = "base_product_receipt_extend")
@Data
public class BaseProductReceiptExtend implements Serializable {
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
     * BOMId
     */
    @ApiModelProperty(value = "BOMId")
    private String bomId;

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
     * 物料编码
     */
    @ApiModelProperty(value = "物料编码")
    private String materialNo;

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
     * 本次送货数量,对应物料接收单配送数量
     */
    @ApiModelProperty(value = "本次送货数量,对应物料接收单配送数量")
    private Integer deliverNumber;

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
     * 是否选中：1：选中；2：未选中
     **/
    @ApiModelProperty(value = "是否选中：1：选中；2：未选中")
    private Integer isCheck;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String extend;

    /**
     * BOM零部件数量
     */
    @ApiModelProperty(value = "BOM零部件数量")
    @TableField(exist = false)
    private Integer bomDemandNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}