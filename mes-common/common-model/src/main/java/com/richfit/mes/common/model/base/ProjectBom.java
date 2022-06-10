package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 侯欣雨
 * @Description 项目BOM
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProjectBom extends BaseEntity<ProjectBom> {

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

    @ApiModelProperty(value = "项目名称", required = true, dataType = "String")
    private String projectName;

    @ApiModelProperty(value = "工作号", required = true, dataType = "String")
    private String workPlanNo;

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


    private String bomKey;

    @ApiModelProperty(value = "来料类型", dataType = "Integer")
    private String sourceType;

    @ApiModelProperty(value = "导入序号", dataType = "Integer")
    private Integer orderNo;

    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String prodDesc;

    @ApiModelProperty(value = "分组", dataType = "Integer")
    private Integer groupBy;
    @ApiModelProperty(value = "是否分解", dataType = "String")
    private String isResolution;
    @ApiModelProperty(value = "状态(0停用,1发布)", dataType = "String")
    private String state;
    @TableField(exist = false)
    @ApiModelProperty(value = "零件所属图号", dataType = "String")
    private String byDrawingNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "等级", dataType = "String")
    private String level;
}
