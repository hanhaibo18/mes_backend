package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * WMS出库信息上传MES(明细列表)
 * reverse_output_database_line_list
 */
@Data
public class ReverseOutputDatabaseLineList implements Serializable {
    /**
     * 参考单ID
     */
    private String referenceListId;

    /**
     * 参考单行项目ID
     */
    private String lineItemId;

    /**
     * 参考单行号
     */
    private String lineNumber;

    /**
     * 物料编码
     */
    private String materialCode;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 单位
     */
    private String unit;

    /**
     * 实发数量
     */
    private String actualQuantity;

    /**
     * 出库单行ID
     */
    private String outboundLineId;

    /**
     * 出库单行项目
     */
    private String outboundLineItem;

    /**
     * 工厂
     */
    private String workCode;

    /**
     * 库房
     */
    private String invCode;

    /**
     * 产品编号明细列表
     */
    @TableField(exist = false)
    private List<ReverseOutputDatabaseProductList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}