package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.common.model.produce.Disqualification;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author llh
 */
@Data
public class DisqualificationResultVo extends Disqualification {

    @ApiModelProperty(value = "不合格情况")
    private String disqualificationCondition;

    @ApiModelProperty(value = "租户公司")
    private String discoverTenant;

    @ApiModelProperty(value = "不合格姓名")
    private String disqualificationName;

    @ApiModelProperty(value = "发现车间")
    private String discoverBranch;

    @ApiModelProperty(value = "责任单位内")
    private String unitResponsibilityWithin;

    @ApiModelProperty(value = "责任单位外")
    private String unitResponsibilityOutside;

    @ApiModelProperty(value = "总重量")
    private Double totalWeight;

    @ApiModelProperty(value = "质控姓名")
    private String qualityName;

    @ApiModelProperty(value = "处理单位1")
    private String unitTreatmentOne;

    @ApiModelProperty(value = "处理单位2")
    private String unitTreatmentTwo;

    @ApiModelProperty(value = "发现工序")
    private String discoverItem;

    @ApiModelProperty(value = "废品工时")
    private Double discardTime;

    @ApiModelProperty(value = "回用工时")
    private Double reuseTime;

    @ApiModelProperty(value = "让步接收数量")
    private Integer acceptDeviation;

    @ApiModelProperty(value = "返修合格数量")
    private Integer repairQualified;

    @ApiModelProperty(value = "报废数量")
    private Integer scrap;

    @ApiModelProperty(value = "退货数量")
    private Integer salesReturn;

    @ApiModelProperty(value = "报废损失")
    private String salesReturnLoss;

    @ApiModelProperty(value = "处理单位1姓名")
    private String treatmentOneName;

    @ApiModelProperty(value = "处理单位2姓名")
    private String treatmentTwoName;

    @ApiModelProperty(value = "责任裁决姓名")
    private String responsibilityName;

    @ApiModelProperty(value = "技术裁决姓名")
    private String technologyName;

    @ApiModelProperty(value = "质控工程师评审意见")
    private String qualityControlOpinion;

    @ApiModelProperty(value = "处理单位1的意见")
    private String unitTreatmentOneOpinion;

    @ApiModelProperty(value = "处理单位2的意见")
    private String unitTreatmentTwoOpinion;

    @ApiModelProperty(value = "裁决意见")
    private String responsibilityOpinion;

}
