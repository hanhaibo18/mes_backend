package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.base.RouterCheck;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: QueryQualityTestingDetailsVo.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年06月29日 17:33:00
 */
@Data
public class QueryQualityTestingDetailsVo {

    @ApiModelProperty(value = "质检指标列表", dataType = "List<RouterCheck>")
    private List<RouterCheck> routerCheckList;
    @ApiModelProperty(value = "质检必传文件列表", dataType = "List<RouterCheck>")
    private List<RouterCheck> operationTypeSpecs;
}
