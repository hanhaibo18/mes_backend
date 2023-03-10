package com.richfit.mes.common.model.wms;
import lombok.Data;

/**
 * @author LLh
 * @Description MES系统物料基础
 */
@Data
public class MaterialBasis {

    /**
     * 工厂  泵业/热工等
     */
    private String factory;

    /**
     * 物料编码
     */
    private String matterCode;

    /**
     * 物料描述
     */
    private String matterDescribe;

    /**
     * 计量单位
     */
    private String unitMeasurement;

    /**
     * 关键件  是/否
     */
    private Integer keyPart;

    /**
     * 跟踪方式  单件/批次
     */
    private Integer trackingMethod;

    /**
     * 材质  为零件材料
     */
    private String materialQuality;

    /**
     * 规格
     */
    private String spec;

    /**
     * 单重
     */
    private Float singleWeight;

    /**
     * 实物配送  是/否
     */
    private Integer physicalDistribution;

    /**
     * 制造类型  外购、外协、自制
     */
    private Integer makeType;

    /**
     * 物料类型  毛坯、成品、半成品
     */
    private Integer matterType;

    /**
     * 预留字段1
     */
    private String reserve;

    /**
     * 预留字段2
     */
    private String reserveTwo;

    /**
     * 预留字段3
     */
    private String reserveThree;

    /**
     * 预留字段4
     */
    private String reserveFour;

    /**
     * 预留字段5
     */
    private String reserveFive;

}
