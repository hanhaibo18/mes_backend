package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: QueryFlawDetection.java
 * @Author: Hou XinYu
 * @Description: 查询探伤报工
 * @CreateTime: 2022年05月10日 04:50:00
 */
@Data
public class QueryFlawDetectionListDto {
    @ApiModelProperty(value = "开始时间", required = true)
    private Date startTime;
    @ApiModelProperty(value = "结束时间", required = true)
    private Date endTime;
    @ApiModelProperty(value = "产品编号", required = true)
    private String productNo;
    @ApiModelProperty(value = "跟单号", required = true)
    private String trackNo;
}
