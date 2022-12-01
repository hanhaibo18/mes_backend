package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: QueryOrderSyncLogPage.java
 * @Author: Hou XinYu
 * @Description: 订单日志查询
 * @CreateTime: 2022年12月01日 09:36:00
 */
@Data
public class QueryOrderSyncLogPageDto extends QueryPageDto {
    @ApiModelProperty(value = "物料号", dataType = "String")
    private String materialNo;
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @ApiModelProperty(value = "同步状态(0=未同步,1=已同步)", dataType = "String")
    private String syncState;
    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;
}
