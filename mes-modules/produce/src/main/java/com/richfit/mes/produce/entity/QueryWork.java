package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: queryWork.java
 * @Author: Hou XinYu
 * @Description: 工作清单查询条件
 * @CreateTime: 2022年05月08日 06:20:00
 */
@Data
public class QueryWork {
    private Date startDate;
    private Date endDate;
    private String workId;
    private String drawingNo;
    private String numberNo;
    private String trackNo;
}
