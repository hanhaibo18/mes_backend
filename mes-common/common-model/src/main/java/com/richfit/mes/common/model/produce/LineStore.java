package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.code.StoreInputTypeEnum;
import com.richfit.mes.common.model.sys.Attachment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 线边库
 */
@Data
public class LineStore extends BaseEntity<LineStore> {

    private static final long serialVersionUID = -1820038423448467701L;
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "计划编号")
    private String trackNo;

    @ApiModelProperty(value = "物料编号")
    private String materialNo;

    @ApiModelProperty(value = "图号")
    private String drawingNo;

    @ApiModelProperty(value = "状态 0完工（初始入库） 1在制 2作废 3已消耗")
    private String status;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "合格证编号")
    private String certificateNo;

    @ApiModelProperty(value = "物料来源")
    private String materialSource;

    @ApiModelProperty(value = "炉批号")
    private String batchNo;

    @ApiModelProperty(value = "工作号")
    private String workNo;

    @ApiModelProperty(value = "毛坯编号")
    private String workblankNo;

    @ApiModelProperty(value = "物料类型 0毛坯 1半成品/成品")
    private String materialType;

    @ApiModelProperty(value = "跟踪方式 0单件 1批次")
    private String trackType;

    @ApiModelProperty(value = "已使用数量")
    private Integer useNum;

    @ApiModelProperty(value = "")
    @TableField(exist = false)
    private String assemblyId;

    @ApiModelProperty(value = "入库时间")
    private Date inTime;

    @ApiModelProperty(value = "出库时间")
    private Date outTime;

    @ApiModelProperty(value = "试棒数量")
    private Integer testBarNumber;

    @ApiModelProperty(value = "试棒类型")
    private String testBarType;

    @ApiModelProperty(value = "生产订单编号")
    private String productionOrder;

    @ApiModelProperty(value = "采购订单编号")
    private String purchaseOrder;

    @ApiModelProperty(value = "物料描述")
    private String materialDesc;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "材质")
    private String texture;

    @ApiModelProperty(value = "重量")
    private Float weight;

    @ApiModelProperty(value = "合同编号")
    private String contractNo;

    @ApiModelProperty(value = "代用材料")
    private String replaceMaterial;

    @ApiModelProperty(value = "预先派工")
    private String beforehandAssigned;

    @ApiModelProperty(value = "上工序跟单编号")
    private String prevTrackNum;

    @ApiModelProperty(value = "是否上传ERP")
    private String isSendErp;

    @ApiModelProperty(value = "是否投料ERP")
    private String isFeedErp;

    @ApiModelProperty(value = "料单类型  0 常规  1 自动")
    private String stockType;

    @ApiModelProperty(value = "产品编号")
    private String prodNo;

    @ApiModelProperty(value = "所属分厂")
    private String branchCode;

    @ApiModelProperty(value = "合格证文件ID")
    private String certificateId;

    @ApiModelProperty(value = "合格证备注")
    private String certificateRemark;

    @ApiModelProperty(value = "钢材质证书ID")
    private String steelQualityId;

    @ApiModelProperty(value = "钢材质证书备注")
    private String steelQualityRemark;

    @ApiModelProperty(value = "理化报告ID")
    private String physicochemicalIndexesId;

    @ApiModelProperty(value = "理化报告备注")
    private String physicochemicalIndexesRemark;

    @ApiModelProperty(value = "钢锭化学成分表ID")
    private String ingotId;

    @ApiModelProperty(value = "钢锭化学成分表备注")
    private String ingotRemark;

    @ApiModelProperty(value = "录入类型 0 手动录入 1 合格证来料接收  2 系统自动生成  3 配送接收")
    private String inputType;

    @TableField(exist = false)
    @ApiModelProperty(value = "对应资料的上传Id列表")
    private List<String> fileIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "对应资料附件列表")
    private List<Attachment> storeAttachRel;


    public LineStore() {
    }

    public LineStore(LineStore lineStore) {
        this.id = lineStore.id;
        this.tenantId = lineStore.tenantId;
        this.trackNo = lineStore.trackNo;
        this.materialNo = lineStore.materialNo;
        this.drawingNo = lineStore.drawingNo;
        this.status = lineStore.status;
        this.number = lineStore.number;
        this.certificateNo = lineStore.certificateNo;
        this.materialSource = lineStore.materialSource;
        this.batchNo = lineStore.batchNo;
        this.workNo = lineStore.workNo;
        this.workblankNo = lineStore.workblankNo;
        this.materialType = lineStore.materialType;
        this.trackType = lineStore.trackType;
        this.useNum = lineStore.useNum;
        this.assemblyId = lineStore.assemblyId;
        this.inTime = lineStore.inTime;
        this.outTime = lineStore.outTime;
        this.testBarNumber = lineStore.testBarNumber;
        this.testBarType = lineStore.testBarType;
        this.productionOrder = lineStore.productionOrder;
        this.purchaseOrder = lineStore.purchaseOrder;
        this.materialDesc = lineStore.materialDesc;
        this.materialName = lineStore.materialName;
        this.productName = lineStore.productName;
        this.texture = lineStore.texture;
        this.weight = lineStore.weight;
        this.contractNo = lineStore.contractNo;
        this.replaceMaterial = lineStore.replaceMaterial;
        this.beforehandAssigned = lineStore.beforehandAssigned;
        this.prevTrackNum = lineStore.prevTrackNum;
        this.isSendErp = lineStore.isSendErp;
        this.isFeedErp = lineStore.isFeedErp;
        this.stockType = lineStore.stockType;
        this.prodNo = lineStore.prodNo;
        this.branchCode = lineStore.branchCode;
        this.inputType = lineStore.inputType;
    }

    public LineStore(Certificate certificate, TrackCertificate tc) {

        this.materialNo = certificate.getMaterialNo();
        this.drawingNo = certificate.getDrawingNo();
        this.certificateNo = certificate.getCertificateNo();
        this.materialSource = certificate.getBranchCode();

        this.batchNo = tc.getBatchNo();
        this.number = tc.getNumber();
        this.workblankNo = tc.getProductNo();

        //半成品 成品
        this.materialType = "1";
        this.trackType = "0";
        this.useNum = 0;
        this.inTime = new Date();

        this.testBarNumber = certificate.getTestBarNumber();
        this.testBarType = certificate.getTestBarType();
        this.productionOrder = null;

        this.productName = certificate.getProductName();
        this.texture = certificate.getTexture();
        this.weight = certificate.getWeight();

        this.isSendErp = "0";
        this.isFeedErp = "0";
        this.stockType = "0";
        this.branchCode = certificate.getNextOptWork();
        this.inputType = StoreInputTypeEnum.CERT_ACCEPT.getCode();
    }

    public LineStore(MaterialReceiveDetail materialReceiveDetail, String branchCode) {

        this.materialNo = materialReceiveDetail.getMaterialNum();
        this.drawingNo = materialReceiveDetail.getDrawingNo();
        this.certificateNo = null;
        this.materialSource = branchCode;
        this.status = "0";
        this.batchNo = null;
        this.number = materialReceiveDetail.getQuantity();
        this.workblankNo = null;

        //半成品 成品
        this.materialType = "1";
        this.trackType = "0";
        this.useNum = 0;
        this.inTime = new Date();

        this.testBarNumber = null;
        this.testBarType = null;
        this.productionOrder = null;

        this.productName = null;
        this.materialName = materialReceiveDetail.getName();
        this.materialDesc = materialReceiveDetail.getName();
        this.texture = null;
        this.weight = null;

        this.isSendErp = "0";
        this.isFeedErp = "0";
        this.stockType = "0";
        this.branchCode = branchCode;
        this.inputType = StoreInputTypeEnum.WMS_SEND.getCode();

    }

}
