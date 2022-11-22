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
    @ApiModelProperty(value = "报告号")
    private String reportNo;
    @ApiModelProperty(value = "炉批号")
    private String batchNo;
    @ApiModelProperty(value = "委托单状态（1已发起、2质检确认、3质检拒绝）")
    private String status;
    @ApiModelProperty(value = "委托单号")
    private String orderNo;
    @ApiModelProperty(value = "材料牌号")
    private String materialMark;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "图号")
    private String drawNo;
    @ApiModelProperty(value = "送样单位")
    private String sampleDept;
    @ApiModelProperty(value = "送样人")
    private String sampleBy;
    @ApiModelProperty(value = "收样人")
    private String sampleReceive;
    @ApiModelProperty(value = "收样日期")
    private String receiveTime;

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
    @ApiModelProperty(value = "同步状态（0未同步，1已经同步）")
    private String syncStatus;
    @ApiModelProperty(value = "同步时间")
    private String syncTime;
    @ApiModelProperty(value = "报告状态（0未生成,1以生成）")
    private String reportStatus;
    @ApiModelProperty(value = "化学分析->全分析")
    private int chemicalAnalysis;
    @ApiModelProperty(value = "化学分析->碳硫")
    private int chemicalCarbonSulfur;
    @ApiModelProperty(value = "化学分析->清洁度")
    private int chemicalClean;
    @ApiModelProperty(value = "化学分析->其他")
    private int chemicalOther;
    @ApiModelProperty(value = "化学分析->其他(值)")
    private String chemicalOtherVal;
    @ApiModelProperty(value = "金相分析->低倍")
    private int metallLowPower;
    @ApiModelProperty(value = "金相分析->组织")
    private int metallTexture;
    @ApiModelProperty(value = "金相分析->晶粒度")
    private int metallGrainSize;
    @ApiModelProperty(value = "金相分析->碳化物")
    private int metallCarbide;
    @ApiModelProperty(value = "金相分析->夹杂物")
    private int metallInclusions;
    @ApiModelProperty(value = "金相分析->石墨")
    private int metallGraphite;
    @ApiModelProperty(value = "金相分析->其他")
    private int metallOther;
    @ApiModelProperty(value = "金相分析->其他(值)")
    private int metallOtherVal;
    @ApiModelProperty(value = "力学性能->拉伸")
    private String forceTensile;
    @ApiModelProperty(value = "力学性能->冲击")
    private int forceImpact;
    @ApiModelProperty(value = "力学性能->弯曲")
    private int forceBend;
    @ApiModelProperty(value = "力学性能->硬度")
    private int forceHardness;
    @ApiModelProperty(value = "力学性能->剪切")
    private int forceShear;
    @ApiModelProperty(value = "力学性能->压扁")
    private int forceFlaser;
    @ApiModelProperty(value = "力学性能->残余应力")
    private int residual;
    @ApiModelProperty(value = "力学性能->其他")
    private int forceOther;
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
    @ApiModelProperty(value = "branchCode")
    private String branchCode;
    @ApiModelProperty(value = "租户id")
    private String tenantId;
}
