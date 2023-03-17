package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 明细列表
 * reverse_input_database_cover_line_list
 */
@Data
public class ReverseInputDatabaseCoverLineList implements Serializable {
    /**
     * 参考单行项目ID 
     */
    private String lineItemId;

    /**
     * 参考单行号 
     */
    private String lineNumber;

    /**
     * 订单编号行项目 MES申请单关联的生产订单行项目      外购/外协报检单关联的采购订单行项目
     */
    private String orderNoLineItem;

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
     * 行项目 多个
     */
    private String lineItem;

    /**
     * 冲销数量 
     */
    private String offsetQuantity;

    /**
     * 产品编号明细列表
     */
    @TableField(exist = false)
    private List<ReverseInputDatabaseCoverProductList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}