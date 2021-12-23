package com.richfit.mes.produce.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.richfit.mes.common.core.base.BasePageDto;
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
    private int status;
    /**
     * 预警状态
     */
    private int alarmStatus;

    private String tenantId;

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
}
