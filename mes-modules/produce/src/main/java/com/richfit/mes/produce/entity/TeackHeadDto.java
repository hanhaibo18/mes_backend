package com.richfit.mes.produce.entity;

import com.richfit.mes.common.core.base.BasePageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:48
 */
@Data
public class TeackHeadDto extends BasePageDto<TeackHeadDto> {

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "跟单号")
    private String trackNo;

    @ApiModelProperty(value = "图号")
    private String drawingNo;

    @ApiModelProperty(value = "工作号")
    private String workNo;

    @ApiModelProperty(value = "产品编号")
    private String productNo;
}
