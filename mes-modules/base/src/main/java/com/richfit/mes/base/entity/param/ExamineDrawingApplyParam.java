package com.richfit.mes.base.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExamineDrawingApplyParam {
    @ApiModelProperty(value = "id的集合")
    private List<String> idList;
    @ApiModelProperty(value = "审批状态 0 待审核 1:审核通过 2 驳回")
    private Integer status;
    @ApiModelProperty(value = "驳回原因 ")
    private String reason;
}
