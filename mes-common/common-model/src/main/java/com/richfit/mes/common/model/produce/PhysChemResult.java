package com.richfit.mes.common.model.produce;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * 理化检验试验结果
 *
 * @author renzewen
 * @since 2022-9-20
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class PhysChemResult{
    @ApiModelProperty(value = "跟单工序id")
    private String id;
    @ApiModelProperty(value = "拉伸试验Rm")
    private String itemId;
    @ApiModelProperty(value = "拉伸试验Rel")
    private String rm;
    @ApiModelProperty(value = "拉伸试验A4")
    private String rel;
    @ApiModelProperty(value = "拉伸试验Z")
    private String a4;
    @ApiModelProperty(value = "金相分析->低倍")
    private String z;
    @ApiModelProperty(value = "冲击试验")
    private String impact;
    @ApiModelProperty(value = "冲击试验温度T(℃)")
    private String temp;
    @ApiModelProperty(value = "弯曲")
    private String bend;
    @ApiModelProperty(value = "硬度")
    private String hardness;
    @ApiModelProperty(value = "压扁")
    private String flattening;
    @ApiModelProperty(value = "化学成分C")
    private String c;
    @ApiModelProperty(value = "化学成分Si")
    private String si;
    @ApiModelProperty(value = "化学成分Mn")
    private String mn;
    @ApiModelProperty(value = "化学成分P")
    private String p;
    @ApiModelProperty(value = "化学成分S")
    private String s;
    @ApiModelProperty(value = "化学成分Cr")
    private String cr;
    @ApiModelProperty(value = "化学成分Mo")
    private String mo;
    @ApiModelProperty(value = "化学成分Ni")
    private String ni;
    @ApiModelProperty(value = "化学成分Cu")
    private String cu;
    @ApiModelProperty(value = "化学成分V")
    private String v;
    @ApiModelProperty(value = "化学成分Nb")
    private String nb;
    @ApiModelProperty(value = "化学成分Ti")
    private String ti;
    @ApiModelProperty(value = "备注")
    private String remark;
    private String branchCode;
    private String tenantId;

}
