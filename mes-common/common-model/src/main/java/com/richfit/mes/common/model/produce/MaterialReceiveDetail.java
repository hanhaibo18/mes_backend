package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/7/29 17:42
 */
@Data
public class MaterialReceiveDetail extends BaseEntity<MaterialReceiveDetail> {

    /**
     * 申请单号
     */
    private String aplyNum;

    /**
     * 配送单号
     */
    private String delieveryNo;

    /**
     * 名称
     */
    private String name;

    /**
     * 物料号
     */
    private String materialNum;

    /**
     * 单位
     */
    private String unit;

    /**
     * 配送数量
     */
    private String quantity;

    /**
     * 申请数量
     */
    private String orderQuantity;

    /**
     * 批次号
     */
    private String batchNum;


}
