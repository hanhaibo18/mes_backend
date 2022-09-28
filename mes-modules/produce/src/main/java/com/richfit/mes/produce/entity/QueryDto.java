package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName: queryDto.java
 * @Author: Hou XinYu
 * @Description: 公共查询类
 * @CreateTime: 2022年01月27日 13:43:00
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryDto<T> {
    @ApiModelProperty(value = "实体", dataType = "entity", required = true)
    private T param;
    @ApiModelProperty(value = "页码", dataType = "Long", required = true)
    private Long page = 1L;
    @ApiModelProperty(value = "数量", dataType = "Long", required = true)
    private Long size = 10L;
    @ApiModelProperty(value = "工厂Code", required = true)
    private String branchCode;
    @ApiModelProperty(value = "租户ID", required = true)
    private String tenantId;
    @ApiModelProperty(value = "排序方式", dataType = "String")
    private String order;
    @ApiModelProperty(value = "排序列明", dataType = "String")
    private String orderCol;
}

