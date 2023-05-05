package com.richfit.mes.produce.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.richfit.mes.common.core.base.BasePageDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:48
 */
@Data
public class PlanDto extends BasePageDto<PlanDto> {

    /**
     * 计划编号
     */
    private String projCode;
    /**
     * 工作号
     */
    private String workNo;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 图号
     */
    private String drawNo;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 预警状态
     */
    private int alarmStatus;

    private String tenantId;
    private String branchCode;

    /**
     * 排序列
     */
    private String orderCol;

    /**
     * 排序方向
     */
    private String order;

    /**
     * 过滤已关闭计划
     */
    private boolean fiterClose;

    /**
     * 过滤已全部生成跟单的计划
     */
    private boolean fiterTrackAll;

    /**
     * 是否列表显示
     */
    private boolean showList;
    /**
     * 计划类型 1新制  2 返修'
     */
    private String projType;

    /**
     * 来源  1 分公司计划  2车间计划
     */
    private String source;

    private String startPlanMonth;

    private String endPlanMonth;

    private String isProjectBom;
}
