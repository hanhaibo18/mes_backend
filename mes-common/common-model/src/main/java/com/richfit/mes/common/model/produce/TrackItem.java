package com.richfit.mes.common.model.produce;

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

    /**
     * 跟单ID
     */
    private String trackHeadId;
    /**
     * 产品编号
     */
    private String productNo;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 工序版本号
     */
    private String optVer;
    /**
     * 工序名
     */
    private String optName;
    /**
     * 工序ID
     */
    private String optId;
    /**
     * 工序类型
     */
    private Integer optType;
    private String subOptParentId;
    @ApiModelProperty(value = "是否并行")
    private Integer optParallelType;
    /**
     * 准结时间
     */
    private Double prepareEndHours;
    /**
     * 单件工时
     */
    private Double singlePieceHours;
    private String technologySequence;
    private Integer originalOptSequence;
    /**
     * 工序号
     */
    private Integer optSequence;
    /**
     * 下一道工序号
     */
    private Integer nextOptSequence;
    /**
     * 工序排序号
     */
    private Integer sequenceOrderBy;
    private Integer partialBatchNo;
    /**
     * 返工次数
     */
    private Integer reworkTimes;
    /**
     * 是否当前工序
     */
    private Integer isCurrent;
    /**
     * 是否开工
     */
    private Integer isDoing;
    private String startDoingUser;
    /**
     * 开工时间
     */
    private Date startDoingTime;
    private Integer isOperationComplete;
    /**
     * 完工时间
     */
    private Date operationCompleteTime;
    /**
     * 是否质检确认
     */
    private Integer isExistQualityCheck;
    /**
     * 是否质检完成
     */
    private Integer isQualityComplete;
    /**
     * 质检时间
     */
    private Date qualityCompleteTime;
    /**
     * 质检数量?
     */
    private Integer qualityQty;
    /**
     * 不合格数量?
     */
    private Integer qualityUnqty;
    /**
     * 质检结果
     */
    private Integer qualityResult;
    //不合格步骤?
    private String failProcess;
    //检验人
    private String qualityCheckBy;
    /**
     * 是否调度确认
     */
    private Integer isExistScheduleCheck;

    /**
     * 是否自动派工
     */
    private Integer isAutoSchedule;


    /**
     * 是否调度完成
     */
    private Integer isScheduleComplete;
    private Date scheduleCompleteTime;
    private String scheduleCompleteBy;
    private String isFinalComplete;
    private Integer isTrackSequenceComplete;
    private Integer batchQty;
    private Integer eventId;
    private Integer assignableQty;
    private Integer completeQty;
    private Double heatTemperature;
    /**
     * 合格证编号
     */
    private String certificateNo;
    private Integer isValid;
    private Double carbonDepth;
    private Double nitrogenDepth;
    private String isCountPrepareHour;
    private String qualityCertificateDestination;
    private String reserveText1;
    private String reserveText2;
    private String reserveText3;
    private String reserveText4;
    private String priority;
    private String equipmentType;
    private Double holdingTemperature;
    private Double lowerLimits;
    private Double upperLimits;
    private String coolingDownMethod;
    private String notice;
    private String branchCode;
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

    @TableField(exist = false)
    private String drawingNo;
    @TableField(exist = false)
    private String trackType;
    @TableField(exist = false)
    private Integer qty;
    @TableField(exist = false)
    private String trackNo;

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
    @ApiModelProperty(value = "工艺版本", dataType = "String")
    private String versions;
}
