package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: QueryFlawDetection.java
 * @Author: Hou XinYu
 * @Description: 查询探伤报工
 * @CreateTime: 2022年05月10日 04:50:00
 */
@Data
public class QueryFlawDetectionDto {
    private Date startTime;
    private Date endTime;
    private String productNo;
    private Boolean isRecheck;
}
