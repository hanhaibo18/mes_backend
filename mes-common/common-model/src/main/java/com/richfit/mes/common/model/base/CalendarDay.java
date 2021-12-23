package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.sql.Time;
import java.util.Date;

/**
 * @author 王瑞
 * @Description 日历日期表
 */
@Data
public class CalendarDay extends BaseEntity<CalendarDay> {

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
     * 时间类型 0按时间段 1按星期
     */
    private String dateType;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 星期几
     */
    private Integer week;

    /**
     * 日期类型 0假期 1加班
     */
    private String type;

}
