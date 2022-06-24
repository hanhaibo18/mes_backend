package com.richfit.mes.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: QueryIsHistory.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年06月24日 11:13:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryIsHistory {
    @ApiModelProperty(value = "是否是历史数据", dataType = "Boolean")
    private Boolean isHistory;
    @ApiModelProperty(value = "老版本号", dataType = "String")
    private String oldVersions;
    @ApiModelProperty(value = "新版本号", dataType = "String")
    private String newVersions;
}
