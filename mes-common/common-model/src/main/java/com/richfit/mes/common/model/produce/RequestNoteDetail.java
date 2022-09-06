package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * produce_request_note_detail
 *
 * @author
 */
@Data
public class RequestNoteDetail extends BaseEntity<RequestNoteDetail> {
    /**
     * 申请单Id
     */
    @ApiModelProperty(value = "申请单Id")
    private String noteId;

    /**
     * 物料号
     */
    @ApiModelProperty(value = "物料号")
    private String materialNo;

    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    /**
     * 图号
     */
    @ApiModelProperty(value = "图号")
    private String drawingNo;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Double number;

    /**
     * 原因
     */
    @ApiModelProperty(value = "原因")
    private String reason;

    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    private String reasonExplain;

    /**
     * 是否需要领料
     */
    @ApiModelProperty(value = "是否需要领料")
    private String isNeedPicking;

    /**
     * 是否是关键件
     */
    @ApiModelProperty(value = "是否是关键件")
    private String isKeyPart;

    /**
     * 是否线边库
     */
    @ApiModelProperty(value = "是否线边库")
    private String isEdgeStore;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "车间编码")
    private String branchCode;

    /**
     * 所属租户
     */
    @ApiModelProperty(value = "租户")
    private String tenantId;

}
