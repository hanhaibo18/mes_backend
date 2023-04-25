package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.wms.ApplyLineList;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * produce_request_note
 *
 * @author
 */
@Data
public class RequestNote extends BaseEntity<RequestNote> {

    private static final long serialVersionUID = -4730255354271675350L;
    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "跟单Id")
    private String trackHeadId;

    /**
     * 跟单工序Id
     */
    @ApiModelProperty(value = "跟单工序Id")
    private String trackItemId;

    @ApiModelProperty(value = "跟单号")
    private String trackNo;

    /**
     * 申请单号
     */
    @ApiModelProperty(value = "申请单号")
    private String requestNoteNumber;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "车间编码")
    private String branchCode;

    /**
     * 所属租户
     */
    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    /**
     * 申请数量
     */
    @ApiModelProperty(value = "数量")
    @TableField(exist = false)
    private Double number = 0.00;

    /**
     * 配送数量
     */
    @ApiModelProperty(value = "配送数量")
    @TableField(exist = false)
    private Double numberDelivery = 0.00;

    /**
     * 申请单Id
     */
    @ApiModelProperty(value = "申请单Id")
    @TableField(exist = false)
    private String noteId;

    /**
     * 物料号
     */
    @ApiModelProperty(value = "物料号")
    @TableField(exist = false)
    private String materialNo;

    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    @TableField(exist = false)
    private String materialName;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    @TableField(exist = false)
    private String unit;

    /**
     * 是否是关键件
     */
    @ApiModelProperty(value = "是否是关键件")
    @TableField(exist = false)
    private String isKeyPart;

    @ApiModelProperty(value = "工作号")
    @TableField(exist = false)
    private String workNo;

    @ApiModelProperty(value = "生产订单编号")
    @TableField(exist = false)
    private String productionOrder;

    /**
     * 行数据
     */
    @ApiModelProperty(value = "生产订单编号")
    @TableField(exist = false)
    private List<ApplyLineList> lineList;

}
