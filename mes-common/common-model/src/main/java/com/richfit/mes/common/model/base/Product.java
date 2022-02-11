package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author 王瑞
 * @Description 物料
 */

@Data
public class Product extends BaseEntity<Product> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 物料编码
     */
    private String materialNo;

    /**
     * 物料日期
     */
    private Date materialDate;

    /**
     * 物料类型 0铸件 1锻件 2精铸件 3成品/半成品
     */
    private String materialType;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 材质
     */
    private String texture;

    /**
     * 重量
     */
    private Float weight;

    /**
     * 单位
     */
    private String unit;

    /**
     * 换算比例
     */
    private Float convertScale;

    /**
     * 换算单位
     */
    private String convertUnit;

    /**
     * 组织结构编码
     */
    private String branchCode;

    /**
     * 类型 0：自制 1：外购 2：外协
     */
    private String objectType;

    @TableField(exist = false)
    private Integer haveRouter;

    @TableField(exist = false)
    private Integer haveBom;

    @TableField(exist = false)
    private Integer routerType;

    @TableField(exist = false)
    private String materialTypeName;
}
