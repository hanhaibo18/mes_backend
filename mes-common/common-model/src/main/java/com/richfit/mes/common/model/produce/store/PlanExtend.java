package com.richfit.mes.common.model.produce.store;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.produce.Plan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: hujia
 * @Date: 2023/2/15 9:21
 */
@Data
@ApiModel(value = "计划管理")
public class PlanExtend {

    private static final long serialVersionUID = -1472432735506772167L;
    @TableId(type = IdType.ASSIGN_UUID)
    protected String id;

    @ApiModelProperty(value = "生产计划id")
    private String planId;

    @ApiModelProperty(value = "需求表id")
    private String demandId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "实样数量")
    private Integer sampleNum;

    @ApiModelProperty(value = "产品名称")
    private String productName;
}
