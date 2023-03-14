package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 
 * material_requisition_upload  MES领料单上传WMS
 */
@Data
public class MaterialRequisitionUpload {
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
    private Integer transType;

    /**
     * 车间  车间跟库存地点对应
     */
    private String workshop;

    /**
     * 生产订单
     */
    private String prodNum;

    /**
     * 工作号
     */
    private String jobNo;

    /**
     * 跟单号
     */
    private String trackNo;

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
     * 创建人
     */
    private String createBy;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 明细列表
     */
    @TableField(exist = false)
    private List<RequisitionLineList> lineList;
}