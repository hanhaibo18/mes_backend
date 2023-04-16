package com.richfit.mes.common.model.wms;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * material_basis MES物料基础数据同步
 */
@Data
public class MaterialBasis implements Serializable {

    /**
     * 工厂 泵业/热工等
     */
    private String workCode;

    /**
     * 物料编码
     */
    private String materialNum;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 关键件 是/否
     */
    private String crucialFlag;

    /**
     * 跟踪方式 单件/批次
     */
    private String trackingMode;

    /**
     * 材质 为零件材料
     */
    private String partsMaterial;

    /**
     * 规格
     */
    private String spec;

    /**
     * 单重
     */
    private String singleWeight;

    /**
     * 实物配送 是/否
     */
    private String deliveryFlag;

    /**
     * 制造类型 外购、外协、自制
     */
    private String produceType;

    /**
     * 物料类型 毛坯、成品、半成品
     */
    private String materialType;

    /**
     * 车间
     */
    private String workshop;

    /**
     * 预留字段1
     */
    private String field1;

    /**
     * 预留字段2
     */
    private String field2;

    /**
     * 预留字段3
     */
    private String field3;

    /**
     * 预留字段4
     */
    private String field4;

    /**
     * 预留字段5
     */
    private String field5;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}