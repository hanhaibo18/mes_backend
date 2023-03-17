package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 入库明细列表
 * reverse_input_database_line_list
 */
@Data
public class ReverseInputDatabaseLineList implements Serializable {
    /**
     * 参考单行项目ID 
     */
    private String lineItemId;

    /**
     * 参考单行号 
     */
    private String lineNumber;

    /**
     * 订单编号行项目 MES申请单关联的生产订单行项目
     */
    private String orderNoLineItem;

    /**
     * 物料编码 外购/外协报检单关联的采购订单行项目
     */
    private String materialNo;

    /**
     * 物料描述 
     */
    private String materialDesc;

    /**
     * 单位 
     */
    private String unit;

    /**
     * 实收数量 
     */
    private String fquantity;

    /**
     * 工作号 
     */
    private String workNo;

    /**
     * 入库单行ID 
     */
    private String receiptlineId;

    /**
     * 入库单行项目 
     */
    private String receiptlineItem;

    /**
     * 产品编号明细列表
     */
    private List<ReverseInputDatabaseProductList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}