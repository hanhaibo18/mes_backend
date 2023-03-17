package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * WMS出库信息冲销上传MES
 * reverse_output_database_cover_upload
 */
@Data
public class ReverseOutputDatabaseCoverUpload implements Serializable {
    /**
     * 参考单类型 MES领料单
     */
    private String referenceListType;

    /**
     * 参考单ID 唯一, MES领料单
     */
    private String referenceListId;

    /**
     * 参考单号 
     */
    private String referenceListNo;

    /**
     * 出库类型 MES领料单出库
     */
    private String outboundType;

    /**
     * 出库单ID 
     */
    private String outboundListId;

    /**
     * 出库单号 
     */
    private String outboundNo;

    /**
     * 明细列表
     */
    @TableField(exist = false)
    private List<ReverseOutputDatabaseCoverUploadLineList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}