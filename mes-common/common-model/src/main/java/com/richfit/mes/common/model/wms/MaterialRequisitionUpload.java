package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackHead;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 
 * material_requisition_upload  MES领料单上传WMS
 */
@Data
public class MaterialRequisitionUpload implements Serializable {

    /**
     * 单据类型：自动入库
     */
    public static final String AUTO_TYPE = "自动类型";
    /**
     * 单据类型：正常
     */
    public static final String NORMAL_TYPE = "正常";

    /**
     * MES领料单ID 唯一
     */
    private String id;

    /**
     * MES领料单编号
     */
    private String applyNum;

    /**
     * 工厂
     */
    private String workCode;

    /**
     * 单据类型 正常/自动出库
     */
    private String transType;

    /**
     * 车间  车间跟库存地点对应
     */
    private String workshop;

    /**
     * 库存地点
     */
    private String invCode;

    /**
     * 生产订单
     */
    private String prodNo;

    /**
     * 工作号
     */
    private String jobNo;

    /**
     * 跟单号
     */
    private String documentNo;

    /**
     * 计划号
     */
    private String prodNum;

    /**
     * 计划ID
     */
    private String prodId;

    /**
     * 工位名称  第2次推送，下出库指令
     */
    private String station;

    /**
     * 要求配送时间
     */
    private Date assignTime;

    /**
     * 要求配送时间 第2次推送
       格式：yyyyMMddHHmmSS
     */
    private Date deliveryDate;

    /**
     * 产品名称
     */
    private String prodDesc;

    /**
     * 产品图号
     */
    private String mainDrawingNo;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建日期
     */
    private String createTime;

    /**
     * 明细列表
     */
    @TableField(exist = false)
    private List<RequisitionLineList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



    public MaterialRequisitionUpload(Assign assign, TrackHead trackHead, List<TrackAssembly> trackAssemblyList, String tenantErpCode, String applyNum) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        this.id = uuid;
        // 领料单编号
        this.applyNum = applyNum;
        // 工厂
        this.workCode = tenantErpCode;
        // 单据类型
        this.transType = NORMAL_TYPE;
        // 车间
        this.workshop = trackHead.getBranchCode();
        // 库存地点
        this.invCode = null;
        // 生产订单
        this.prodNo = trackHead.getProductionOrder();
        // 工作号
        this.jobNo = trackHead.getWorkNo();
        // 跟单号
        this.documentNo = trackHead.getTrackNo();
        // 计划号
        this.prodNum = trackHead.getWorkPlanNo();
        // 计划ID
        this.prodId = trackHead.getWorkPlanId();
        // 工位名称
        this.station = null;
        // 派工时间
        this.assignTime = assign.getAssignTime();
        // 要求配送时间
        this.deliveryDate = null;
        // 项目名称
        this.prodDesc = trackHead.getProductName();
        // 产品图号
        this.mainDrawingNo = trackHead.getDrawingNo();
        // 项目名称
        this.projectName = trackHead.getProductName();
        // 创建人
        this.createBy = trackHead.getCreateBy();
        // 创建日期
        this.createTime = String.valueOf(new Date());
        this.lineList = new ArrayList<>(trackAssemblyList.size());

        int init = 0;
        for (TrackAssembly trackAssembly: trackAssemblyList) {
            init ++;
            RequisitionLineList requisitionLine = new RequisitionLineList(trackHead,trackAssembly,uuid,init);
            lineList.add(requisitionLine);
        }

    }
}