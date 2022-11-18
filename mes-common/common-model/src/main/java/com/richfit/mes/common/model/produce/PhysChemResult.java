package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
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
public class PhysChemResult extends BaseEntity<PhysChemResult> {
    @ApiModelProperty(value = "报告号")
    private String reportNo;
    @ApiModelProperty(value = "组织机构")
    private String branchCode;
    @ApiModelProperty(value = "租户id")
    private String tenantId;
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
}
