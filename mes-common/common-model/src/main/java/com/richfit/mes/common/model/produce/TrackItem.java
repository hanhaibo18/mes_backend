package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 王瑞
 * @Description 跟单工序
 */
@Data
public class TrackItem extends BaseEntity<TrackItem> {

    private static final long serialVersionUID = -7319209148899298755L;
    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "跟单ID", dataType = "String")
    private String trackHeadId;
    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "工序字典表Id", dataType = "String")
    private String operatiponId;
    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNoFlow;
    /**
     * 设备ID
     */
    @ApiModelProperty(value = "设备ID", dataType = "String")
    private String deviceId;

    /**
     * 跟单分流Id
     */
    @ApiModelProperty(value = "分流Id", dataType = "String")
    private String flowId;

    /**
     * 工序版本号
     */
    @ApiModelProperty(value = "工序版本号", dataType = "String")
    private String optVer;
    /**
     * 工序名
     */
    @ApiModelProperty(value = "工序名", dataType = "String")
    private String optName;
    /**
     * 工序ID
     */
    @ApiModelProperty(value = "工序ID", dataType = "String")
    private String optId;

    /**
     * 工序号
     */
    @ApiModelProperty(value = "工序号", dataType = "String")
    private String optNo;
    /**
     * 工序类型
     */
    @ApiModelProperty(value = "工序类型", dataType = "String")
    private String optType;
    @ApiModelProperty(value = "subOptParentId", dataType = "String")
    private String subOptParentId;
    @ApiModelProperty(value = "是否并行", dataType = "Integer")
    private Integer optParallelType;
    /**
     * 准结时间
     */
    @ApiModelProperty(value = "准结时间", dataType = "Double")
    private Double prepareEndHours;
    /**
     * 单件工时
     */
    @ApiModelProperty(value = "单件工时", dataType = "Double")
    private Double singlePieceHours;
    @ApiModelProperty(value = "技术顺序", dataType = "String")
    private String technologySequence;
    @ApiModelProperty(value = "原工序顺序", dataType = "Integer")
    private Integer originalOptSequence;
    /**
     * 工序号
     */
    @ApiModelProperty(value = "工序号", dataType = "Integer")
    private Integer optSequence;
    /**
     * 下一道工序号
     */
    @ApiModelProperty(value = "下一道工序号", dataType = "Integer")
    private Integer nextOptSequence;
    /**
     * 工序排序号
     */
    @ApiModelProperty(value = "工序排序号", dataType = "Integer")
    private Integer sequenceOrderBy;
    @ApiModelProperty(value = "部分批次号", dataType = "Integer")
    private Integer partialBatchNo;
    /**
     * 返工次数
     */
    @ApiModelProperty(value = "返工次数", dataType = "Integer")
    private Integer reworkTimes;
    /**
     * 是否当前工序
     */
    @ApiModelProperty(value = "是否当前工序", dataType = "Integer")
    private Integer isCurrent = 0;
    /**
     * 是否开工
     */
    @ApiModelProperty(value = "是否开工(0 = 未开工 1= 已开工 2 = 已完工)", dataType = "Integer")
    private Integer isDoing;
    @ApiModelProperty(value = "开工人", dataType = "String")
    private String startDoingUser;
    /**
     * 开工时间
     */
    @ApiModelProperty(value = "开工时间", dataType = "Date")
    private Date startDoingTime;
    @ApiModelProperty(value = "当前工序是否报工", dataType = "Integer")
    private Integer isOperationComplete;
    /**
     * 完工时间
     */
    @ApiModelProperty(value = "完工时间", dataType = "Date")
    private Date operationCompleteTime;
    /**
     * 是否质检确认
     */
    @ApiModelProperty(value = "是否质检确认", dataType = "Integer")
    private Integer isExistQualityCheck;
    /**
     * 是否质检完成
     */
    @ApiModelProperty(value = "是否质检完成", dataType = "Integer")
    private Integer isQualityComplete;
    /**
     * 质检时间
     */
    @ApiModelProperty(value = "质检时间", dataType = "Date")
    private Date qualityCompleteTime;
    /**
     * 质检数量?
     */
    @ApiModelProperty(value = "质检数量?", dataType = "Integer")
    private Integer qualityQty;
    /**
     * 不合格数量?
     */
    @ApiModelProperty(value = "不合格数量?", dataType = "Integer")
    private Integer qualityUnqty;
    /**
     * 质检结果
     */
    @ApiModelProperty(value = "质检结果", dataType = "Integer")
    private Integer qualityResult;
    /**
     * 不合格步骤?
     */
    @ApiModelProperty(value = "不合格步骤", dataType = "String")
    private String failProcess;
    /**
     * 检验人
     */
    @ApiModelProperty(value = "检验人", dataType = "String")
    private String qualityCheckBy;
    /**
     * 检验车间
     */
    @ApiModelProperty(value = "检验车间", dataType = "String")
    private String qualityCheckBranch;

    /**
     * 是否调度确认
     */
    @ApiModelProperty(value = "是否调度确认", dataType = "Integer")
    private Integer isExistScheduleCheck;
    /**
     * 是否自动派工
     */
    @ApiModelProperty(value = "是否自动派工", dataType = "Integer")
    private Integer isAutoSchedule;
    @ApiModelProperty(value = "模型配送状态0已申请未配送1已配送 2已退库", dataType = "Integer")
    private Integer modelStatus;


    /**
     * 是否调度完成
     */
    @ApiModelProperty(value = "是否调度完成", dataType = "Integer")
    private Integer isScheduleComplete;

    @ApiModelProperty(value = "调度完成时间", dataType = "Date")
    private Date scheduleCompleteTime;

    @ApiModelProperty(value = "调度人", dataType = "String")
    private String scheduleCompleteBy;

    @ApiModelProperty(value = "调度意见", dataType = "String")
    private String scheduleCompleteResult;

    @ApiModelProperty(value = "是否最终完成", dataType = "String")
    private String isFinalComplete;

    @ApiModelProperty(value = "最终完成时间", dataType = "Date")
    private Date finalCompleteTime;

    @ApiModelProperty(value = "跟单顺序完成", dataType = "Integer")
    private Integer isTrackSequenceComplete;

    @ApiModelProperty(value = "eventId", dataType = "Integer")
    private Integer eventId;

    @ApiModelProperty(value = "可派工数量", dataType = "Integer")
    private Integer assignableQty;

    @ApiModelProperty(value = "工序完成数量", dataType = "Integer")
    private Double completeQty;

    @ApiModelProperty(value = "加热温度", dataType = "Integer")
    private Double heatTemperature;

    /**
     * 合格证编号 忽略非空判断
     */
    @ApiModelProperty(value = "合格证编号", dataType = "Integer")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String certificateNo;

    @ApiModelProperty(value = "是有效的", dataType = "Integer")
    private Integer isValid;

    @ApiModelProperty(value = "carbonDepth", dataType = "Double")
    private Double carbonDepth;

    @ApiModelProperty(value = "nitrogenDepth", dataType = "Double")
    private Double nitrogenDepth;

    @ApiModelProperty(value = "isCountPrepareHour", dataType = "String")
    private String isCountPrepareHour;

    @ApiModelProperty(value = "qualityCertificateDestination", dataType = "String")
    private String qualityCertificateDestination;

    @ApiModelProperty(value = "reserveText1", dataType = "String")
    private String reserveText1;

    @ApiModelProperty(value = "reserveText2", dataType = "String")
    private String reserveText2;

    @ApiModelProperty(value = "reserveText3", dataType = "String")
    private String reserveText3;

    @ApiModelProperty(value = "reserveText4", dataType = "String")
    private String reserveText4;

    @ApiModelProperty(value = "priority", dataType = "String")
    private String priority;

    @ApiModelProperty(value = "equipmentType", dataType = "String")
    private String equipmentType;

    @ApiModelProperty(value = "holdingTemperature", dataType = "Double")
    private Double holdingTemperature;

    @ApiModelProperty(value = "lowerLimits", dataType = "Double")
    private Double lowerLimits;

    @ApiModelProperty(value = "upperLimits", dataType = "Double")
    private Double upperLimits;

    @ApiModelProperty(value = "coolingDownMethod", dataType = "String")
    private String coolingDownMethod;

    @ApiModelProperty(value = "notice", dataType = "String")
    private String notice;

    @ApiModelProperty(value = "机构编码", dataType = "Integer")
    private String branchCode;

    @ApiModelProperty(value = "租户ID", dataType = "Integer")
    private String tenantId;


    @ApiModelProperty(value = "探伤结果", required = true)
    private Integer flawDetection;
    @ApiModelProperty(value = "探伤报告号", required = true)
    private String reportNo;
    @ApiModelProperty(value = "探伤备注", required = true)
    private String flawDetectionRemark;
    @ApiModelProperty(value = "探伤报告文件Id", required = true)
    private String flawDetectionPaper;
    @TableField(exist = false)
    private String assignDeviceId;

    @TableField(exist = false)
    private String userId;

    @TableField(exist = false)
    private Double completedHours;

    @TableField(exist = false)
    private Double actualHours;

    @TableField(exist = false)
    private Double reportHours;

    @TableField(exist = false)
    private Date startTime;

    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;
    @TableField(exist = false)
    private String trackType;
    @TableField(exist = false)
    private Integer qty;
    @TableField(exist = false)
    private String trackNo;

    @ApiModelProperty(value = "工艺版本", dataType = "String")
    @TableField(exist = false)
    private String versions;
    @TableField(exist = false)
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @TableField(exist = false)
    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String partsName;
    @TableField(exist = false)
    @ApiModelProperty(value = "总数量", dataType = "String")
    private Integer totalQuantity;
    @TableField(exist = false)
    @ApiModelProperty(value = "可派工数量", dataType = "String")
    private Integer dispatchingNumber;
    @TableField(exist = false)
    @ApiModelProperty(value = "重量", dataType = "Float")
    private Float weight;
    @TableField(exist = false)
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "计划号", dataType = "String")
    private String workPlanNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "工艺Id", dataType = "String")
    private String routerId;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质", dataType = "String")
    private String texture;

    @TableField(exist = false)
    @ApiModelProperty(value = "零件名称", dataType = "String")
    private String materialName;
    @TableField(exist = false)
    @ApiModelProperty(value = "是否为长周期 0 :否  1 是 ", dataType = "Integer")
    private Integer isLongPeriod;
    @TableField(exist = false)
    @ApiModelProperty(value = "是否为长周期 0 :否  1 是 ")
    private String longPeriod;
    @TableField(exist = false)
    @ApiModelProperty(value = "计划完成时间 ", dataType = "Date")
    private Date planEndTime;
    @TableField(exist = false)
    @ApiModelProperty(value = "钢水重量 ", dataType = "String")
    private String weightMolten;
    @TableField(exist = false)
    @ApiModelProperty(value = "单重KG ", dataType = "String")
    private String pieceWeight;
    @TableField(exist = false)
    @ApiModelProperty(value = "申请状态 0未配送 1已配送 2已退库 ", dataType = "Date")
    private Integer applyStatus;
    @TableField(exist = false)
    @ApiModelProperty(value = "项目名称 ", dataType = "String")
    private String projectName;

    @ApiModelProperty(value = "是否派工(未派工=0,已派工=1)", dataType = "int")
    private Integer isSchedule;
    @ApiModelProperty(value = "是否给予准结工时", dataType = "int")
    private Integer isPrepare;

    @ApiModelProperty(value = "是否是确认工序", dataType = "int")
    private Integer isNotarize;

    @ApiModelProperty(value = "规则Id", dataType = "String")
    private String ruleId;

    @ApiModelProperty(value = "规则名称", dataType = "String")
    private String ruleName;

    @ApiModelProperty(value = "是否复检", dataType = "String")
    private String isRecheck;

    @ApiModelProperty(value = "工艺版本", dataType = "String")
    @TableField(exist = false)
    private String routerVer;

    @TableField(value = "number")
    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "调度是否显示")
    private Integer isScheduleCompleteShow;

    @ApiModelProperty(value = "探伤记录编码")
    private String inspectRecordNo;

    @ApiModelProperty(value = "审核通过的探伤记录模板")
    private String tempType;

    @ApiModelProperty(value = "探伤检验人")
    private String checkBy;

    @ApiModelProperty(value = "探伤审核人")
    private String auditBy;

    @ApiModelProperty(value = "是否发起委托")
    private String isEntrust;

    @ApiModelProperty(value = "炉批号")
    @TableField(exist = false)
    private String batchNo;

    @ApiModelProperty(value = "材料委托单状态")
    @TableField(exist = false)
    private String orderStatus;

    @ApiModelProperty(value = "设备分类编码（热工）", dataType = "String")
    private String typeCode;

    @ApiModelProperty(value = "设备分类名称（热工）", dataType = "String")
    private String typeName;

    @ApiModelProperty(value = "温度下限℃（热工）", dataType = "String")
    private String tempDown;

    @ApiModelProperty(value = "温度上限℃（热工）", dataType = "String")
    private String tempUp;

    @ApiModelProperty(value = "实施温度℃（热工）", dataType = "String")
    private String tempWork;

    @ApiModelProperty(value = "保温时间h（热工）", dataType = "String")
    private String holdTime;

    @ApiModelProperty(value = "冷却方式（热工）", dataType = "String")
    private String coolType;

    @ApiModelProperty(value = "工时（热工）", dataType = "String")
    private Double heatHour;

    @ApiModelProperty(value = "工艺信息")
    private String routerInfo;


    @ApiModelProperty(value = "是否生成理化报告")
    @TableField(exist = false)
    private String syncStatus;

    @ApiModelProperty(value = "预装炉id", dataType = "String")
    private Long prechargeFurnaceId;


    @ApiModelProperty(value = "产品来源名称（热工）")
    @TableField(exist = false)
    private String productSourceName;

    @ApiModelProperty(value = "不合格Id")
    private String disqualificationId;

    @ApiModelProperty(value = "浇注状态")
    private Integer pourState;

    @ApiModelProperty(value = "浇注温度")
    private String pourTemperature;

    @ApiModelProperty(value = "工艺浇注温度")
    @TableField(exist = false)
    private String pourTemperatureRouter;

    @ApiModelProperty(value = "浇注时间 单位秒")
    private String pourTime;

    @ApiModelProperty(value = "工艺浇注时间 单位秒")
    @TableField(exist = false)
    private String pourTimeRouter;

    @ApiModelProperty(value = "保温完成时间 ", dataType = "Date")
    private Date holdFinishedTime;

    @ApiModelProperty(value = "热风机关闭时间")
    private String fanClosedTime;

    @ApiModelProperty(value = "试棒编号")
    @TableField(exist = false)
    private String testBarNo;

    @ApiModelProperty(value = "试棒跟单编号")
    @TableField(exist = false)
    private String testBarType;

    @ApiModelProperty(value = "锭型")
    @TableField(exist = false)
    private String ingotCase;

    @ApiModelProperty(value = "预装炉派工id")
    private String prechargeFurnaceAssignId;

    @ApiModelProperty(value = "是否冶炼配炉::是；否")
    @TableField(exist = false)
    private String ifPrechargeFurnace;

    @ApiModelProperty(value = "派工id")
    @TableField(exist = false)
    private String assignId;

    @ApiModelProperty(value = "根据材质和锭型进行分组后的数量")
    @TableField(exist = false)
    private Integer numByTexture;

    @ApiModelProperty(value = "浇注时间点")
    @TableField(exist = false)
    private String pourTimeDot;

    @TableField(exist = false)
    private String classes;

    /**
     * 下料规格
     */
    @TableField(exist = false)
    private String blankSpecifi;


}
