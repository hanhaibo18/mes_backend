package com.richfit.mes.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: TreeVo.java
 * @Author: Hou XinYu
 * @Description: 质检人员
 * @CreateTime: 2022年08月30日 16:31:00
 */
@Data
public class TreeVo {
    @ApiModelProperty(value = "车间编码/用户编码", dataType = "String")
    private String code;
    @ApiModelProperty(value = "车间名称/用户名称", dataType = "String")
    private String name;
    @ApiModelProperty(value = "用户类型", dataType = "Integer")
    private String userType;
    @ApiModelProperty(value = "质检人员信息")
    private List<TreeVo> tree;
}
