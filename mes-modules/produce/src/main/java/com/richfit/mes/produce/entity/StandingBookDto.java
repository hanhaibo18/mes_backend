package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: StandingBookDto.java
 * @Author: Hou XinYu
 * @Description: 跟单台账查询条件
 * @CreateTime: 2022年04月27日 22:46:00
 */
@Data
public class StandingBookDto {
    private Date startTime;
    private Date endTime;
    private String documentaryId;
    private String drawingNo;
    private String workNo;
    private String productNo;
}
