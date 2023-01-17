package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * produce_disqualification_final_result
 *
 * @author
 */
@ApiModel(value = "generate.ProduceDisqualificationFinalResult")
@Data
public class DisqualificationFinalResult extends BaseEntity<DisqualificationFinalResult> {

    /**
     * 发现车间
     */
    @ApiModelProperty(value = "发现车间")
    private String discoverBranch;

    /**
     * 发现工序
     */
    @ApiModelProperty(value = "发现工序")
    private String discoverItem;

    /**
     * 总重量
     */
    @ApiModelProperty(value = "总重量")
    private Double totalWeight;

    /**
     * 责任单位内
     */
    @ApiModelProperty(value = "责任单位内")
    private String unitResponsibilityWithin;

    /**
     * 责任单位外
     */
    @ApiModelProperty(value = "责任单位外")
    private String unitResponsibilityOutside;

    /**
     * 处理单位1
     */
    @ApiModelProperty(value = "处理单位1")
    private String unitTreatmentOne;

    /**
     * 处理单位2
     */
    @ApiModelProperty(value = "处理单位2")
    private String unitTreatmentTwo;

    /**
     * 废品损失
     */
    @ApiModelProperty(value = "废品损失")
    private String abandonmentLoss;

    /**
     * 废品工时
     */
    @ApiModelProperty(value = "废品工时")
    private Double discardTime;

    /**
     * 回用工时
     */
    @ApiModelProperty(value = "回用工时")
    private Double reuseTime;

    /**
     * 让步接收数量
     */
    @ApiModelProperty(value = "让步接收数量")
    private Integer acceptDeviation;

    /**
     * 让步接收损失
     */
    @ApiModelProperty(value = "让步接收损失")
    private String acceptDeviationLoss;

    /**
     * 让步接收产品编号
     */
    @ApiModelProperty(value = "让步接收产品编号")
    private String acceptDeviationNo;

    /**
     * 返修合格数量
     */
    @ApiModelProperty(value = "返修合格数量")
    private Integer repairQualified;

    /**
     * 返修损失
     */
    @ApiModelProperty(value = "返修损失")
    private String repairLoss;

    /**
     * 返修后产品编号
     */
    @ApiModelProperty(value = "返修后产品编号")
    private String repairNo;

    /**
     * 返修结果
     */
    @ApiModelProperty(value = "返修结果")
    private String recapDemerits;

    /**
     * 返修描述
     */
    @ApiModelProperty(value = "返修描述")
    private String recapDescribe;

    /**
     * 返修检验员
     */
    @ApiModelProperty(value = "返修检验员")
    private String recapUser;

    /**
     * 返修时间
     */
    @ApiModelProperty(value = "返修时间")
    private Date recapTime;

    /**
     * 报废数量
     */
    @ApiModelProperty(value = "报废数量")
    private Integer scrap;

    /**
     * 报废损失
     */
    @ApiModelProperty(value = "报废损失")
    private String scrapLoss;

    /**
     * 报废后产品编号
     */
    @ApiModelProperty(value = "报废后产品编号")
    private String scrapNo;

    /**
     * 退货数量
     */
    @ApiModelProperty(value = "退货数量")
    private Integer salesReturn;

    /**
     * 退货损失
     */
    @ApiModelProperty(value = "退货损失")
    private String salesReturnLoss;

    /**
     * 退货产品编号
     */
    @ApiModelProperty(value = "退货产品编号")
    private String salesReturnNo;

    /**
     * 发现车间
     */
    @ApiModelProperty(value = "发现车间")
    private String discoverTenant;

    @ApiModelProperty(value = "质控意见")
    private String qualityControlOpinion;
    @ApiModelProperty(value = "质控姓名")
    private String qualityName;
    @ApiModelProperty(value = "质控时间")
    private Date qualityTime;

    @ApiModelProperty(value = "处理单位1意见")
    private String unitTreatmentOneOpinion;
    @ApiModelProperty(value = "处理单位1姓名")
    private String treatmentOneName;
    @ApiModelProperty(value = "处理单位1时间")
    private Date treatmentOneTime;

    @ApiModelProperty(value = "处理单位2意见")
    private String unitTreatmentTwoOpinion;
    @ApiModelProperty(value = "处理单位2姓名")
    private String treatmentTwoName;
    @ApiModelProperty(value = "处理单位2时间")
    private Date treatmentTwoTime;

    @ApiModelProperty(value = "技术裁决意见")
    private String technologyOpinion;
    @ApiModelProperty(value = "技术裁决姓名")
    private String technologyName;
    @ApiModelProperty(value = "技术裁决时间")
    private Date technologyTime;

    @ApiModelProperty(value = "责任裁决意见")
    private String responsibilityOpinion;
    @ApiModelProperty(value = "责任裁决姓名")
    private String responsibilityName;
    @ApiModelProperty(value = "责任裁决时间")
    private Date responsibilityTime;

    @ApiModelProperty(value = "不合格情况")
    private String disqualificationCondition;
    @ApiModelProperty(value = "不合格姓名")
    private String disqualificationName;
    @ApiModelProperty(value = "不合格时间")
    private Date disqualificationTime;
    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构")
    private String branchCode;

    /**
     * 所属租户
     */
    @ApiModelProperty(value = "所属租户")
    private String tenantId;


    private static final long serialVersionUID = 1L;
}
