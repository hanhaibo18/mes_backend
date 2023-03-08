package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.heat.CompleteUserInfoDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 马峰
 * @Description 报工实体类
 * @CreateTime: 2022年02月02日 08:08:00
 * @ModifyTime: 2022年08月15日 12:08:00
 */
@Data
public class TrackComplete extends BaseEntity<TrackComplete> {

    private static final long serialVersionUID = 2731220432204410300L;

    public static final Integer IS_FINAL_STEP = 1;

    public static final Integer YES_IS_CURRENT = 1;

    public static final Integer NO_IS_CURRENT = 0;
    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 跟单工序项ID
     */
    protected String tiId;
    /**
     * 跟单工序项ID
     */
    protected String assignId;
    /**
     * 完工用户ID
     */
    protected String userId;
    /**
     * 完工用户ID
     */
    protected String userName;
    /**
     * 跟单ID
     */
    protected String trackId;
    /**
     * 跟单ID
     */
    protected String trackNo;
    /**
     * 产品编号
     */
    protected String prodNo;
    /**
     * 外协单号
     */
    protected String outNo;
    /**
     * 完工设备ID
     */
    protected String deviceId;
    /**
     * 完工设备ID
     */
    protected String deviceName;
    /**
     * 完工数量
     */
    protected Double completedQty;
    /**
     * 拒绝数量
     */
    protected Double rejectQty;

    /**
     * 完工工时
     */
    protected Double completedHours;
    /**
     * 实际工时
     */
    protected Double actualHours;
    /**
     * 报告工时
     */
    protected Double reportHours;
    /**
     * 静态工时
     */
    protected Double staticHours;
    /**
     * 报工人
     */
    //报工人
    protected String completeBy;

    /**
     * 报工时间
     */
    protected Date completeTime;
    /**
     * 质检人
     */
    protected String qualityCheckBy;
    /**
     * 质检车间?
     */
    protected String qualityCheckBranch;
    /**
     * 备注
     */
    protected String remarks;


    //北石新增报工字段
    /**
     * 实用固定机时
     */
    protected Double actualFixHours;
    /**
     * 实用变动机时（正常班）
     */
    protected Double actualNomalHours;

    /**
     * 实用变动机时（加班）
     */
    protected Double actualOverHours;
    /**
     * 完成固定机时
     */
    protected Double completedFixHours;
    /**
     * 完成变动机时
     */
    protected Double completedChangeHours;
    /**
     * 单件补付机时
     */
    protected Double singleAddHours;
    /**
     * 辅助工时
     */
    protected Double auxiliaryHours;

    @ApiModelProperty(value = "步骤", dataType = "Integer")
    protected String step;
    @ApiModelProperty(value = "炉批号", dataType = "Integer")
    protected String furnaceNo;
    @ApiModelProperty(value = "是否最后一道工序", dataType = "Integer")
    protected Integer isFinalStep;
    @ApiModelProperty(value = "水温℃", dataType = "Integer")
    protected Double waterTempera;
    @ApiModelProperty(value = "炉温℃", dataType = "Integer")
    protected Double furnaceTempera;
    @ApiModelProperty(value = "介质温度℃", dataType = "Integer")
    protected Double neurogenTempera;
    @ApiModelProperty(value = "油温℃", dataType = "Integer")
    protected Double oilTempera;
    @ApiModelProperty(value = "炉冷min", dataType = "Integer")
    protected Double furnaceCool;
    @ApiModelProperty(value = "空冷min", dataType = "Integer")
    protected Double vacancyCool;
    @ApiModelProperty(value = "水冷min", dataType = "Integer")
    protected Double waterCool;
    @ApiModelProperty(value = "油冷min", dataType = "Integer")
    protected Double oilCool;
    @ApiModelProperty(value = "介质冷min", dataType = "Integer")
    protected Double neurogenCool;
    @ApiModelProperty(value = "KR7280℃", dataType = "Integer")
    protected Double krTempera;
    @ApiModelProperty(value = "KR7280min", dataType = "Integer")
    protected Double krCool;
    @ApiModelProperty(value = "实际干活时间", dataType = "String")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd mm:dd:ss")
    protected Date actualWorkTime;
    @ApiModelProperty(value = "是否当前步骤", dataType = "Integer")
    protected Integer isCurrent;
    @ApiModelProperty(value = "预装炉id", dataType = "String")
    protected String prechargeFurnaceId;
    @ApiModelProperty(value = "步骤分组id", dataType = "String")
    protected String stepGroupId;
    @TableField(exist = false)
    @ApiModelProperty(value = "标准工时", dataType = "")
    protected BigDecimal heatHour;


    /**
     * 探伤结果
     */
    protected String detectionResult;
    @TableField(exist = false)
    private String workNo;
    @TableField(exist = false)
    private String drawingNo;
    @TableField(exist = false)
    private Integer trackType;
    @TableField(exist = false)
    private Integer trackQty;
    @TableField(exist = false)
    private String trackNo2;
    @TableField(exist = false)
    private String productNo;


    @TableField(exist = false)
    private String optCode;
    @TableField(exist = false)
    private String optName;
    @TableField(exist = false)
    private Integer optType;
    @TableField(exist = false)
    private String optNo;
    @TableField(exist = false)
    private Integer optSequence;
    @TableField(exist = false)
    private Integer technologySequence;
    @TableField(exist = false)
    private Integer optParallelType;
    @TableField(exist = false)
    private Integer sequenceOrderBy;
    @TableField(exist = false)
    private Double prepareEndHours;
    @TableField(exist = false)
    private Double singlePieceHours;

    @ApiModelProperty(value = "是否修改", dataType = "Integer")
    @TableField(exist = false)
    private Integer isUpdate;

    @ApiModelProperty(value = "产品名称", dataType = "String")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "是否给予准结工时", dataType = "int")
    private int isPrepare;

    @ApiModelProperty(value = "质检结果", dataType = "String")
    @TableField(exist = false)
    private String qualityResult;

    @ApiModelProperty(value = "订单编号", dataType = "String")
    @TableField(exist = false)
    private String productionOrder;

    @ApiModelProperty(value = "optId", dataType = "String")
    @TableField(exist = false)
    private String optId;


    /**
     * 工时查询总工时
     */
    @ApiModelProperty(value = "总工时", dataType = "Double")
    @TableField(exist = false)
    protected Double totalHours;

    @TableField(exist = false)
    @ApiModelProperty(value = "叶子结点数据", dataType = "List<TrackComplete>")
    private List<TrackComplete> trackCompleteList;
    /**
     * 叶子结点数据
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否叶子结点", dataType = "Boolean")
    private Boolean isLeafNodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "步骤人员信息", dataType = "List<Map>")
    private List<CompleteUserInfoDto> userInfos;

    @ApiModelProperty(value = "optId", dataType = "String")
    @TableField(exist = false)
    private String parentId;
}
