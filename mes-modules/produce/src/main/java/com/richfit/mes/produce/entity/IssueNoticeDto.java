package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: IssueNoticeDto.java
 * @Author: Hou XinYu
 * @Description: 下发通知DTO
 * @CreateTime: 2023年05月30日 17:28:00
 */
@Data
public class IssueNoticeDto {
    private List<String> idList;
    @ApiModelProperty(value = "执行单位")
    private List<String> executableUnitList;
    @ApiModelProperty(value = "落成单位")
    private List<String> designatedUnit;
}
