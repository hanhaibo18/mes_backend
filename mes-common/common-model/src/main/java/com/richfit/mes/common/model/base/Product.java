package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 王瑞
 * @Description 物料
 * <p>
 * 20220429 gl 增加API注解，方便前端阅读接口文件
 */

@Data
public class Product extends BaseEntity<Product> {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "所属租户", required = true)
    private String tenantId;

    /**
     * 物料编码
     */
    @ApiModelProperty(value = "物料编码", required = true)
    private String materialNo;

    /**
     * 物料日期
     */
    @ApiModelProperty(value = "物料日期")
    private Date materialDate;

    /**
     * 物料类型 0铸件 1锻件 2精铸件 3成品/半成品
     */
    @ApiModelProperty(value = "物料类型 0铸件 1锻件 2精铸件 3成品/半成品")
    private String materialType;

    /**
     * 物料描述
     */
    @ApiModelProperty(value = "物料描述")
    private String materialDesc;

    /**
     * 图号
     */
    @ApiModelProperty(value = "图号")
    private String drawingNo;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /**
     * 材质
     */
    @ApiModelProperty(value = "材质")
    private String texture;

    /**
     * 重量
     */
    @ApiModelProperty(value = "重量")
    private Float weight;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 换算比例
     */
    @ApiModelProperty(value = "换算比例")
    private Float convertScale;

    /**
     * 换算单位
     */
    @ApiModelProperty(value = "换算单位")
    private String convertUnit;

    /**
     * 组织结构编码
     */
    @ApiModelProperty(value = "组织结构编码")
    private String branchCode;

    /**
     * 类型 0：自制 1：外购 2：外协
     */
    @ApiModelProperty(value = "类型 0：自制 1：外购 2：外协")
    private String objectType;

    @TableField(exist = false)
    private Integer haveRouter;

    @TableField(exist = false)
    private Integer haveBom;

    @TableField(exist = false)
    private Integer routerType;

    @TableField(exist = false)
    private String materialTypeName;

    @TableField(exist = false)
    private BigDecimal qty;
}
