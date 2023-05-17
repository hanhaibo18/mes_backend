package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.produce.entity.QueryPageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: queryInspectorDto.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年09月29日 16:01:00
 */
@Data
public class QueryInspectorDto extends QueryPageDto {
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
    @ApiModelProperty(value = "是否发布", dataType = "String")
    private Integer isIssue;
    @ApiModelProperty(value = "状态", dataType = "String")
    private String type;
    @ApiModelProperty(value = "处理单位1", dataType = "String")
    private String unitTreatmentOne;
    @ApiModelProperty(value = "处理单位2", dataType = "String")
    private String unitTreatmentTwo;
}
