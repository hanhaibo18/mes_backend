package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.util.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DispatchDto.java
 * @Author: Hou XinYu
 * @Description: 调度通知查询条件
 * @CreateTime: 2023年06月02日 16:37:00
 */
@Data
public class DispatchDto extends PageDto {
    @ApiModelProperty(value = "开始时间")
    private String stateTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "通知编号")
    private String skillNoticeNumber;
    @ApiModelProperty(value = "工作号")
    private String workNo;
    @ApiModelProperty(value = "接收状态")
    private String dispatchState;
    @ApiModelProperty(value = "钻机名称")
    private String drillingRigName;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "图号")
    private String drawingNo;
}
