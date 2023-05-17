package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * material_requisition_upload  MES领料单上传WMS
 */
@Data
public class MaterialRequisitionUpload implements Serializable {
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
     * 要求配送时间 第2次推送
       格式：yyyyMMddHHmmSS
     */
    private String deliveryDate;

    /**
     * 产品名称
     */
    private String prodName;

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
}