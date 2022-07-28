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

    private static final long serialVersionUID = -7319209148899298755L;
    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "跟单ID", dataType = "String")
    private String trackHeadId;
    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;
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
    private Integer isCurrent;
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
    @ApiModelProperty(value = "当前工序是否完成", dataType = "Integer")
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
    //不合格步骤?
    @ApiModelProperty(value = "不合格步骤", dataType = "String")
    private String failProcess;
    //检验人
    @ApiModelProperty(value = "检验人", dataType = "String")
    private String qualityCheckBy;
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

    @ApiModelProperty(value = "跟单顺序完成", dataType = "Integer")
    private Integer isTrackSequenceComplete;

    @ApiModelProperty(value = "batchQty", dataType = "Integer")
    private Integer batchQty;

    @ApiModelProperty(value = "eventId", dataType = "Integer")
    private Integer eventId;

    @ApiModelProperty(value = "可派工数量", dataType = "Integer")
    private Integer assignableQty;

    @ApiModelProperty(value = "工序完成数量", dataType = "Integer")
    private Double completeQty;

    @ApiModelProperty(value = "加热温度", dataType = "Integer")
    private Double heatTemperature;

    /**
     * 合格证编号
     */
    @ApiModelProperty(value = "合格证编号", dataType = "Integer")
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
    @ApiModelProperty(value = "工艺Id", dataType = "String")
    private String routerId;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质", dataType = "String")
    private String texture;

    @ApiModelProperty(value = "是否派工(未派工=0,已派工=1)", dataType = "int")
    private int isSchedule;
    @ApiModelProperty(value = "是否给予准结工时", dataType = "int")
    private int isPrepare;
}
