package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
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
@ApiModel("PhysChemOrder")
public class PhysChemOrder extends BaseEntity<PhysChemOrder> {
    @ApiModelProperty(value = "炉批号")
    private String batchNo;
    @ApiModelProperty(value = "委托单状态（0待发起、1已发起、2质检确认、3质检拒绝）")
    private String status;
    @ApiModelProperty(value = "委托单号")
    private String orderNo;
    @ApiModelProperty(value = "材料牌号")
    private String materialMark;
    @ApiModelProperty(value = "送样单位")
    private String sampleDept;
    @ApiModelProperty(value = "送样人")
    private String sampleBy;

    @ApiModelProperty(value = "制造厂家")
    private String manufacturer;
    @ApiModelProperty(value = "热处理状态")
    private String heatState;
    @ApiModelProperty(value = "试棒规格")
    private String testBarSpec;
    @ApiModelProperty(value = "取样位置")
    private String samplePlace;
    @ApiModelProperty(value = "验收标准")
    private String accepStandard;
    @ApiModelProperty(value = "试样数量")
    private String sampleNum;

    @ApiModelProperty(value = "送样时间")
    private String sampleTime;
    @ApiModelProperty(value = "报告号")
    private String reportNo;
    @ApiModelProperty(value = "同步状态（0未同步，1已经同步）")
    private String syncStatus;
    @ApiModelProperty(value = "报告状态（0未生成,1以生成）")
    private String reportStatus;
    @ApiModelProperty(value = "化学分析->全分析")
    private String chemicalAnalysis;
    @ApiModelProperty(value = "化学分析->碳硫")
    private String chemicalCarbonSulfur;
    @ApiModelProperty(value = "化学分析->清洁度")
    private String chemicalClean;
    @ApiModelProperty(value = "化学分析->其他")
    private String chemicalOther;
    @ApiModelProperty(value = "化学分析->其他(值)")
    private String chemicalOtherVal;
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
    @ApiModelProperty(value = "金相分析->其他(值)")
    private String metallOtherVal;
    @ApiModelProperty(value = "力学性能->拉伸")
    private String forceTensile;
    @ApiModelProperty(value = "力学性能->冲击")
    private String forceImpact;
    @ApiModelProperty(value = "力学性能->弯曲")
    private String forceBend;
    @ApiModelProperty(value = "力学性能->硬度")
    private String forceHardness;
    @ApiModelProperty(value = "力学性能->剪切")
    private String forceShear;
    @ApiModelProperty(value = "力学性能->压扁")
    private String forceFlaser;
    @ApiModelProperty(value = "力学性能->残余应力")
    private String residual;
    @ApiModelProperty(value = "力学性能->其他")
    private String forceOther;
    @ApiModelProperty(value = "力学性能->其他(值)")
    private String forceOtherVal;
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
    @ApiModelProperty(value = "委托人")
    private String consignor;


    @TableField(exist = false)
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @TableField(exist = false)
    @ApiModelProperty(value = "图号")
    private String drawingNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "跟单号")
    private String trackNo;
}
