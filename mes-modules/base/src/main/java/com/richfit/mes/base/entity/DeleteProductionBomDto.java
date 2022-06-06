package com.richfit.mes.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DeleteProjectBomVo.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年06月07日 06:42:00
 */
@Data
public class DeleteProductionBomDto {
    @ApiModelProperty(value = "被删除的BOM的图号", required = true, dataType = "List<String>")
    List<String> drawingNoList;
    @ApiModelProperty(value = "机构编码", required = true, dataType = "String")
    String branchCode;
}
