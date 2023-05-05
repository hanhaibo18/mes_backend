package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * WMS报检单上传MES
 * reverse_inspection_doc_upload
 */
@Data
public class ReverseInspectionDocUpload implements Serializable {
    /**
     * 报检单ID 唯一
     */
    private String id;

    /**
     * 报检单号 
     */
    private String number;

    /**
     * 业务类型 外购产品/外协产品
     */
    private String businessType;

    /**
     * 工厂 
     */
    private String workCode;

    /**
     * 库存地点 
     */
    private String invCode;

    /**
     * 采购订单 
     */
    private String purchaseOrder;

    /**
     * 行项目 
     */
    private String lineItem;

    /**
     * 合同号 
     */
    private String contractNo;

    /**
     * 供应商编码 
     */
    private String supplierCode;

    /**
     * 供应商编码
     */
    private String supplierName;

    /**
     * 到货日期 
     */
    private String arrivalDate;

    /**
     * 物料编码 
     */
    private String materialNo;

    /**
     * 物料描述 
     */
    private String materialDesc;

    /**
     * 计量单位 
     */
    private String unit;

    /**
     * 报检数量 
     */
    private BigDecimal inspectionQuantity;

    /**
     * 产品编号 
     */
    private String productNo;

    /**
     * 工作号 
     */
    private String workNo;

    /**
     * 炉号/批次号 
     */
    private String heatNoOrBatchNo;

    /**
     * 外协工序 外协
     */
    private String process;

    /**
     * 下工序 外协
     */
    private String nextOpt;

    /**
     * 验收准则 外协，200字符
     */
    private String acceptanceCriteria;

    /**
     * 备注 
     */
    private String remark;

    /**
     * 报检单创建人 
     */
    private String modifyBy;

    /**
     * 通知单创建日期 
     */
    private String createTime;

    @TableField(exist = false)
    private List<ReverseInspectionLineList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}