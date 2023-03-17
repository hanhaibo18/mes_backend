package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * WMS出库信息冲销上传MES(明细列表)
 * reverse_output_database_cover_upload_line_list
 */
@Data
public class ReverseOutputDatabaseCoverUploadLineList implements Serializable {
    /**
     * 参考单行项目ID 
     */
    private String lineItemId;

    /**
     * 参考单行号 
     */
    private String lineNumber;

    /**
     * 出库单行ID 
     */
    private String outboundLineId;

    /**
     * 出库单行项目 
     */
    private String outboundLineItem;

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
    private List<ReverseOutputDatabaseCoverUploadProductList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}