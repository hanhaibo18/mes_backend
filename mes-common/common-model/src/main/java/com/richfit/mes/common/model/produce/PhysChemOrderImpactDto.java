package com.richfit.mes.common.model.produce;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 材料检测委托单力学性能冲击参数
 * @Author: renzewen
 * @Date: 2022/8/22 9:48
 */
@Data
public class PhysChemOrderImpactDto {

    @ApiModelProperty(value = "力学性能->冲击->实验温度")
    private String forceImpactTemp;
    @ApiModelProperty(value = "力学性能->冲击->缺口类型")
    private String forceImpactGap;
    @ApiModelProperty(value = "力学性能->冲击->试样方向")
    private String forceImpactDirection;

}
