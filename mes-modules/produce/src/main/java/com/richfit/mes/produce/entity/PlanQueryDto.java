package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: PlanQueryDto.java
 * @Author: Hou XinYu
 * @Description: 查询能关联跟单的计划
 * @CreateTime: 2022年04月20日 16:00:00
 */
@Data
public class PlanQueryDto {
    Date startTime;
    Date endTime;
    String drawingNo;
    String tenantId;
    String branchCode;
}
