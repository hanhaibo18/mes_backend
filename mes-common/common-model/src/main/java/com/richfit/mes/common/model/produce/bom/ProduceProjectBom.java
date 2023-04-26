package com.richfit.mes.common.model.produce.bom;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhiqiang.lu
 * @Description 项目BOM
 */

@TableName("produce_project_bom")
@Data
public class ProduceProjectBom extends BaseEntity<ProduceProjectBom> {

    @ApiModelProperty(value = "租户Id", required = true, dataType = "String")
    private String tenantId;

    @ApiModelProperty(value = "机构编码", required = true, dataType = "String")
    private String branchCode;

    @ApiModelProperty(value = "项目名称", required = true, dataType = "String")
    private String projectName;
    @ApiModelProperty(value = "工作号", required = true, dataType = "String")
    private String workPlanNo;
    @ApiModelProperty(value = "上级产品图号", dataType = "String")
    private String mainDrawingNo;

    @ApiModelProperty(value = "产品图号", dataType = "String")
    private String drawingNo;

    @ApiModelProperty(value = "物料编码", dataType = "String")
    private String materialNo;

    @ApiModelProperty(value = "材质", dataType = "String")
    private String texture;

    @ApiModelProperty(value = "重量", dataType = "Float")
    private Float weight;

    @ApiModelProperty(value = "单位", dataType = "String")
    private String unit;

    @ApiModelProperty(value = "是否是编号来源", dataType = "String")
    private String isNumFrom;

    @ApiModelProperty(value = "是否关键件", dataType = "String")
    private String isKeyPart;

    @ApiModelProperty(value = "是否仓储领料", dataType = "String")
    private String isNeedPicking;

    @ApiModelProperty(value = "实物配送区分", dataType = "String")
    private String isEdgeStore;

    @ApiModelProperty(value = "是否齐套检查", dataType = "String")
    private String isCheck;

    @ApiModelProperty(value = "级别", dataType = "String")
    private String grade;


    @ApiModelProperty(value = "跟单类型?", dataType = "String")
    private String trackType;

    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer number;

    private String bomKey;

    @ApiModelProperty(value = "来料类型", dataType = "Integer")
    private String sourceType;

    @ApiModelProperty(value = "导入序号", dataType = "Integer")
    private Integer orderNo;

    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String prodDesc;

    @ApiModelProperty(value = "分组", dataType = "String")
    private String bomGrouping;

    @ApiModelProperty(value = "分组Id", dataType = "String")
    private String groupId;

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

    @ApiModelProperty(value = "配料工序名", dataType = "String")
    private String optName;

    @ApiModelProperty(value = "发布状态(0未发布,1发布)", dataType = "Integer")
    private Integer publishState;
}
