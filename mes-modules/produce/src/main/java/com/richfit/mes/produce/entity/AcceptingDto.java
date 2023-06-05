package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.util.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: AcceptingDto.java
 * @Author: Hou XinYu
 * @Description: 接受排产查询
 * @CreateTime: 2023年05月30日 18:15:00
 */
@Data
public class AcceptingDto extends PageDto {
    @ApiModelProperty(value = "排产单号", dataType = "String")
    private String productionOrder;

    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;

    @ApiModelProperty(value = "产品型号", dataType = "String")
    private String produceType;

    @ApiModelProperty(value = "排产类别", dataType = "String")
    private String productionType;

    @ApiModelProperty(value = "接收类别", dataType = "String")
    private String acceptingState;

    @ApiModelProperty(value = "排产开始日期", dataType = "Date")
    private String salesSchedulingDateStart;

    @ApiModelProperty(value = "排产结束日期", dataType = "Date")
    private String salesSchedulingDateEnd;

    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;

    @ApiModelProperty(value = "物料编码", dataType = "String")
    private String materialNo;
}
