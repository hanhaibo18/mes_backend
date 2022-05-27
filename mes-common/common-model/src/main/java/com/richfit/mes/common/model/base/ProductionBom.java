package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 王瑞
 * @Description 物料
 */

@Data
public class ProductionBom extends BaseEntity<ProductionBom> {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户Id", required = true, dataType = "String")
    private String tenantId;

    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码", required = true, dataType = "String")
    private String branchCode;

    /**
     * 上级产品图号
     */
    @ApiModelProperty(value = "上级产品图号", dataType = "String")
    private String mainDrawingNo;

    /**
     * 产品图号
     */
    @ApiModelProperty(value = "产品图号", dataType = "String")
    private String drawingNo;

    /**
     * 物料编码
     */
    @ApiModelProperty(value = "物料编码", dataType = "String")
    private String materialNo;

    /**
     * 物料描述
     */
    @TableField(exist = false)
    private String materialDesc;

    /**
     * 产品名称
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 状态 0:待发布 1:已发布 2:停用
     */
    @ApiModelProperty(value = "状态 0:待发布 1:已发布 2:停用", dataType = "String")
    private String status;

    /**
     * 材质
     */
    @ApiModelProperty(value = "材质", dataType = "String")
    private String texture;

    /**
     * 重量
     */
    @ApiModelProperty(value = "重量", dataType = "Float")
    private Float weight;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位", dataType = "String")
    private String unit;

    /**
     * 产品图片
     */
    @ApiModelProperty(value = "产品图片", dataType = "String")
    private String productImage;

    /**
     * 产品编号来源
     */
    @ApiModelProperty(value = "产品编号来源", dataType = "String")
    private String productSource;

    /**
     * 是否关键件
     */
    @ApiModelProperty(value = "是否关键件", dataType = "String")
    private String isKeyPart;

    /**
     * 是否仓储领料
     */
    @ApiModelProperty(value = "是否仓储领料", dataType = "String")
    private String isNeedPicking;

    /**
     * 实物配送区分
     */
    @ApiModelProperty(value = "实物配送区分", dataType = "String")
    private String isEdgeStore;

    /**
     * 是否齐套检查
     */
    @ApiModelProperty(value = "是否齐套检查", dataType = "String")
    private String isCheck;

    /**
     * 级别
     */
    @ApiModelProperty(value = "级别", dataType = "String")
    private String grade;

    /**
     * 产品附件
     */
    @ApiModelProperty(value = "产品附件", dataType = "String")
    private String productFile;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号", dataType = "String")
    private String versionNo;

    /**
     * 产品类型
     */
    @TableField(exist = false)
    private String productType;

    @TableField(exist = false)
    private String objectType;

    @ApiModelProperty(value = "跟单类型?", dataType = "String")
    private String trackType;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer number;

    /**
     * 发布日期
     */
    @ApiModelProperty(value = "发布日期", dataType = "Date")
    private Date publishTime;

    /**
     * 发布人
     */
    @ApiModelProperty(value = "发布人", dataType = "String")
    private String publishBy;

    @TableField(exist = false)
    private Product product;

    /**
     * 是否当前版本 0否 1是
     */
    @ApiModelProperty(value = "是否当前版本 0否 1是", dataType = "String")
    private String isCurrent;

    /**
     * 工序ID
     */
    @ApiModelProperty(value = "工序ID", dataType = "String")
    public String optId;

    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称", dataType = "String")
    public String optName;

    public String isNumFrom;

    public String bomKey;

    public String sourceType;

    public Integer orderNo;

    @TableField(exist = false)
    private String isImport;

    public String prodDesc;
}
