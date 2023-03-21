package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * produce_track_head_mold
 *
 * @author Hou XinYu
 */
@Data
public class TrackHeadMold extends BaseEntity<TrackHeadMold> {

    /**
     * 模具类别(0=制新,1=返修)
     */
    @ApiModelProperty(value = "模具类别")
    private Integer moldType;

    /**
     * 模具材质(0=木质,1=气化模)
     */
    @ApiModelProperty(value = "模具材质")
    private Integer dieMaterial;

    /**
     * 实样数量
     */
    @ApiModelProperty(value = "实样数量")
    private Integer realSamplesQty;

    /**
     * 芯盒数量
     */
    @ApiModelProperty(value = "芯盒数量")
    private Integer coreBoxQty;

    /**
     * 浇道数量
     */
    @ApiModelProperty(value = "浇道数量")
    private Integer pouringGateQty;

    /**
     * 冒口数量
     */
    @ApiModelProperty(value = "冒口数量")
    private Integer feedHeadQty;

    /**
     * 型板数量
     */
    @ApiModelProperty(value = "型板数量")
    private Integer templateQty;

    /**
     * 胎垫数量
     */
    @ApiModelProperty(value = "胎垫数量")
    private Integer tiresQty;

    /**
     * 活块数量
     */
    @ApiModelProperty(value = "活块数量")
    private Integer dieInsertQty;

    /**
     * 冷铁数量
     */
    @ApiModelProperty(value = "冷铁数量")
    private Integer chillingBlockQty;

    /**
     * 刮板数量
     */
    @ApiModelProperty(value = "刮板数量")
    private Integer scraperQty;

    /**
     * 完工日期
     */
    @ApiModelProperty(value = "完工日期")
    private Date completionDate;

    /**
     * 模型版本
     */
    @ApiModelProperty(value = "模型版本")
    private String modelVersion;

    private String branchCode;

    private String tenantId;

    private static final long serialVersionUID = 1L;
}
