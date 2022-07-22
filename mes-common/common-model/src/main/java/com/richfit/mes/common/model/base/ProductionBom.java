package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王瑞
 * @Description 物料
 */

@Data
@ApiModel(value = "产品BOM")
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
     * 是否是编号来源
     */
    @ApiModelProperty(value = "是否是编号来源", dataType = "String")
    private String isNumFrom;

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


    @ApiModelProperty(value = "跟单类型?", dataType = "String")
    private String trackType;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer number;

    public String bomKey;

    @ApiModelProperty(value = "来料类型", dataType = "Integer")
    public String sourceType;

    @ApiModelProperty(value = "导入序号", dataType = "Integer")
    public Integer orderNo;

    @ApiModelProperty(value = "零部件名称", dataType = "String")
    public String prodDesc;

    /**
     * 配料工序名
     */
    @ApiModelProperty(value = "配料工序名", dataType = "String")
    private String optName;
}
