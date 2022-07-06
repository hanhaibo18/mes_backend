package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 马峰
 * @Description 工艺
 */
@Data
public class Sequence extends BaseEntity<Sequence> {


    private static final long serialVersionUID = 2195891974936492858L;
    /**
     * 工序ID
     */
    @ApiModelProperty(value = "工艺Id", dataType = "String")
    private String routerId;
    /**
     * 工序顺序
     */
    @ApiModelProperty(value = "工序顺序", dataType = "int")
    private int optOrder;
    /**
     * 工序顺序
     */
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;
    /**
     * 工序顺序
     */
    @ApiModelProperty(value = "工序类型", dataType = "String")
    private String optType;
    /**
     * 工序编码
     */
    @ApiModelProperty(value = "工序编码", dataType = "String")
    private String optCode;

    /**
     * 准结工时
     */
    @ApiModelProperty(value = "准结工时", dataType = "int")
    private int prepareEndHours;
    /**
     * 额定工时
     */
    @ApiModelProperty(value = "额定工时", dataType = "int")
    private int singlePieceHours;
    /**
     * 技术要求
     */
    @ApiModelProperty(value = "技术要求", dataType = "String")
    private String technologySequence;
    /**
     * 是否质检
     */
    @ApiModelProperty(value = "是否质检", dataType = "String")
    private String isQualityCheck;
    /**
     * 是否调度
     */
    @ApiModelProperty(value = "是否调度", dataType = "String")
    private String isScheduleCheck;
    /**
     * 是否并行
     */
    @ApiModelProperty(value = "是否并行", dataType = "String")
    private String isParallel;
    /**
     * 是否并行
     */
    @ApiModelProperty(value = "是否自动分配", dataType = "String")
    private String isAutoAssign;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态", dataType = "String")
    private String status;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;
    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码", dataType = "String")
    private String branchCode;
    /**
     * 工序ID
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String optId;
    /**
     * 下个工序
     */
    @ApiModelProperty(value = "下工序", dataType = "int")
    private int optNextOrder;

    /**
     * 是否有工序图（PDM新增）
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String drawing;

    /**
     * 工序类型PDM
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String type;


    /**
     * 工序序号pdm
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String op_no;


    /**
     * 关/重/试件（PDM新增）
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String gzs;


    /**
     * 工序内容（PDM新增）
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String content;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String versionCode;


    /**
     * 注意事项
     */
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String notice;
}
