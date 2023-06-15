package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackFlow;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 申请单上传  产品编号明细列表
 *  applyLineProductList
 */
@Data
public class ApplyLineProductList implements Serializable {

    /**
     *  MES申请单行id
     */
    private String applyLineId;

    /**
     *  产品编号
     */
    private String productNum;

    /**
     *  数量
     */
    private Integer quantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public ApplyLineProductList() {

    }

    public ApplyLineProductList(Certificate certificate, TrackFlow trackFlow) {
        // id
        this.applyLineId = certificate.getId();
        // 产品编号
        this.productNum = trackFlow.getProductNo();
        // 产品数量
        this.quantity = trackFlow.getNumber();
    }
}
