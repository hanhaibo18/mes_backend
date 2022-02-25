package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import java.util.*;

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
    private Date qualityCompleteTime;
    private Integer qualityQty;
    private Integer qualityUnqty;
    /**
     * 质检结果
     */
    private Integer qualityResult;
    private String failProcess;
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

}
