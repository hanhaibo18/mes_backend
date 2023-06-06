package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: SkillIssueNoticeDto.java
 * @Author: Hou XinYu
 * @Description: 调度通知下发
 * @CreateTime: 2023年06月05日 11:29:00
 */
@Data
public class SkillIssueNoticeDto {
    private List<String> idList;
    @ApiModelProperty(value = "执行单位")
    private List<String> executableUnitList;
}
