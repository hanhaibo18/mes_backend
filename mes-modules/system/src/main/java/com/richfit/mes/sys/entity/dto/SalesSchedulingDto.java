package com.richfit.mes.sys.entity.dto;

import com.richfit.mes.common.model.util.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: SalesSchedulingDto.java
 * @Author: Hou XinYu
 * @Description: 销售排产通知查询条件
 * @CreateTime: 2023年05月29日 18:38:00
 */
@Data
public class SalesSchedulingDto extends PageDto {

    @ApiModelProperty(value = "排产单号", dataType = "String")
    private String productionOrder;

    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;

    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String produceName;

    @ApiModelProperty(value = "用户单位", dataType = "String")
    private String userUnit;

    @ApiModelProperty(value = "发文单位", dataType = "String")
    private String issuingUnit;

    @ApiModelProperty(value = "排产开始日期", dataType = "Date")
    private String salesSchedulingDateStart;

    @ApiModelProperty(value = "排产结束日期", dataType = "Date")
    private String salesSchedulingDateEnd;

    @ApiModelProperty(value = "通知接收状态", dataType = "integer")
    private String notificationStatus;
}
