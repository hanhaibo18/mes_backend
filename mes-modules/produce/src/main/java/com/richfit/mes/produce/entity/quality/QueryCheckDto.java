package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.produce.entity.QueryPageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: QueryCheckDto.java
 * @Author: Hou XinYu
 * @Description: 查询意见填报人
 * @CreateTime: 2022年10月17日 11:23:00
 */
@Data
public class QueryCheckDto extends QueryPageDto {

    @ApiModelProperty(value = "跟单号", dataType = "String")
    private String trackNo;
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;
    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;
    @ApiModelProperty(value = "截止日期", dataType = "String")
    private String endTime;
    @ApiModelProperty(value = "不合格品申请单", dataType = "String")
    private String processSheetNo;
    @ApiModelProperty(value = "是否处理", dataType = "Boolean", required = true)
    private Boolean isDispose;
    @ApiModelProperty(value = "申请公司", dataType = "String")
    private String tenantId;
}
