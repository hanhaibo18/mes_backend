package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: renzewen
 * @Date: 2022/9/6 9:21
 */
@Data
@ApiModel(value = "探伤模板字典")
public class InspectionDictionary extends BaseEntity<InspectionDictionary> {

    private static final long serialVersionUID = -1472432735506772177L;

    @ApiModelProperty(value = "父id")
    private String parentId;

    @ApiModelProperty(value = "模板类型")
    private String tempType;

    @ApiModelProperty(value = "字典编码")
    private String dicCode;

    @ApiModelProperty(value = "字典值")
    private String dicValue;

    @ApiModelProperty(value = "排序号")
    private Integer serialNum;
}
