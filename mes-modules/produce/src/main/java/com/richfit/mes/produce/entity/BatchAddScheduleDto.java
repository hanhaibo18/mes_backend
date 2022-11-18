package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: BatchAddScheduleDto.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年07月06日 11:53:00
 */
@Data
public class BatchAddScheduleDto {
    @ApiModelProperty(value = "工序Id集合", dataType = "List<String>")
    private List<String> tiId;
    @ApiModelProperty(value = "车间", dataType = "String")
    private String branchCode;
    @ApiModelProperty(value = "下车间", dataType = "String")
    private String nextBranchCode;
    @ApiModelProperty(value = "审核意见", dataType = "String")
    private String result;
    @ApiModelProperty(value = "是否给予准结工时", dataType = "String")
    private Integer isPrepare;
}
