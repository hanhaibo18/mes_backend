package com.tc.mes.plm.entity.request;

import lombok.Data;

/**
 * pdm 物料数据
 */
@Data
public class MaterialRequest {
    /**
     *  图号
     */
    private String drawing_no;
    /**
     *  版本
     */
    private String item_rev;
    /**
     *  名称
     */
    private String name;
    /**
     *  记录人
     */
    private String create_by;
    /**
     *  发布状态
     */
    private String item_status;
    /**
     *  发布时间
     */
    private String release_time;
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
    private String alloy_no;
    /**
     *  产品名称
     */
    private String product_name;
    /**
     *  物料类型
     */
    private String material_type;
    /**
     *  是否关键件
     */
    private String is_key_part;
    /**
     *  类型
     */
    private String object_type;
    /**
     *  SAP物料编码（外购件）
     */
    private String material_no;
    /**
     *  规格
     */
    private String specification;
    /**
     *  附件（未标注）
     */
    private String preview_url;
}
