package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * WMS入库信息冲销上传MES
 * reverse_input_database_cover_upload
 */
@Data
public class ReverseInputDatabaseCoverUpload implements Serializable {
    /**
     * 参考单类型 外购产品报检单、外协产品报检单、MES申请单
     */
    private String referenceListType;

    /**
     * 参考单ID 唯一, MES申请单ID或报检单ID
     */
    private String referenceListId;

    /**
     * 参考单号 
     */
    private String referenceListNo;

    /**
     * 订单编号 MES申请单关联的生产订单  外购/外协报检单关联的采购订单
     */
    private String orderNo;

    /**
     * 入库类型 MES申请单入库/外购报检单入库/外协报检单入库
     */
    private String materialType;

    /**
     * 入库单ID 
     */
    private String materialId;

    /**
     * 入库单号 
     */
    private String materialNo;

    /**
     * 明细列表
     */
    @TableField(exist = false)
    private List<ReverseInputDatabaseCoverLineList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}