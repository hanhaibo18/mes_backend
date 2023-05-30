package com.richfit.mes.common.model.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName: PageDto.java
 * @Author: Hou XinYu
 * @Description: 通用分页方法
 * @CreateTime: 2023年05月29日 18:55:00
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {
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
