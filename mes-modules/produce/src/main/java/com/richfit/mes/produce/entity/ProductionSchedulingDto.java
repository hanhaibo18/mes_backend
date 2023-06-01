package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.util.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ProductionSchedulingDto.java
 * @Author: Hou XinYu
 * @Description: 生产排产查询条件
 * @CreateTime: 2023年05月30日 16:43:00
 */
@Data
public class ProductionSchedulingDto extends PageDto {

    @ApiModelProperty(value = "排产单号", dataType = "String")
    private String productionOrder;

    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;

    @ApiModelProperty(value = "产品型号", dataType = "String")
    private String produceType;

    @ApiModelProperty(value = "排产状态", dataType = "String")
    private String schedulingState;

    @ApiModelProperty(value = "通知类型", dataType = "String")
    private String notificationType;

    @ApiModelProperty(value = "排产类别", dataType = "String")
    private String productionType;

    @ApiModelProperty(value = "排产开始日期", dataType = "Date")
    private String salesSchedulingDateStart;

    @ApiModelProperty(value = "排产结束日期", dataType = "Date")
    private String salesSchedulingDateEnd;
}
