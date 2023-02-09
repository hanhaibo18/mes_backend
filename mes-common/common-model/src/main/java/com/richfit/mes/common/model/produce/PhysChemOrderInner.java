package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 理化委托单中间表
 *
 * @author renzewen
 * @since 2022-9-5
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel("PhysChemOrderInner")
public class PhysChemOrderInner extends BaseEntity<PhysChemOrderInner> {
    @ApiModelProperty(value = "炉批号")
    private String batchNo;
    @ApiModelProperty(value = "委托单状态（0、未发起、1已发起、2质检确认、3质检拒绝）")
    private String status;
    @ApiModelProperty(value = "委托单号")
    private String orderNo;
    @ApiModelProperty(value = "材料牌号")
    private String materialMark;
    @ApiModelProperty(value = "零件名称")
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
    @ApiModelProperty(value = "报告号")
    private String reportNo;
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
    private String metallOther;
    @ApiModelProperty(value = "金相分析->其他(值)")
    private String metallOtherVal;
    @ApiModelProperty(value = "力学性能->拉伸")
    private int forceTensile;
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
    @ApiModelProperty(value = "力学性能->拉伸->屈服强度1")
    private String forceTensileStrength1;
    @ApiModelProperty(value = "力学性能->拉伸->屈服强度2")
    private String forceTensileStrength2;
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
    //试验数据结果字段
    @ApiModelProperty(value = "c")
    private String c;
    @ApiModelProperty(value = "si")
    private String si;
    @ApiModelProperty(value = "mn")
    private String mn;
    @ApiModelProperty(value = "p")
    private String p;
    @ApiModelProperty(value = "s")
    private String s;
    @ApiModelProperty(value = "cr")
    private String cr;
    @ApiModelProperty(value = "mo")
    private String mo;
    @ApiModelProperty(value = "ni")
    private String ni;
    @ApiModelProperty(value = "cu")
    private String cu;
    @ApiModelProperty(value = "v")
    private String v;
    @ApiModelProperty(value = "ti")
    private String ti;
    @ApiModelProperty(value = "nb")
    private String nb;
    @ApiModelProperty(value = "al")
    private String al;
    @ApiModelProperty(value = "fe")
    private String fe;
    @ApiModelProperty(value = "sn")
    private String sn;
    @ApiModelProperty(value = "zn")
    private String zn;
    @ApiModelProperty(value = "pb")
    private String pb;
    @ApiModelProperty(value = "w")
    private String w;
    @ApiModelProperty(value = "rm")
    private String rm;
    @ApiModelProperty(value = "rel")
    private String rel;
    @ApiModelProperty(value = "a")
    private String a;
    @ApiModelProperty(value = "z")
    private String z;
    @ApiModelProperty(value = "ak")
    private String ak;
    @ApiModelProperty(value = "弯曲Bend")
    private String bend;
    @ApiModelProperty(value = "硬度Hardness")
    private String hardness;
    @ApiModelProperty(value = "压扁Flattening")
    private String flattening;
    @ApiModelProperty(value = "一般疏松")
    private String geneLoose;
    @ApiModelProperty(value = "中心疏松")
    private String centerLoose;
    @ApiModelProperty(value = "低倍其它")
    private String other;
    @ApiModelProperty(value = "金相检验结果")
    private String resultsMetal;

    @ApiModelProperty(value = "报告日期（年）")
    private String reportYear;
    @ApiModelProperty(value = "报告日期（月）")
    private String reportMonth;
    @ApiModelProperty(value = "报告日期（日）")
    private String reportDay;
    @ApiModelProperty(value = "拉伸附加序号")
    private String tensileAdditionalNo;
    @ApiModelProperty(value = "冲击附加序号")
    private String impactAdditionalNo;

    @ApiModelProperty(value = "弯压硬低应剪附加序号")
    private String othertestAdditionalNo;
    @ApiModelProperty(value = "化学附加序号")
    private String chemicalAdditionalNo;
    @ApiModelProperty(value = "金相附加序号")
    private String metallAdditionalNo;
    @ApiModelProperty(value = "金相结果名称")
    private String metallName;
    @ApiModelProperty(value = "弯压硬低应剪方向")
    private String othertestDirection;

    @ApiModelProperty(value = "弯压硬低应剪方向")
    private String tensileAdditional;
    @ApiModelProperty(value = "拉伸附加")
    private String impactAdditional;
    @ApiModelProperty(value = "冲击附加")
    private String othertestAdditional;
    @ApiModelProperty(value = "化学附加")
    private String chemicalAdditional;
    @ApiModelProperty(value = "金相附加")
    private String metallAdditional;

    @ApiModelProperty(value = "冲击试样参数")
    private String impactParameter;
    @ApiModelProperty(value = "弯压硬低应剪名称")
    private String othertestName;
    @ApiModelProperty(value = "弯压硬低应剪参数1")
    private String othertestParameter1;
    @ApiModelProperty(value = "弯压硬低应剪参数2")
    private String othertestParameter2;
    @ApiModelProperty(value = "碳光谱线")
    private String cSpectralline;

    @ApiModelProperty(value = "硫光谱线")
    private String sSpectralline;
    @ApiModelProperty(value = "报告备注名称")
    private String reportRemarkName;
    @ApiModelProperty(value = "报告备注单位")
    private String reportRemarkUnit;
    @ApiModelProperty(value = "报告备注数值")
    private String reportRemarkVal;
    @ApiModelProperty(value = "弯压硬低应剪数值")
    private String othertestVal;

    @ApiModelProperty(value = "拉伸试验者")
    private String tensileTester;
    @ApiModelProperty(value = "冲击试验者")
    private String impactTester;
    @ApiModelProperty(value = "弯压硬低应剪试验者")
    private String othertestTester;
    @ApiModelProperty(value = "化学试验者")
    private String chemicalTester;
    @ApiModelProperty(value = "金相试验者")
    private String metallTester;

    @ApiModelProperty(value = "复核者")
    private String reviewedBy;
    @ApiModelProperty(value = "批准者")
    private String supervidor;




    /**
     * 多选的力学性能参数
     */
    @TableField(exist = false)
    private List<PhysChemOrderImpactDto> impacts;



    @ApiModelProperty(value = "审核状态:0未审核，1已审核，2以退回")
    private String isAudit;


    @ApiModelProperty(value = "审核人")
    private String auditBy;


    @ApiModelProperty(value = "审核时间")
    private String auditTime;


    @ApiModelProperty(value = "合格状态：0合格，1不合格")
    private String isStandard;


    @ApiModelProperty(value = "合格判定人")
    private String standardBy;


    @ApiModelProperty(value = "合格判定时间")
    private String standardTime;


}
