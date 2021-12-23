package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.sql.Time;

/**
 * @author 王瑞
 * @Description 日历班次表
 */
@Data
public class CalendarClass extends BaseEntity<CalendarClass> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 机构编码
     */
    private String branchCode;


    private String name;


    /**
     * 开始时间
     */
    private Time startTime;

    /**
     * 结束时间
     */
    private Time endTime;


}
