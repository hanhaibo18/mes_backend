package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * produce_track_complete_cache
 *
 * @author
 */
@Data
public class TrackCompleteCache extends BaseEntity<TrackCompleteCache> {

    /**
     * 报工人
     */
    private String completeBy;

    /**
     * 报工时间
     */
    private Date completeTime;

    /**
     * 跟单工序ID
     */
    private String tiId;

    /**
     * 派工ID
     */
    private String assignId;

    /**
     * 跟单ID
     */
    private String trackId;

    /**
     * 报工人员ID
     */
    private String userId;

    /**
     * 报工设备ID
     */
    private String deviceId;

    /**
     * 产品编号
     */
    private String prodNo;

    /**
     * 跟单编号
     */
    private String trackNo;

    /**
     * 探伤结果
     */
    private String detectionResult;

    /**
     * 报工数量
     */
    private Double completedQty;

    /**
     * 拒绝数量
     */
    private Double rejectQty;

    /**
     * 报工工时
     */
    private Double completedHours;

    /**
     * 实际工时
     */
    private Double actualHours;

    /**
     * 上报工时
     */
    private Double reportHours;

    /**
     * 静态工时
     */
    private Double staticHours;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;

    private String outNo;

    private String siteId;

    private String deviceName;

    private String userName;

    private String siteName;

}
