package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author 王瑞
 * @Description 线边库
 */
@Data
public class LineStore extends BaseEntity<LineStore> {

    /**
     * 租户ID
     */
    private String tenantId;

    private String trackNo;

    /**
     * 物料编号
     */
    private String materialNo;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * 状态 0在制 1完工 2作废 3已消耗
     */
    private String status;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 合格证编号
     */
    private String certificateNo;

    /**
     * 物料来源
     */
    private String materialSource;

    /**
     * 炉批号
     */
    private String batchNo;

    /**
     * 工作号
     */
    private String workNo;

    /**
     * 毛坯编号
     */
    private String workblankNo;

    /**
     * 物料类型 0毛坯 1半成品/成品
     */
    private String materialType;

    /**
     * 跟踪方式 0单件 1批次
     */
    private String trackType;

    /**
     * 已使用数量
     */
    private Integer userNum;

    @TableField(exist = false)
    private String assemblyId;

    /**
     * 入库时间
     */
    private Date inTime;

    /**
     * 出库时间
     */
    private Date outTime;

    /**
     * 试棒数量
     */
    private Integer testBarNumber;

    /**
     * 试棒类型
     */
    private String testBarType;
    /**
     * 生产订单编号
     */
    private String productionOrder;
    /**
     * 采购订单编号
     */
    private String purchaseOrder;
    /**
     * 物料描述
     */
    private String materialDesc;
    private String materialName;
    private String productName;
    private String texture;
    private Float weight;
    /**
     * 合同编号
     */
    private String contractNo;
    /**
     * 代用材料
     */
    private String replaceMaterial;
    /**
     * 预先派工
     */
    private String beforehandAssigned;
    /**
     * 上工序跟单编号
     */
    private String prevTrackNum;
    /**
     * 是否上传ERP
     */
    private String isSendErp;
    /**
     * 是否投料ERP
     */
    private String isFeedErp;
    /**
     * 料单类型  0 常规  1 自动
     */
    private String stockType;

    /**
     * 图号 产品编号
     */
    private String prodNo;

    private String branchCode;

    /**
     * 合格证文件ID
     */
    private String certificateId;

    /**
     * 合格证备注
     */
    private String certificateRemark;

    /**
     * 钢材质证书ID
     */
    private String steelQualityId;

    /**
     * 钢材质证书备注
     */
    private String steelQualityRemark;

    /**
     * 理化报告ID
     */
    private String physicochemicalIndexesId;
    /**
     * 理化报告备注
     */
    private String physicochemicalIndexesRemark;
    /**
     * 钢锭化学成分表ID
     */
    private String ingotId;
    /**
     * 钢锭化学成分表备注
     */
    private String ingotRemark;


    public LineStore() {
    }

    public LineStore(LineStore lineStore) {
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
        this.userNum = lineStore.userNum;
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
    }
}
