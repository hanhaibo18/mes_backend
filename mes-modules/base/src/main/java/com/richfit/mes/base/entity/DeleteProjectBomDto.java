package com.richfit.mes.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DeleteProjectBomVo.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年06月07日 06:42:00
 */
@Data
public class DeleteProjectBomDto {
    @ApiModelProperty(value = "被删除的BOM的工作号", required = true, dataType = "List<String>")
    List<String> workPlanNoList;
    @ApiModelProperty(value = "机构编码", required = true, dataType = "String")
    String branchCode;
}
