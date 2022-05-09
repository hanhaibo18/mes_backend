package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: QueryTailAfterDto.java
 * @Author: Hou XinYu
 * @Description: 查询调度跟踪
 * @CreateTime: 2022年05月06日 08:10:00
 */
@Data
public class QueryTailAfterDto {
    private Date startDate;
    private Date endDate;
    private String drawingNo;
    private String trackNo;
}
