package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.util.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: SkillNoticeDto.java
 * @Author: Hou XinYu
 * @Description: 接受技术通知查询条件
 * @CreateTime: 2023年06月02日 16:01:00
 */
@Data
public class SkillNoticeDto extends PageDto {
    @ApiModelProperty(value = "开始时间")
    private String stateTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "通知编号")
    private String skillNoticeNumber;
    @ApiModelProperty(value = "工作号")
    private String workNo;
    @ApiModelProperty(value = "接收状态")
    private String notificationState;
}
