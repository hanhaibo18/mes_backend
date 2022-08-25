package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhiqiang.lu
 */
@Data
@Accessors(chain = true)
@TableName("produce_inspection_record_card")
@ApiModel(value = "质量检验记录卡")
public class ProduceInspectionRecordCard extends BaseEntity<ProduceInspectionRecordCard> {

    private static final long serialVersionUID = -1044825101675722165L;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "跟单id")
    private String trackHeadId;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品图号")
    private String productDrawingNo;

    @ApiModelProperty(value = "部件名称")
    private String partsName;

    @ApiModelProperty(value = "部件图号")
    private String partsDrawingNo;

    @ApiModelProperty(value = "零件编号")
    private String sparePartsNo;

    @ApiModelProperty(value = "零件名称")
    private String sparePartsName;

    @ApiModelProperty(value = "零件图号")
    private String sparePartsDrawingNo;

    @ApiModelProperty(value = "材质")
    private String texture;
}
