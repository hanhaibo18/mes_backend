package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.model.produce.TrackAssembly;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * requisition_line_info_list 产品编号明细列表
 */
@Data
public class RequisitionLineInfoList implements Serializable {
    /**
     * MES领料单行id
     */
    private String applyLineId;

    /**
     * 产品编号
     */
    private String productNum;

    /**
     * 数量
     */
    private String quantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public RequisitionLineInfoList(TrackAssembly trackAssembly,String uuid) {
        this.applyLineId = uuid;
        this.productNum = trackAssembly.getProductNo();
        this.quantity = String.valueOf(trackAssembly.getNumber());
    }
}