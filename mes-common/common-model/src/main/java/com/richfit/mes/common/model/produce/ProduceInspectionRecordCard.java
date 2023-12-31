package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

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

    @ApiModelProperty(value = "质量检测卡号")
    private String cardNo;

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

    @ApiModelProperty(value = "炉批号")
    private String batchNo;

    @ApiModelProperty(value = "检验记录卡生成状态  Y已生成 N未为生成")
    private String isCardData;

    @ApiModelProperty(value = "状态 同跟单管理状态")
    private String status;

    @ApiModelProperty(value = "跟单分类：1机加  2装配 3热处理 4钢结构")
    private String classes;

    @TableField(exist = false)
    @ApiModelProperty(value = "详细明细")
    private List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList;

    public static ProduceInspectionRecordCard byTrackHead(ProduceInspectionRecordCard produceInspectionRecordCard, TrackHead trackHead) {
        if (produceInspectionRecordCard == null) {
            produceInspectionRecordCard = new ProduceInspectionRecordCard();
        }
        produceInspectionRecordCard.setId(trackHead.getFlowId());
        produceInspectionRecordCard.setTenantId(trackHead.getTenantId());
        produceInspectionRecordCard.setBranchCode(trackHead.getBranchCode());
        produceInspectionRecordCard.setTrackHeadId(trackHead.getId());
        produceInspectionRecordCard.setSparePartsNo(trackHead.getProductNo());
        produceInspectionRecordCard.setSparePartsName(trackHead.getProductName());
        produceInspectionRecordCard.setSparePartsDrawingNo(trackHead.getDrawingNo());
        produceInspectionRecordCard.setTexture(trackHead.getTexture());
        produceInspectionRecordCard.setBatchNo(trackHead.getBatchNo());
        produceInspectionRecordCard.setIsCardData(trackHead.getIsCardData());
        produceInspectionRecordCard.setStatus(trackHead.getStatus());
        produceInspectionRecordCard.setClasses(trackHead.getClasses());
        return produceInspectionRecordCard;
    }
}
