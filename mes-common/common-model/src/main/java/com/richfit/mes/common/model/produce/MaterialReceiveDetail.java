package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
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
    private String deliveryNo;

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
    private int quantity;

    /**
     * 申请数量
     */
    private int orderQuantity;

    /**
     * 批次号
     */
    private String batchNum;

    /**
     * 接收时间
     */
    private String receiveDate;

    /**
     * 状态
     */
    private String state;

    /**
     * 图号
     */
    @TableField(exist = false)
    private String drawingNo;

    /**
     * 跟单号
     */
    @TableField(exist = false)
    private String trackNo;


}
