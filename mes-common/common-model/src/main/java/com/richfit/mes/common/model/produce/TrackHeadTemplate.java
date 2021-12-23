package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author 王瑞
 * @Description 跟单模板
 */
@Data
public class TrackHeadTemplate extends BaseEntity<TrackHeadTemplate> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 模板编号
     */
    private String templateNo;

    /**
     * 模板描述
     */
    private String templateDesc;

    /**
     * 组织机构编号
     */
    private String branchCode;

    /**
     * 派工前是否要编辑工序
     */
    private String beforeDispatch;

    /**
     * 工序是否可编辑
     */
    private String isEditRouter;

    /**
     * 行数
     */
    private Integer rowNumber;

    /**
     * 列数
     */
    private Integer colNumber;

    /**
     * 定额工时是否可编辑
     */
    private String editQuotaTime;

    /**
     * 实际工时是否可编辑
     */
    private String editWorkTime;

    /**
     * 是否质检确认
     */
    private String isQualityCheck;

    /**
     * 是否调度确认
     */
    private String isScheduleCheck;

}
