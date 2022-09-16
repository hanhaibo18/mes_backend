package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * 理化检验委托单
 *
 * @author renzewen
 * @since 2022-9-5
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class PhysChemOrder extends BaseEntity<PhysChemOrder> {
    @ApiModelProperty(value = "跟单工序id")
    private String itemId;
    @ApiModelProperty(value = "化学分析->全分析")
    private String chemicalAnalysis;
    @ApiModelProperty(value = "化学分析->碳硫")
    private String chemicalCarbonSulfur;
    @ApiModelProperty(value = "化学分析->清洁度")
    private String chemicalClean;
    @ApiModelProperty(value = "化学分析->其他")
    private String chemicalOther;
    @ApiModelProperty(value = "金相分析->低倍")
    private String metallLowPower;
    @ApiModelProperty(value = "金相分析->组织")
    private String metallTexture;
    @ApiModelProperty(value = "金相分析->晶粒度")
    private String metallGrainSize;
    @ApiModelProperty(value = "金相分析->碳化物")
    private String metallCarbide;
    @ApiModelProperty(value = "金相分析->夹杂物")
    private String metallInclusions;
    @ApiModelProperty(value = "金相分析->石墨")
    private String metallGraphite;
    @ApiModelProperty(value = "金相分析->其他")
    private String metallOther;
    @ApiModelProperty(value = "力学性能->拉伸数量")
    private String forceTensileNumber;
    @ApiModelProperty(value = "力学性能->冲击数量")
    private String forceImpactNumber;
    @ApiModelProperty(value = "力学性能->弯曲数量")
    private String forceBendNumber;
    @ApiModelProperty(value = "力学性能->硬度数量")
    private String forceHardnessNumber;
    @ApiModelProperty(value = "力学性能->剪切数量")
    private String forceShearNumber;
    @ApiModelProperty(value = "力学性能->压扁数量")
    private String forceFlaserNumber;
    @ApiModelProperty(value = "力学性能->其他数量")
    private String forceOtherNumber;
    @ApiModelProperty(value = "力学性能->拉伸->屈服强度")
    private String forceTensileStrength;
    @ApiModelProperty(value = "力学性能->拉伸->伸长率")
    private String forceTensileElongation;
    @ApiModelProperty(value = "力学性能->拉伸->试样方向")
    private String forceTensileDirection;
    @ApiModelProperty(value = "力学性能->冲击->实验温度")
    private String forceImpactTemp;
    @ApiModelProperty(value = "力学性能->冲击->缺口类型")
    private String forceImpactGap;
    @ApiModelProperty(value = "力学性能->冲击->试样方向")
    private String forceImpactDirection;
    @ApiModelProperty(value = "力学性能->弯曲->试样方向")
    private String forceBendDirection;
    @ApiModelProperty(value = "力学性能->硬度->硬度种类")
    private String forceBendType;
    @ApiModelProperty(value = "力学性能->硬度->试验位置")
    private String forceBendPart;
    @ApiModelProperty(value = "残余应力->数量")
    private String residualNumber;
}
