package com.kld.mes.plm.entity.dto;

import lombok.Data;

/**
 * pdm 物料数据
 */
@Data
public class MaterialDto {

    /**
     *  图号
     */
    private String drawingNo;
    /**
     *  版本
     */
    private String itemRev;
    /**
     *  名称
     */
    private String name;
    /**
     *  记录人
     */
    private String createBy;
    /**
     *  发布状态
     */
    private String itemStatus;
    /**
     *  发布时间
     */
    private String releaseTime;
    /**
     *  重量
     */
    private String weight;
    /**
     *  材质
     */
    private String texture;
    /**
     *  材料编码（新增）
     */
    private String alloyNo;
    /**
     *  产品名称
     */
    private String productName;
    /**
     *  物料类型
     */
    private String materialType;
    /**
     *  是否关键件
     */
    private String isKeyPart;
    /**
     *  类型
     */
    private String objectType;
    /**
     *  SAP物料编码（外购件）
     */
    private String materialNo;
    /**
     *  规格
     */
    private String specification;
    /**
     *  附件
     */
    private String previewUrl;

}
